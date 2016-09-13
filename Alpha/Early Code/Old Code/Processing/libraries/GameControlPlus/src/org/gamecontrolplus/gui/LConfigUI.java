package org.gamecontrolplus.gui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlButton;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlHat;
import org.gamecontrolplus.ControlIO;
import org.gamecontrolplus.ControlInput;
import org.gamecontrolplus.ControlSlider;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class LConfigUI implements PConstants, LConstants {


	private final ControlDevice device;

	final ControlIO controlIO;

	final PApplet app;
	
	final Configuration config;

	private boolean active = false;

	List<LBase> uiElements = new ArrayList<LBase>();
	List<LConnector> uiConnections = new ArrayList<LConnector>();

	private Map<String, LBaseInput> devInpKeys = new HashMap<String, LBaseInput>();
	private Map<String, LDescriptor> descriptors = new HashMap<String, LDescriptor>();

	private StringBuffer report;
	private int errCount = 0;


	private void addConfigToGUI(float spaceNeeded, float spaceForDescs, float spaceForInputs){
		float px, py;
		// Create and add device inputs to UI 
		px = window.papplet.width - 10 - INPUT_UI_LENGTH - PANEL_WIDTH;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForInputs) / 2; 

		for(ControlInput input : device.getInputs()){
			LBaseInput ui = LBaseInput.makeInputUI(this, input, px, py);
			if(ui != null){
				uiElements.add(ui);
				py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
				devInpKeys.put(ui.name, ui);
			}
		}
		// Create and add descriptors to UI 
		px = 10;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForDescs) / 2; 
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			LDescriptor ui = new LDescriptor(this, px, py, iconfig);
			uiElements.add(ui);
			descriptors.put(ui.name, ui);
			py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
		}

		// Now create list of connectors
		for(LBase ui : uiElements)
			for(LConnector c : ui.connectors)
				uiConnections.add(c);
	}

	/**
	 * Make existing connections between game inputs and device inputs based on type and name.
	 */
	private void makeExistingConnections(){
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			LBaseInput di = devInpKeys.get(iconfig.deviceInputName);
			if(di != null && iconfig.type == di.uiType && iconfig.inputConNo < di.getNbrOfConnectors()){
				LDescriptor descUI = descriptors.get(iconfig.key);
				descUI.connectors[0].conTo = di.connectors[iconfig.inputConNo];
				di.connectors[iconfig.inputConNo].conTo = descUI.connectors[0];
				di.setMultiplier(iconfig.multiplier);
				di.setTolerance(iconfig.tolerance);
			}
		}
	}
	
	private void addToReport(String line, boolean isError){
		report.append(line);
		if(isError) errCount++;
	}

	/**
	 * Verify the configuration
	 * @param chain
	 * @return
	 */
	private boolean verifyConfig(boolean chain){
		report = new StringBuffer();
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			LDescriptor descUI = descriptors.get(iconfig.key);
			LConnector con = descUI.connectors[0].conTo;
			if(con != null){
				iconfig.deviceInputName = con.owner.name;
				iconfig.inputConNo = con.conNo;
				iconfig.multiplier = ((LBaseInput)con.owner).getMultiplier();
				iconfig.tolerance = ((LBaseInput)con.owner).getTolerance();
			}
			else {
				addToReport("No input assigned to: " + descUI.name + "\n", true);
			}	
		}
		if(errCount > 0)
			addToReport("VERIFY - " + errCount + " errors found\n", false);
		else
			addToReport("VERIFY - successful\n", false);
		if(!chain)
			txaStatus.setText(report.toString());
		return errCount == 0;
	}

	private boolean saveConfig(){
		if(!verifyConfig(true)){
			addToReport("SAVE - abandoned\n", false);
			txaStatus.setText(report.toString());
			return false;
		}
		Configuration.saveConfiguration(app, config);
		return true;
	}

//	private String[] makeConfigLines() {
//		String[] data = new String[config.gameInputs.length + 1];
//		data[0] = config.usage;
//		int index = 1;
//		for(Configuration.InputConfig iconfig : config.gameInputs){
//			System.out.println("   " + iconfig.toString());
//			data[index++] = iconfig.toString();
//		}
//		return data;
//	}

	public void verify_click(MButton button, MEvent event) { 
		verifyConfig(false);
	}


	public void use_device_click(MButton button, MEvent event) { 
		if(saveConfig()){
			System.out.println("Saving config");
			device.matches(config);
			controlIO.finishedConfig(device);
			window.forceClose();
		}
	}

	public void quit_click(MButton button, MEvent event) { 
		controlIO.finishedConfig(null);
		window.forceClose();
	}


	public void clear_click(MButton button, MEvent event) {
		txaStatus.setText("");
	}

	synchronized public void pre(MWinApplet appc, MWinData data) {
		current = null;
		for(LBase ui : uiElements){
			ui.update();
			ui.overWhat(appc.mouseX, appc.mouseY);
		}
		if(!dragging && current != null && current.conTo != null){
			current.conTo.isOver = true;
		}
	}

	synchronized public void mouse(MWinApplet appc, MWinData data, MouseEvent mevent) {
		switch(mevent.getAction()){
		case MouseEvent.PRESS:
			if(current != null){
				start = current;
				dragging = true;
			}
			break;
		case MouseEvent.RELEASE:
			if(current != null && start != null && current.type != start.type){
				LConnector descCon = (current.type == DESC) ? current : start;
				LDescriptor descUI = ((LDescriptor)descCon.owner);
				LConnector inputCon = (start.type == INPUT) ? start : current;
				LBaseInput inputUI = ((LBaseInput)inputCon.owner);
				// Make sure the device input is the right type for the descriptor
				int type0 = ((LDescriptor)descCon.owner).iconfig.type;
				int type1 = inputCon.owner.uiType;
				if(type0 == type1){
					// Remove any existing connection
					end = current;
					current = null;
					if(start.conTo != null)
						start.conTo.conTo = null;
					if(end.conTo != null)
						end.conTo.conTo = null;
					// Add new connection
					start.conTo = end;
					end.conTo = start;
					descUI.iconfig.deviceInputName = inputUI.name;		// Not sure if needed look at makeConfigLines
   					inputUI.setMultiplier(descUI.iconfig.multiplier);
					inputUI.setTolerance(descUI.iconfig.tolerance);
				}
			}
			current = start = null;
			dragging = false;
			break;
		case MouseEvent.DRAG:

			break;
		}
	}

	synchronized public void draw(MWinApplet appc, MWinData data) {
		appc.background(BACKGROUND);
		if(!active) return;
		// Draw control panel at bottom
		appc.noStroke();
		appc.fill(PANEL);;
		appc.rect(appc.width - PANEL_WIDTH, 0, PANEL_WIDTH, appc.height);
		// Draw connections
		appc.strokeWeight(3.5f);
		for(LConnector c : uiConnections){
			if(c.conTo != null && c.type == LConnector.DESC){
				appc.stroke(c.isOver ? HIGHLIGHT : CONNECTION);
				appc.line(c.px,  c.py,  c.conTo.px,  c.conTo.py);
			}
		}
		// Connection in the making
		if(dragging && start != null){
			appc.stroke(CONNECTION);
			appc.line(start.px, start.py, appc.mouseX, appc.mouseY);
		}
		// Draw descriptors and inputs
		for(LBase ui : uiElements)
			ui.draw();
	}

//	public void printDevice(int id,  ControlDevice device){
//		System.out.println("========================================================================");
//		System.out.println("Device number  " + id + " is called '" + device.getName() + "' and has");
//		System.out.println("\t" + device.getNumberOfButtons() + " buttons");
//		System.out.println("\t" + device.getNumberOfSliders() + " sliders");
//		System.out.println("\t" + device.getNumberOfRumblers() + " rumblers");
//		device.printButtons();
//		device.printSliders();
//		System.out.println("------------------------------------------------------------------------\n\n");		
//	}

	// Widow GUI stuff
	MWindow window;
	MTabManager tabManager = new MTabManager();
	MTextField txfFilename;
	MTextArea txaStatus;

	public LConfigUI(PApplet papp, LSelectEntry entry){
		float px, py, pw;
		app = papp;
		device = entry.device;
		entry.device.open();
		controlIO = entry.controlIO;
		this.config = LSelectUI.config;
		float spaceForInputs = ELEMENT_UI_GAP;

		// Scan through controls to calculate the window height needed
		for(ControlInput input : device.getInputs()){
			if(input instanceof ControlHat){
				spaceForInputs += 5 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlButton){
				spaceForInputs += INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlSlider){
				spaceForInputs += 4 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else
				System.out.println("Unknown input " + input);	
		}
		float spaceForDescs = config.nbrInputs() * (DESC_UI_HEIGHT + ELEMENT_UI_GAP + 2);
		float spaceNeeded = Math.max(spaceForInputs, spaceForDescs);
		spaceNeeded = Math.max(spaceNeeded, PANEL_HEIGHT);
		// Now calculate window scaling and height
		if(papp.displayHeight < spaceNeeded + 40)
			scale = papp.displayHeight / (spaceNeeded + 40);	
		else
			scale = 1.0f;
		int winHeight = Math.round(spaceNeeded  * scale);

		// Apply scaling
		input_UI_height = INPUT_UI_HEIGHT * scale;
		desc_UI_height = DESC_UI_HEIGHT * scale;
		element_UI_gap  = ELEMENT_UI_GAP * scale;
		input_UI_length = INPUT_UI_LENGTH;
		desc_UI_length = DESC_UI_LENGTH;
		textfield_gap = TEXTFIELD_GAP * scale;
		indicator_d = INICATOR_D * scale;
		connector_size_r = CONNECTOR_SIZE_R * scale;		
		connector_size_d = 2 * connector_size_r;
		fontSize = FONT_SIZE * scale;
		font = new Font("Dialog", Font.PLAIN, (int)fontSize);

		// CREATE THE WINDOW
		String title = "'" + device.getName() + "'  [" + device.getTypeName() + " on " + device.getPortTypeName() + "]"; 
		window = new MWindow(papp, title, 80, 100, 1020, winHeight, false, M4P.JAVA2D);
		window.setResizable(false);
		window.addDrawHandler(this, "draw");
		window.addMouseHandler(this, "mouse");
		window.addPreHandler(this, "pre");
		window.papplet.noLoop();
		tabManager = new MTabManager();
		M4P.setCursor(CROSS, window);	

		// Create the control panel
		px = window.papplet.width - PANEL_WIDTH + 10;
		pw = PANEL_WIDTH - 20;
		py = 10;
		MLabel lblFilenamePrompt = new MLabel(window.papplet, px, py, pw, 20, "Config. for: " + config.getUsgae());
		lblFilenamePrompt.setTextAlign(MAlign.LEFT, null);
		lblFilenamePrompt.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblFilenamePrompt.setTextBold();
		lblFilenamePrompt.setOpaque(true);
		py += 26;
		float bw = (pw - 20)/3;
		MButton btnClearStatus = new MButton(window.papplet, px, py, bw, 20);
		btnClearStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnClearStatus.setText("Clear Status");
		btnClearStatus.addEventHandler(this, "clear_click");
		MButton btnVerify = new MButton(window.papplet, px + (pw - bw)/2, py, bw, 20);
		btnVerify.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnVerify.setText("Verify");
		btnVerify.addEventHandler(this, "verify_click");
		MButton btnSave = new MButton(window.papplet, px + pw - bw, py, bw, 20);
		btnSave.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnSave.setText("USE");
		btnSave.addEventHandler(this, "use_device_click");
		py += 26;
		MLabel lblStatus = new MLabel(window.papplet, px, py, pw, 20, "VERIFY / SAVE STATUS REPORT");
		lblStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblStatus.setTextBold();
		lblStatus.setOpaque(true);
		py += 22;
		txaStatus = new MTextArea(window.papplet, px, py, pw, 140, M4P.SCROLLBARS_VERTICAL_ONLY);
		txaStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		txaStatus.setDefaultText("Verify / save status report");
		py += txaStatus.getHeight() + 4;
		MButton btnQuit = new MButton(window.papplet, px, py, pw, 20);
		btnQuit.setLocalColorScheme(M4P.RED_SCHEME);
		btnQuit.setText("CANCEL CONFIGURATION AND EXIT");
		btnQuit.addEventHandler(this, "quit_click");

		window.papplet.textSize(fontSize);

		addConfigToGUI(spaceNeeded, spaceForDescs, spaceForInputs);
		makeExistingConnections();

		active = true;
		window.papplet.loop();
	}

	void close(){
		window.forceClose();
	}


	LConnector start = null;
	LConnector end = null;
	LConnector current = null;

	final float scale;
	final float input_UI_height;
	final float desc_UI_height;
	final float element_UI_gap;
	final float input_UI_length;
	final float desc_UI_length;
	final float textfield_gap;
	final float indicator_d;
	final float connector_size_r;  // radius
	final float connector_size_d;  // diameter
	final float fontSize;
	final Font font;

	private boolean dragging = false;

}
