package org.gamecontrolplus.gui;

import org.gamecontrolplus.ControlButton;
import org.gamecontrolplus.ControlHat;
import org.gamecontrolplus.ControlInput;
import org.gamecontrolplus.ControlSlider;

public abstract class LBaseInput extends LBase {


	public static LBaseInput makeInputUI(LConfigUI ccw, ControlInput input, float x, float y) {
		LBaseInput obj = null;
		if(input instanceof ControlHat)
			obj = new LCoolieHat(ccw, (ControlHat) input, x, y);
		else  if(input instanceof ControlButton)
			obj = new LButton(ccw, (ControlButton) input, x, y);
		else  if(input instanceof ControlSlider)
			obj = new LSlider(ccw, (ControlSlider) input, x, y);
		return obj;
	}

	int namePos;
	// BUTTON, HAT, SLIDER
	String inputTypeName = "";

	
	public float getValue(){
		return 0;
	}

	public float getMultiplier(){
		return 0;
	}
	
	public void setTolerance(float t){
	}

	public void setMultiplier(float m){
	}

	public float getTolerance(){
		return 0;
	}

	public LBaseInput(LConfigUI ccw, float x, float y, int nbr_connects, int nbrLines){
		super(ccw, x, y, nbr_connects);
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn]= new LConnector(app, this, cn, 
					px - ccw.connector_size_r, // x
					py + (cn + 0/5f) * ccw.input_UI_height + ccw.connector_size_r, //y
					ccw.connector_size_d); // size
		UI_HEIGHT = nbrLines * ccw.input_UI_height;
		fontBaseLine = (ccw.input_UI_height + ccw.fontSize) / 2;
	}

	protected void drawBackground(){
		// Background
		app.noStroke();
		app.fill(backCol);
		app.rect(0, 0, ccw.input_UI_length,  UI_HEIGHT);
		app.fill(TEXTFILL);
		app.text(name, namePos, fontBaseLine);
	}

	protected void drawConnectors(){
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn].draw(cn * ccw.input_UI_height);
	}

	protected void drawControlHighlight(){
		// Highlighter when mouse over
		app.stroke(isOver ? HIGHLIGHT : BORDER);
		app.strokeWeight(isOver ? 4 : 1);
		app.noFill();
		app.rect(0, 0, ccw.input_UI_length,  UI_HEIGHT);
	}

}
