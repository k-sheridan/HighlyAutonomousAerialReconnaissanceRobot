import g4p_controls.G4P;
import g4p_controls.GAlign;
import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import g4p_controls.GTabManager;
import g4p_controls.GTextArea;
import g4p_controls.GTextField;
import g4p_controls.GWinApplet;
import g4p_controls.GWinData;
import g4p_controls.GWindow;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.gamecontrolplus.ControlButton;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlHat;
import org.gamecontrolplus.ControlIO;
import org.gamecontrolplus.ControlInput;
import org.gamecontrolplus.ControlSlider;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;


public class TConfigUI implements PConstants, TConstants {

  static int nbrWindows = 0;

  public static String pathToSketch = "";
  //	public static String nameOfSketch = "";
  public static String fileSeparator = System.getProperty("file.separator");

  private final ControlDevice device;

  final ControlIO controlIO;

  private boolean active = false;

  List<TBase> uiElements = new ArrayList<TBase>();
  List<TConnector> uiConnections = new ArrayList<TConnector>();

  TConnector start = null;
  TConnector end = null;
  TConnector current = null;

  float scale;
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

  private StringBuffer report;
  List<TConnector> configConnections = new ArrayList<TConnector>();
  private Set<String> keys = new TreeSet<String>();
  private int errCount = 0;

  private boolean dragging = false;

  private void addToReport(String line, boolean isError) {
    report.append(line);
    if (isError) errCount++;
  }

  /**
   	 * This method will check for any errors / omissions in all
   	 * used descriptors.
   	 */
  private void validateDescriptors() {
    // Create a list of used descriptors
    for (TConnector ui : uiConnections) {
      if (ui.type == TConnector.DESC && ui.conTo != null) {
        configConnections.add(ui);
      }
    }
    if (configConnections.size() == 0) {
      addToReport("Empty configuration!\n", true);
      return;
    }
    // We only get here if we have some connections configured
    for (TConnector ui : configConnections) {
      TDescriptor descUI = (TDescriptor)ui.owner;
      String inputName = ((TBaseInput)ui.conTo.owner).name;
      String desc = descUI.txfDescription.getText();
      String key = descUI.txfKey.getText();
      if (desc.length() == 0)
        addToReport("No description for input: " + inputName + "\n", true);
      if (key.length() == 0)
        addToReport("No key for input: " + inputName + "\n", true);
      else if (keys.contains(key))
        addToReport("Duplicate key '" + key + "' on input: " + inputName + "\n", true);
      keys.add(key);
    }
  }

  /**
   	 * Verify the configuration
   	 * @param chain
   	 * @return
   	 */
  private boolean verifyConfig(boolean chain) {
    configConnections.clear();
    keys.clear();
    report = new StringBuffer();
    errCount = 0;

    if (txfDeviceUsage.getText().length() == 0)
      addToReport("No description of device role provided\n", true);

    validateDescriptors();

    if (errCount > 0)
      addToReport("VERIFY - " + errCount + " errors found\n", false);
    else
      addToReport("VERIFY - successful\n", false);
    if (!chain)
      txaStatus.setText(report.toString());
    return errCount == 0;
  }

  private void saveConfig() {
    if (!verifyConfig(true)) {
      addToReport("SAVE - abandoned\n", false);
      txaStatus.setText(report.toString());
      return;
    }
    String filename = txfFilename.getText();
    if (filename.length() == 0) {
      addToReport("Name for configuration file required\n", true);
    }
    else {
      //==================================================================================================
      // Will eventually need to use sketch data path
      //==================================================================================================
      File file = new File(pathToSketch + fileSeparator +"data" + fileSeparator + filename);
      String[] lines = makeConfigLines();
      PApplet.saveStrings(file, lines);
    }
    if (errCount > 0)
      addToReport("SAVE - failed\n", false);
    else
      addToReport("SAVE - successful", false);
    txaStatus.setText(report.toString());
  }

  private String[] makeConfigLines() {
    String[] data = new String[configConnections.size() + 1];
    data[0] = txfDeviceUsage.getText();
    int index = 1;
    for (TConnector ui : configConnections) {
      TDescriptor descUI = (TDescriptor)ui.owner;
      TBaseInput inputUI = (TBaseInput)ui.conTo.owner;
      int inputConNo = ui.conTo.conNo;
      String desc = descUI.txfDescription.getText();
      String key = descUI.txfKey.getText();
      String inputName = inputUI.name;
      int typeID = inputUI.uiType;
      float multiplier = inputUI.getMultiplier();
      float tolerance = inputUI.getTolerance();
      String type = inputUI.inputTypeName;
      data[index] = key + SEPARATOR + desc + SEPARATOR + typeID + SEPARATOR + type;
      data[index] += SEPARATOR + inputName + SEPARATOR + inputConNo;
      data[index] += SEPARATOR + multiplier + SEPARATOR + tolerance;
      index++;
    }
    return data;
  }

  public void verify_click(GButton button, GEvent event) { 
    verifyConfig(false);
  }


  public void save_click(GButton button, GEvent event) { 
    saveConfig();
  }


  public void clear_click(GButton button, GEvent event) {
    txaStatus.setText("");
  }

  synchronized public void pre(GWinApplet appc, GWinData data) {
    current = null;
    for (TBase ui : uiElements) {
      ui.update();
      ui.overWhat(appc.mouseX, appc.mouseY);
    }
    if (!dragging && current != null && current.conTo != null) {
      current.conTo.isOver = true;
    }
  }

  synchronized public void mouse(GWinApplet appc, GWinData data, MouseEvent mevent) {
    switch(mevent.getAction()) {
    case MouseEvent.PRESS:
      if (current != null) {
        start = current;
        dragging = true;
      }
      break;
    case MouseEvent.RELEASE:
      if (current != null && start != null && current.type != start.type) {
        end = current;
        current = null;
        dragging = false;
        if (start.conTo != null)
          start.conTo.conTo = null;
        if (end.conTo != null)
          end.conTo.conTo = null;
        start.conTo = end;
        end.conTo = start;
      }
      current = start = null;
      dragging = false;
      break;
    case MouseEvent.DRAG:

      break;
    }
  }

  synchronized public void draw(GWinApplet appc, GWinData data) {
    appc.background(BACKGROUND);
    if (!active) return;
    // Draw control panel at bottom
    appc.noStroke();
    appc.fill(PANEL);
    ;
    appc.rect(appc.width - PANEL_WIDTH, 0, PANEL_WIDTH, appc.height);
    // Draw connections
    appc.strokeWeight(3.5f);
    for (TConnector c : uiConnections) {
      if (c.conTo != null && c.type == TConnector.DESC) {
        appc.stroke(c.isOver ? HIGHLIGHT : CONNECTION);
        appc.line(c.px, c.py, c.conTo.px, c.conTo.py);
      }
    }
    // Connection in the making
    if (dragging && start != null) {
      appc.stroke(CONNECTION);
      appc.line(start.px, start.py, appc.mouseX, appc.mouseY);
    }
    // Draw descriptors and inputs
    for (TBase ui : uiElements)
      ui.draw();
  }

  public void device_usage(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      if (source.getText().indexOf(SEPARATOR) >= 0)
        source.setText(source.getText().replaceAll(SEPARATOR, " "));
    }
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
  GWindow window;
  GTabManager tabManager = new GTabManager();
  GTextField txfFilename, txfDeviceUsage;
  GTextArea txaStatus;

  public TConfigUI(PApplet papp, TSelectEntry entry) {
    float px, py, pw;
    device = entry.device;
    device.open();
    controlIO = entry.controlIO;
    float spaceForInputs = ELEMENT_UI_GAP;
    int nbrConnectors = 0;
    // Scan through controls to calculate the window height needed
    for (ControlInput input : device.getInputs()) {
      if (input instanceof ControlHat) {
        spaceForInputs += 5 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
        nbrConnectors ++;
      }
      else  if (input instanceof ControlButton) {
        spaceForInputs += INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
        nbrConnectors++;
      }
      else  if (input instanceof ControlSlider) {
        spaceForInputs += 4 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
        nbrConnectors++;
      }
      else
        System.out.println("Unknown input " + input);
    }
    float spaceForDescs = nbrConnectors * (DESC_UI_HEIGHT + ELEMENT_UI_GAP + 2);
    float spaceNeeded = Math.max(spaceForInputs, spaceForDescs);
    spaceNeeded = Math.max(spaceNeeded, PANEL_HEIGHT);
    // Now calculate window scaling and height
    if (papp.displayHeight < spaceNeeded + 40)
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
    window = new GWindow(papp, title, 80 + nbrWindows * 40, 100 + nbrWindows * 30, 1020, winHeight, false, G4P.JAVA2D);
    window.setResizable(false);
    window.addDrawHandler(this, "draw");
    window.addMouseHandler(this, "mouse");
    window.addPreHandler(this, "pre");
    window.papplet.noLoop();
    tabManager = new GTabManager();
    G4P.setCursor(CROSS, window);	
    nbrWindows++;

    // Create the control panel
    px = window.papplet.width - PANEL_WIDTH + 10;
    pw = PANEL_WIDTH - 20;
    py = 10;
    GLabel lblDeviceUsagle = new GLabel(window.papplet, px, py, pw, 20, "Device role (e.g. Tank controller)");
    lblDeviceUsagle.setTextAlign(GAlign.LEFT, null);
    lblDeviceUsagle.setLocalColorScheme(G4P.GREEN_SCHEME);
    lblDeviceUsagle.setTextBold();
    lblDeviceUsagle.setOpaque(true);
    py += 22;
    txfDeviceUsage = new GTextField(window.papplet, px, py, pw, 20);
    txfDeviceUsage.setLocalColorScheme(G4P.GREEN_SCHEME);
    txfDeviceUsage.setDefaultText("How will this device be used?");
    txfDeviceUsage.addEventHandler(this, "device_usage");
    py += 30;
    GLabel lblFilenamePrompt = new GLabel(window.papplet, px, py, pw, 20, "Filename for this configuration");
    lblFilenamePrompt.setTextAlign(GAlign.LEFT, null);
    lblFilenamePrompt.setLocalColorScheme(G4P.GREEN_SCHEME);
    lblFilenamePrompt.setTextBold();
    lblFilenamePrompt.setOpaque(true);
    py += 22;
    txfFilename = new GTextField(window.papplet, px, py, pw, 20);
    txfFilename.setLocalColorScheme(G4P.GREEN_SCHEME);
    txfFilename.setDefaultText("Enter a filename for this configuration");
    py += 26;
    float bw = (pw - 20)/3;
    GButton btnClearStatus = new GButton(window.papplet, px, py, bw, 20);
    btnClearStatus.setLocalColorScheme(G4P.GREEN_SCHEME);
    btnClearStatus.setText("Clear Status");
    btnClearStatus.addEventHandler(this, "clear_click");

    GButton btnVerify = new GButton(window.papplet, px + (pw - bw)/2, py, bw, 20);
    btnVerify.setLocalColorScheme(G4P.GREEN_SCHEME);
    btnVerify.setText("Verify");
    btnVerify.addEventHandler(this, "verify_click");

    GButton btnSave = new GButton(window.papplet, px + pw - bw, py, bw, 20);
    btnSave.setLocalColorScheme(G4P.GREEN_SCHEME);
    btnSave.setText("Save");
    btnSave.addEventHandler(this, "save_click");
    py += 30;
    GLabel lblStatus = new GLabel(window.papplet, px, py, pw, 20, "VERIFY / SAVE STATUS REPORT");
    lblStatus.setLocalColorScheme(G4P.GREEN_SCHEME);
    lblStatus.setTextBold();
    lblStatus.setOpaque(true);
    py += 22;
    txaStatus = new GTextArea(window.papplet, px, py, pw, 140, G4P.SCROLLBARS_VERTICAL_ONLY);
    txaStatus.setLocalColorScheme(G4P.GREEN_SCHEME);
    txaStatus.setDefaultText("Verify / save status report");
    py += txaStatus.getHeight() + 2;

    // Create and add inputs to UI 
    window.papplet.textSize(fontSize);
    px = window.papplet.width - 10 - INPUT_UI_LENGTH - PANEL_WIDTH;
    py = ELEMENT_UI_GAP + (spaceNeeded - spaceForInputs) / 2; 
    for (ControlInput input : device.getInputs()) {
      TBaseInput ui = TBaseInput.makeInputUI(this, input, px, py);
      if (ui != null) {
        uiElements.add(ui);
        py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
      }
    }
    // Create and add descriptors to UI 
    px = 10;
    py = ELEMENT_UI_GAP + (spaceNeeded - spaceForDescs) / 2; 
    for (int i = 0; i < nbrConnectors; i++) {
      TDescriptor ui = new TDescriptor(this, px, py);
      uiElements.add(ui);
      py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
    }
    // Now create list of connectors
    for (TBase ui : uiElements)
      for (TConnector c : ui.connectors)
        uiConnections.add(c);
    active = true;
    window.papplet.loop();
  }

  void close() {
    window.forceClose();
  }
}

