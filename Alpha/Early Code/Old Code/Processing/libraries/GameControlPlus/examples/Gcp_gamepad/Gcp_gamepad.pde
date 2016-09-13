/**
 Basic demonstration of using a gamepad.
 
 When this sketch runs it will try and find
 a game device that matches the configuration
 file 'gamepad' if it can't match this device
 then it will present you with a list of devices
 you might try and use.
 
 The chosen device requires 3 sliders and 2 button.
 */

import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;

ControlIO control;
Configuration config;
ControlDevice gpad;

float pupilPosX, pupilPosY, pupilSize;
float eyeRad = 80, eyeSize = eyeRad * 2;
float browSize =  eyeSize * 1.2f, browFactor;
float irisRad = 42, irisSize = irisRad * 2;
float lidPos, restLid = PI * 0.3f, minLid = restLid/1.5f, maxLid = PI * 0.92f;

public void setup() {
  size(400, 240);
  // Initialise the ControlIO
  control = ControlIO.getInstance(this);
  // Find a device that matches the configuration file
  gpad = control.getMatchedDevice("gamepad_eyes");
  if (gpad == null) {
    println("No suitable device configured");
    System.exit(-1); // End the program NOW!
  }
}

public void draw() {
  background(255, 200, 255);
  // Either button will dilate pupils
  boolean dilated = gpad.getButton("PUPILSIZE1").pressed() || gpad.getButton("PUPILSIZE2").pressed();
  pupilSize = dilated ? irisSize * 0.6f : irisSize * 0.45f; 
  pupilPosX =  0.9f * map(gpad.getSlider("XPOS").getValue(), -1, 1, -(eyeRad - irisRad), eyeRad - irisRad);
  pupilPosY =  0.9f * map(gpad.getSlider("YPOS").getValue(), -1, 1, -(eyeRad - irisRad), eyeRad - irisRad);
  // Eyebrow first
  lidPos = gpad.getSlider("EYELID").getValue();
  browFactor = (lidPos >= 0) ? 1 : map(lidPos, 0, -1, 1.1f, 1.3f);
  // Now the actual lids
  lidPos = map(lidPos, -0.12f, 1, restLid, maxLid);
  lidPos = constrain(lidPos, minLid, maxLid);
  // DRaw a pir of eyes
  drawEye(100, 140);
  drawEye(300, 140);
}

public void drawEye(int x, int y) {
  pushMatrix();
  translate(x, y);
  // draw white of eye
  stroke(0, 96, 0);
  strokeWeight(3);
  fill(255);
  ellipse(0, 0, eyeSize, eyeSize);
  // draw iris
  noStroke();
  fill(120, 100, 220);
  ellipse(pupilPosX, pupilPosY, irisSize, irisSize);
  // draw pupil
  fill(32);
  ellipse(pupilPosX, pupilPosY, pupilSize, pupilSize);
  // draw eye lid
  stroke(0, 96, 0);
  strokeWeight(4);
  fill(220, 160, 220);
  arc(0, 0, eyeSize, eyeSize, 1.5f*PI-lidPos, 1.5f*PI+lidPos, CHORD);
  // Draw eyebrow
  stroke(100, 100, 10);
  strokeWeight(10);
  noFill();
  arc(0, 0, browSize, browSize * browFactor, 1.2f*PI, 1.8f*PI);
  popMatrix();
}