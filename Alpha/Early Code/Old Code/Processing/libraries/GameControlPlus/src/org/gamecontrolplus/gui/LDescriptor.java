package org.gamecontrolplus.gui;

import org.gamecontrolplus.Configuration.InputConfig;


public class LDescriptor extends LBase {


	InputConfig iconfig;
	LBaseInput source = null;
	int conID = 0;
	
	public LDescriptor(LConfigUI ccw, float x, float y, InputConfig iconfig) {
		super(ccw, x ,y, 1);
		this.iconfig = iconfig;
		name = iconfig.key;
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn]= new LConnector(app, this, cn, 
					px + ccw.desc_UI_length + ccw.connector_size_r, // x
					py + (cn + 0/5f) * ccw.desc_UI_height + ccw.connector_size_r, //y
					ccw.connector_size_d); // size

		uiType = UI_DESCRIPTOR;
		UI_HEIGHT = ccw.desc_UI_height;
		backCol = UI_E_BACK[iconfig.type];
		fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;

	}


	public void draw(){
		app.pushMatrix();
//		app.pushStyle();
		app.translate(px, py);
		// Descriptor area
		app.noStroke();
		app.fill(backCol );
		app.rect(0, 0, ccw.desc_UI_length, UI_HEIGHT);

		// Text
		app.fill(0);
		app.text(iconfig.description, 10, fontBaseLine);
		
		// Highlight
		app.stroke(isOver ? HIGHLIGHT : BORDER);
		app.strokeWeight(isOver ? 4 : 1);
		app.noFill();
		app.rect(0, 0, ccw.desc_UI_length,  UI_HEIGHT);

		// Connectors 
//		app.pushMatrix();
		app.translate(ccw.desc_UI_length,0);
		drawConnectors();
//		app.popMatrix();

		app.popMatrix();
	}

	protected void drawConnectors(){
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn].draw(cn * ccw.desc_UI_height);
	}

//	public String getName(){
//		return txfKey.getText();
//	}

}
