/*
 * Part of the ProControl Plus library - http://www.lagers.org.uk/procontrol
 * 
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * 
 * ---------------------------------------------------------------------------------
 * Updated and enhanced from the proCONTROLL library [http://texone.org/procontrol], 
 * copyright (c) 2005 Christian Riekoff which was released under the terms of the GNU 
 * Lesser General Public License (version 2.1 or later) as published by the Free 
 * Software Foundation.
 * ---------------------------------------------------------------------------------
 */

package org.gamecontrolplus;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import net.java.games.input.Component;

/**
 * This class represents a button of a device. You can use the pressed() 
 * method to see if a button is pressed or use the plug method to 
 * handle events.
 * 
 * @author Peter Lager & Christian Riekoff
 */
public class ControlButton extends ControlInput{
	
	private boolean pressed = false;
	private boolean oldPressed = false;
	
	/**
	 * Instance to the PApplet where ProControl Plus is running
	 */
	private final PApplet parent;
		
	/**
	 * Initializes a new Button.
	 * @param i_component
	 */
	ControlButton(final Component i_component, final PApplet i_parent){
		super(i_component);
		parent = i_parent;
		inputType = BUTTON_TYPE;
	}
	
	/**
	 * This method is called before each frame to update the button state.
	 */
	void update(){
		actualValue = component.getPollData()*8;
		pressed = actualValue>0f;
		if(pressed && oldPressed){
			callPlugs(whilePressPlugs);
		}else if(pressed && !oldPressed){
			callPlugs(onPressPlugs);
		}else if(!pressed && oldPressed){
			callPlugs(onReleasePlugs);
		}	
		oldPressed = pressed;
	}
	
	/**
	 * This method returns true if the button was pressed. 
	 * @return boolean, true if the button was pressed
	 * @usage application
	 * @related ControllButton
	 */
	public boolean pressed(){
		return pressed;
	}
	
	protected final List<Plug> onPressPlugs = new ArrayList<Plug>();
	protected final List<Plug> onReleasePlugs = new ArrayList<Plug>();
	protected final List<Plug> whilePressPlugs = new ArrayList<Plug>();
	
	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a button the method name and the event type you want to react on.
	 * If your method is inside a class you have to give the plug a reference to it.
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 */
	public void plug(
		final Object i_object, 
		final String i_methodName,
		final int i_eventType
	){
		List<Plug> plugList;
		Plug plug = new Plug(i_object, i_methodName);
		switch(i_eventType){
			case ControlIO.ON_PRESS:
				plugList = onPressPlugs;
				break;
			case ControlIO.ON_RELEASE:
				plugList = onReleasePlugs;
				break;	
			case ControlIO.WHILE_PRESS:
				plugList = whilePressPlugs;
				break;
			default:
				throw new RuntimeException("Error on plug "+i_methodName+" check the given event type");
		}
		plugList.add(plug);
	}
	
	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a button the method name and the event type you want to react on.
	 * If your method is inside a class you have to give the plug a reference to it.
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 */
	public void plug(
		final String i_methodName,
		final int i_eventType
	){
		plug(parent, i_methodName, i_eventType);
	}
	
	/**
	 * Call all plugs for this button
	 * @param i_plugList
	 */
	protected void callPlugs(final List<Plug> i_plugList){
		for(int i = 0; i < i_plugList.size();i++){
			Plug plug = i_plugList.get(i);
			plug.call();
		}
	}
	
	/**
	 * Get a text description for this button
	 * @param tab
	 */
	public String toText(String tab){
		String name = (actualName.length() > 20 ) ?
				actualName.substring(0, 17) + "..." : (actualName + "                    ").substring(0,  20);
		return tab + "button    " + name + "-";
	}
}
