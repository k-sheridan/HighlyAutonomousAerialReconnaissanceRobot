package org.gamecontrolplus.gui;

import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlIO;

import processing.core.PApplet;

/**
 * This class represents a single entry in the device selection screen.
 * @author peter
 *
 */
public class LSelectEntry implements Comparable<LSelectEntry> {

	public final PApplet app;
	public final ControlIO controlIO;
	public final ControlDevice device;
	// GUI stuff
	public final MLabel displayName;
	public final MButton btnGoConfig;
	public LConfigUI winCofig = null;
	LSelectUI selWindow;
	
	public LSelectEntry(LSelectUI selWindow, ControlIO controlIO, ControlDevice dev){
		this.selWindow = selWindow;
		this.app = selWindow.window.papplet;
		this.controlIO = controlIO;
		this.device = dev;
		displayName = new MLabel(app, 36, 20, app.width-36, 20);
		displayName.setText(device.getName() + "  [" + device.getTypeName() + "]");
		displayName.setTextAlign(MAlign.LEFT, null);
		btnGoConfig = new MButton(app, 4, 24, 24, 14);
		btnGoConfig.addEventHandler(this, "configClick");				
	}

	public void setIndex(int index){
		displayName.moveTo(36, 20 + index * 20);
		if(btnGoConfig != null) btnGoConfig.moveTo(4, 24 + index * 20);
	}

	public void configClick(MButton source, MEvent event) {
		if(winCofig == null) {
			winCofig = new LConfigUI(app, this);
			selWindow.finishedWith();     
		}
	}

	@Override
	public int compareTo(LSelectEntry entry) {
		return device.compareTo(entry.device);
	}
}
