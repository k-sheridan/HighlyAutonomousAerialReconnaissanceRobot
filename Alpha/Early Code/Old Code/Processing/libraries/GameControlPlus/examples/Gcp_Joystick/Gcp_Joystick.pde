/**
 Basic demonstration of using a joystick.
 
 When this sketch runs it will try and find
 a game device that matches the configuration
 file 'joystick' if it can't match this device
 then it will present you with a list of devices
 you might try and use.
 
 The chosen device requires 2 sliders and 2 buttons.
 */

import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;

ControlIO control;
ControlDevice stick;
float px, py;
boolean trailOn;

ArrayList<PVector>  shadows = new ArrayList<PVector>();
ArrayList<PVector>  trail = new ArrayList<PVector>();

public void setup() {
  size(400, 400);
  // Initialise the ControlIO
  control = ControlIO.getInstance(this);
  // Find a device that matches the configuration file
  stick = control.getMatchedDevice("joystick");
  if (stick == null) {
    println("No suitable device configured");
    System.exit(-1); // End the program NOW!
  }
  // Setup a function to trap events for this button
  stick.getButton("SHADOW").plug(this, "dropShadow", ControlIO.ON_RELEASE);
}

// Poll for user input called from the draw() method.
public void getUserInput() {
  px = map(stick.getSlider("X").getValue(), -1, 1, 0, width);
  py = map(stick.getSlider("Y").getValue(), -1, 1, 0, height);
  trailOn = stick.getButton("TRAIL").pressed();
}

// Event handler for the SHADOW button
public void dropShadow() {
  // Make sure we have the latest position
  getUserInput();
  shadows.add(new PVector(px, py, 40));
}

public void draw() {
  getUserInput(); // Polling
  background(255, 255, 240);
  // Draw shadows
  fill(0, 0, 255, 32);
  noStroke();
  for (PVector shadow : shadows)
    ellipse(shadow.x, shadow.y, shadow.z, shadow.z);
  // Add to trail if appropriate trail
  if (trailOn) 
    trail.add(new PVector(px, py));
  else
    trail.clear();
  // If there is a trail then draw it
  if (trail.size() > 1) {
    stroke(132, 0, 0);
    for (int n = 1; n < trail.size(); n++) {
      PVector v0 = trail.get(n-1);
      PVector v1 = trail.get(n);
      line(v0.x, v0.y, v1.x, v1.y);
      v0 = v1;
    }
  }
  // Show position
  noStroke();
  fill(255, 64, 64, 64);
  ellipse(px, py, 20, 20);
}
