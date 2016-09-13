package org.gamecontrolplus.gui;

import java.util.Collections;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix3D;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * DO NOT ATTEMPT TO USE THIS CLASS <br>
 * 
 * Although this class and many of its methods are declared public this is to make 
 * them available through Reflection and means that they should only be used inside
 *  the library code. <br> 
 * 
 * This class is used to remember information about a particular applet (i.e. window)
 * and is responsible for forwarding events passed to it from Processing. <br>
 * 
 * It remembers the original transformation matrix to simplify working with 3D renderers
 * and libraries such as PeasyCam.
 * 
 * @author Peter Lager
 *
 */
public class MWindowInfo implements PConstants, MConstants, MConstantsInternal {

	public PApplet app;
	public boolean app_g_3d;
	public PMatrix orgMatrix;
	
	public LinkedList<MAbstractControl> windowControls = new LinkedList<MAbstractControl>();
	// These next two lists are for controls that are to be added or remove since these
	// actions must be performed outside the draw cycle to avoid concurrent modification
	// exceptions when changing windowControls
	public LinkedList<MAbstractControl> toRemove = new LinkedList<MAbstractControl>();
	public LinkedList<MAbstractControl> toAdd = new LinkedList<MAbstractControl>();
	
	// Set this to true if papplet is a GWinApplet objects i.e. part of a 
	// Gwindow object
	boolean isGWindow;
	
	
	/**
	 * Create an applet info object
	 * @param papplet
	 */
	public MWindowInfo (PApplet papplet) {
		app = papplet;
		// Is this applet part of a GWindow object
		isGWindow = (app instanceof MWinApplet);
		app_g_3d = app.g.is3D();
		if(app.g.is3D())
			orgMatrix = papplet.getMatrix((PMatrix3D)null);
//		else
//			orgMatrix = papplet.getMatrix((PMatrix2D)null);
		
		/*
		 * The WinInfo object is responsible for capturing events from Processing
		 * and passing them onto the GWindow objects and the controls.
		 */
		app.registerMethod("draw",this);
		app.registerMethod("mouseEvent",this);
		app.registerMethod("keyEvent",this);
		app.registerMethod("pre",this);
		app.registerMethod("post",this);
	}

	/**
	 * The draw method registered with Processing
	 */
	public void draw(){
		app.pushMatrix();
		if(app_g_3d) {
			app.hint(PConstants.DISABLE_DEPTH_TEST);
			// Load the identity matrix.
			app.resetMatrix();
			// Apply the original Processing transformation matrix.
			app.applyMatrix(orgMatrix);
		}
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD )
				control.draw();
		}		
		if(app_g_3d)
			app.hint(PConstants.ENABLE_DEPTH_TEST);
		app.popMatrix();
	}

	/**
	 * The mouse method registered with Processing
	 * 
	 * If this is a secondary window then it should call the draw method for the window.
	 * 
	 * Should call the user defined method for the  the draw method for the actual GWindow object.
	 * 
	 * @param event
	 */
	public void mouseEvent(MouseEvent event){
		if(isGWindow)
			((MWinApplet)app).mouseEvent(event);
		for(MAbstractControl control : windowControls){
			if((control.registeredMethods & MOUSE_METHOD) == MOUSE_METHOD)
				control.mouseEvent(event);
		}
	}

	/**
	 * The key method registered with Processing
	 */	
	public void keyEvent(KeyEvent event) {
		if(isGWindow)
			((MWinApplet)app).keyEvent(event);
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & KEY_METHOD) == KEY_METHOD)
				control.keyEvent(event);
		}			
	}

	/**
	 * The pre method registered with Processing
	 */
	public void pre(){
		if(MAbstractControl.controlToTakeFocus != null && MAbstractControl.controlToTakeFocus.getPApplet() == app){
			MAbstractControl.controlToTakeFocus.setFocus(true);
			MAbstractControl.controlToTakeFocus = null;
		}
		if(isGWindow)
			((MWinApplet)app).pre();
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & PRE_METHOD) == PRE_METHOD)
				control.pre();
		}
	}

	/**
	 * The post method registered with Processing
	 */
	public void post(){
		if(M4P.cursorChangeEnabled){
			if(MAbstractControl.cursorIsOver != null ) //&& GAbstractControl.cursorIsOver.getPApplet() == app)
				app.cursor(MAbstractControl.cursorIsOver.cursorOver);			
			else 
				app.cursor(M4P.mouseOff);
		}
		if(isGWindow)
			((MWinApplet)app).post();
		for(MAbstractControl control : windowControls){
			if( (control.registeredMethods & POST_METHOD) == POST_METHOD)
				control.post();
		}
		// =====================================================================================================
		// =====================================================================================================
		//  This is where components are removed or added to the window to avoid concurrent access violations 
		// =====================================================================================================
		// =====================================================================================================
		synchronized (this) {
		// Dispose of any unwanted controls
			if(!toRemove.isEmpty()){
				for(MAbstractControl control : toRemove){
					// If the control has focus then lose it
					if(MAbstractControl.focusIsWith == control)
						control.loseFocus(null);
					// Clear control resources
					control.buffer = null;
					if(control.parent != null){
						control.parent.children.remove(control);
						control.parent = null;
					}
					if(control.children != null)
						control.children.clear();
					control.palette = null;
					control.jpalette = null;
					control.eventHandlerObject = null;
					control.eventHandlerMethod = null;
					control.winApp = null;
					windowControls.remove(control);
					System.gc();			
				}
				toRemove.clear();
			}
			if(!toAdd.isEmpty()){
				for(MAbstractControl control : toAdd)
					windowControls.add(control);
				toAdd.clear();
				Collections.sort(windowControls, M4P.zorder);
			}
		}
	}

	/**
	 * Dispose of this WIndow. <br>
	 * First unregister for event handling then clear list of controls
	 * for this window.
	 */
	void dispose(){
		app.noLoop();
		app.unregisterMethod("draw", this);
		app.unregisterMethod("pre", this);
		app.unregisterMethod("post", this);
		app.unregisterMethod("mouseEvent",this);
		app.unregisterMethod("keyEvent",this);
		windowControls.clear();
	}
	
	/**
	 * If a control is to be added to this window then add the control
	 * to the toAdd list. The control will actually be added during the 
	 * post() method
	 * @param control
	 */
	synchronized void addControl(MAbstractControl control){
		// Make sure we avoid duplicates
		if(!windowControls.contains(control) && !toAdd.contains(control))
			toAdd.add(control);
	}

	/**
	 * If a control is to be removed to this window then add the control
	 * to the toAdd list. The control will actually be added during the 
	 * post() method
	 * @param control
	 */
	synchronized void removeControl(MAbstractControl control){
		// Make sure we avoid duplicates
		if(windowControls.contains(control) && !toRemove.contains(control))
			toRemove.add(control);
	}

	void setColorScheme(int cs){
		for(MAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}

	void setAlpha(int alpha){
		for(MAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}


		
}