package org.gamecontrolplus.gui;

import org.gamecontrolplus.ControlHat;

public class LCoolieHat extends LBaseInput {

	ControlHat button;

	protected float value = 0, multiplier = 1;
	protected float x, y;

	final boolean[] indicators = new boolean[4];
	final String[] itext = { "left", "right", "up", "down" };


	public LCoolieHat(LConfigUI ccw, ControlHat pbutton, float x, float y) {
		super(ccw, x, y, 1, 5);
		uiType = UI_COOLIEHAT;
		inputTypeName = "HAT";
		button = pbutton;
		name = button.getName();
		namePos = 20 + (int)(ccw.input_UI_length - 20 - app.textWidth(name)) / 2;
		backCol = UI_E_BACK[uiType];
		fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;
	}

	public float getMultiplier(){
		return multiplier;
	}

	public void setMultiplier(float m){
		button.setMultiplier(m);
	}
	
	@Override
	public void update(){
		value = button.getValue();
		x = button.getX();
		y = button.getY();
		indicators[0] = button.left();
		indicators[1] = button.right();
		indicators[2] = button.up();
		indicators[3] = button.down();
		multiplier = button.getMultiplier();
	}

	public void draw(){
		app.pushMatrix();
		app.translate(px, py);

		drawBackground();
		// Direction indicators
		app.noStroke();
		app.fill(0xFF90D090);
		app.rect(2, ccw.input_UI_height, 60, ccw.input_UI_height * 4 - 1);
		app.stroke(BORDER);
		app.strokeWeight(1);
		for(int i = 0; i < 4; i++){
			float iy = (i + 1) * ccw.input_UI_height;
			app.fill(indicators[i] ? PRESSED : RELEASED);
			app.ellipse(ccw.input_UI_height / 2, iy + ccw.input_UI_height / 2, ccw.indicator_d, ccw.indicator_d);
			app.fill(TEXTFILL);
			app.text(itext[i], 24, iy + fontBaseLine);
			switch(i){
			case 0:
				app.text("Hat Value:   " + value, 72, iy + fontBaseLine);
				break;
			case 1:
				app.text("Multiplier:  " + multiplier, 72, iy + fontBaseLine);
				break;
			case 2:
				app.text("X:   " + x, 72, iy + fontBaseLine);
				break;
			case 3:
				app.text("Y:   " + y, 72, iy + fontBaseLine);
				break;
			}
		}
		// Pressed ?
		app.stroke(BORDER);
		app.strokeWeight(1);
		app.fill(value != 0 ? PRESSED : RELEASED);
		app.ellipse(ccw.input_UI_height/2, ccw.input_UI_height/2, ccw.indicator_d, ccw.indicator_d);

		drawConnectors();
		drawControlHighlight();
		app.popMatrix();
	}

}
