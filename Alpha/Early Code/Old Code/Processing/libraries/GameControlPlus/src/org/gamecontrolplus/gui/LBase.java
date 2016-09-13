package org.gamecontrolplus.gui;

import processing.core.PApplet;
import processing.core.PConstants;

public abstract class LBase implements  PConstants, LConstants {

	protected LConfigUI ccw;
	protected final PApplet app;

	String name;

	// UI_BUTTON, UI_COOLIEHAT, UI_SLIDER or UI_DESCRIPTOR	
	int uiType;
	
	final float px, py;
	
	float UI_HEIGHT;
	float fontBaseLine;
	int backCol;

	final LConnector[] connectors;
	
	boolean isOver = false;
	
	LBase(LConfigUI ccw, float x, float y, int nbr_connects){
		this.ccw = ccw;
		app = ccw.window.papplet;
		UI_HEIGHT = ccw.desc_UI_height;
		px = x;
		py = y;
		connectors = new LConnector[nbr_connects];
	}
	
	public int getNbrOfConnectors(){
		return connectors.length;
	}
	
	protected abstract void drawConnectors();

	public void update(){ }
	
	public void overWhat(float mx, float my){
		for(int cn = 0; cn < connectors.length; cn++){
			isOver = connectors[cn].isOver(ccw, mx , my);
			if(isOver) {
				break;
			}
		}
	}
	
	public abstract void draw();

}
