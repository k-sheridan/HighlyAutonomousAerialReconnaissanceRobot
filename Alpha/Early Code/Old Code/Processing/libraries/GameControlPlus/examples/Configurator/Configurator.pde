/**
 If you are creating your own sketches that need the Game Control Plus library,
 then you can use this sketch to create the input device configuration file.
 
 This sketch requires the G$P library which you can add using the 
 Import Library | All Library menu option in the PDE.
 
 The game input device MUST be connected before launching this sketch
 otherwise it will not find it.
 
 It is recommended that you have already created the user sketch so the 
 configuration file can be saved to it. Note that the configuration file will
 always be stored in a folder called 'data' inside your sketch folder.
 If the 'data' folder does not exist, it will be created when you save a
 configuration file. The reason for this is to ensure the configuration
 file is included if you export the sketch as an application.
 
 The sketch cannot be used to edit existing configuration files so if you
 make a mistake you either have to start over or edit the configuration 
 file in a text editor.
 
 created by Peter Lager 2014
 */

import g4p_controls.G4P;
import g4p_controls.GAlign;
import g4p_controls.GButton;
import g4p_controls.GCScheme;
import g4p_controls.GEvent;
import g4p_controls.GLabel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlIO;

ControlIO controlIO;
int panelHeight;
GLabel lblPath, lblSketch;
GButton btnSelSketch;

List<TSelectEntry> deviceEntries =  new ArrayList<TSelectEntry>();

public void setup() {
  controlIO = ControlIO.getInstance(this);
  panelHeight = 40;
  int appHeight = controlIO.getNumberOfDevices() *20 + 40 + panelHeight;
  size(800, appHeight);
  G4P.messagesEnabled(false);                                                                             
  G4P.setGlobalColorScheme(GCScheme.GREEN_SCHEME);
  if (frame != null)
    frame.setTitle("Game Input Device Configurator");
  registerMethod("dispose", this);
  createSelectionInterface();
  TConfigUI.pathToSketch = sketchPath("");
  List<ControlDevice> devices = controlIO.getDevices();
  // Add entries for devices added
  for (ControlDevice d : devices) {
    if (d != null && !d.getTypeName().equalsIgnoreCase("keyboard"))
      deviceEntries.add(new TSelectEntry(this, controlIO, d));
  }
  // Reposition entries on screen
  for (int i = 0; i < deviceEntries.size(); i++)
    deviceEntries.get(i).setIndex(panelHeight + 20, i);
  //    sel
  System.getProperty("file.separator");
}

private void createSelectionInterface() {
  GLabel lblPathTag = new GLabel(this, 0, 0, 100, 20, "Sketch Path : ");
  lblPathTag.setOpaque(true);
  lblPathTag.setTextBold();    
  GLabel lblSketchTag = new GLabel(this, 0, 20, 100, 20, "Sketch Name : ");
  lblSketchTag.setOpaque(true);
  lblSketchTag.setTextBold();  

  lblPath = new GLabel(this, 100, 0, width-100, 20);
  lblPath.setLocalColorScheme(G4P.PURPLE_SCHEME);
  lblPath.setOpaque(true);
  lblPath.setText(sketchPath(""));
  lblPath.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);

  lblSketch = new GLabel(this, 100, 20, width-100, 20);
  lblSketch.setLocalColorScheme(G4P.PURPLE_SCHEME);
  lblSketch.setOpaque(true);
  lblSketch.setText(getClass().getSimpleName() + ".pde");
  lblSketch.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);

  btnSelSketch = new GButton(this, width-60, 4, 56, 33, "Select Sketch");

  GLabel lblControls = new GLabel(this, 0, panelHeight, width, 20);
  lblControls.setText("Game Devices Available");
  lblControls.setOpaque(true);
  lblControls.setTextBold();
}

public void handleButtonEvents(GButton button, GEvent event) { 
  if (button == btnSelSketch && event == GEvent.CLICKED) {
    selectSketch();
  }
}

public void draw() {
  background(255, 255, 220);

  stroke(230, 230, 200);
  fill(240, 240, 210);
  int y = panelHeight;
  while (y < height) {
    rect(0, y, width, 20);
    y += 40;
  }
}

public void dispose() {
  System.out.println("Disposing");
  for (TSelectEntry entry : deviceEntries) {
    if (entry.winCofig != null) {
      entry.winCofig.close();
      entry.winCofig = null;
    }
  }
}

public void selectSketch() {
  String selected = G4P.selectInput("Select main sketch (pde) file", "pde", "Processing sketch");
  if (selected != null) {
    File file = new File(selected);
    // Get the name of the parent folder
    String sketchFolderName = file.getParent();
    // Get the filename without the extension
    String filename = file.getName();
    int index = filename.lastIndexOf('.');
    String sketchName = (index > 0 ) ? filename.substring(0, index) : filename;
    // See if we have selected the main sketch pde
    if (sketchFolderName.endsWith(sketchName)) {
      TConfigUI.pathToSketch = sketchFolderName;
      lblPath.setText(sketchFolderName);
      lblSketch.setText(filename);
    }
  }
}

