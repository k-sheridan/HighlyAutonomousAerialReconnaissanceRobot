package org.gamecontrolplus.gui;

import java.util.ArrayList;
import java.util.List;

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlIO;

import processing.core.PApplet;

/**
 * This creates the device select entry window.
 * @author peter
 *
 */
public class LSelectUI {

	static Configuration config;
	
	MWindow window;
	
	PApplet app;
	ControlIO controlIO;

	List<LSelectEntry> deviceEntries =  new ArrayList<LSelectEntry>();
	
	
	public LSelectUI(PApplet app, Configuration config){
		M4P.messagesEnabled(false);
		this.app = app;
		LSelectUI.config = config;
		this.controlIO = ControlIO.getInstance(app);
		List<ControlDevice> devices = controlIO.getDevices();

		String title = "Select device for " + LSelectUI.config.getUsgae();
		window = new MWindow(app, title, 80, 40, 500, 20*(devices.size() + 3), false, PApplet.JAVA2D);
		window.setResizable(false);
		window.addDrawHandler(this, "draw");
		
		// Add entries for devices added
		for(ControlDevice d : devices){
			if(d.available && !d.getTypeName().equalsIgnoreCase("keyboard"))
				deviceEntries.add(new LSelectEntry(this, controlIO, d));
		}

		createSelectionInterface(window.papplet);
		for(int i = 0; i < deviceEntries.size(); i++)
			deviceEntries.get(i).setIndex(i);
	}
	
	public void finishedWith(){
		window.forceClose();
	}
	
	public void createSelectionInterface(PApplet wapp){
		MLabel lblControls = new MLabel(wapp, 0, 0, wapp.width, 20);
		lblControls.setText("Control devices");
		lblControls.setOpaque(true);
		lblControls.setTextBold();
		MButton btnExit = new MButton(wapp, wapp.width - 105, wapp.height-28, 100, 24);
		btnExit.setText("Exit Game");
		btnExit.addEventHandler(this, "exitClick");
	}

	public void exitClick(MButton source, MEvent event) {
		ControlIO.configurating = false;
		ControlIO.configuredDevice = null;
		window.forceClose();
	}

	synchronized public void draw(MWinApplet appc, MWinData data) {
		appc.background(255, 255, 220);
		appc.stroke(230, 230, 200);
		appc.fill(240, 240, 210);
		int y =0;
		while(y < appc.height){
			appc.rect(0,y,appc.width,20);
			y += 40;
		}
		appc.fill(200,255,200);
		appc.rect(0,appc.height-30,appc.width,30);
	}
}
