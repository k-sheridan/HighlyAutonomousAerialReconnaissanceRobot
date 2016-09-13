/**
Example of using Game Control Plus in a game.

Although I am using a joystick to control the tank, any device 
with 2 sliders and 5 buttons will work.

This sketch also needs the 'Sprites' library to be installed.
You can do this using the Sketch | Import Library | Add Library'
menu option in Processing. 
 
created by Peter Lager
*/

import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;

import sprites.utils.*;
import sprites.maths.*;
import sprites.*;

ControlIO control;
ControlDevice stick;

Sprite tBase, tTurret, tShell;
StopWatch timer;
float tankSpeed = 0, tankMaxSpeed = 40, tankMaxRevSpeed = -15;
float tankHeading, tankRotateRate = 0.8f;
float tankAccelRate = 5, tankDecelRate = 15;
float turretRot, turretRotRate = 2.0f;

int score, nbrTargetsHit = 0;
int tankStartX, tankStartY;
Sprite[] forest, target;
// Defines colour of tank. Must be in range 0-7
int tcol = 5;

final int SPLASH = 0;
final int INPLAY = 1;
int mode = SPLASH;
float playTime = 0;

PImage splash;
int dispX, dispY;

//HashSet<Integer> keysDown = new HashSet<Integer>();

public void setup() {
  size(640, 480);
  // Initialise the ControlIO
  control = ControlIO.getInstance(this);
  // Find a device that matches the configuration file
  stick = control.getMatchedDevice("tank_game");
  if (stick == null) {
    println("No suitable device configured");
    System.exit(-1); // End the program NOW!
  }

  // Load instruction screen
  splash = loadImage("tankinstr.png");
  dispX = (width - splash.width)/2;
  dispY = (height - splash.height)/2;
  // Create the game timer
  timer = new StopWatch();
  // Uses an image to define the terrain and target positions, also defines
  // the tank start position.
  ArrayList<Sprite> trees = new ArrayList<Sprite>();
  ArrayList<Sprite> targets = new ArrayList<Sprite>();
  PImage map = loadImage("tank_grid1.png");
  Sprite s;
  for (int y = 10; y < width; y += 20) {
    for (int x = 10; x < width; x += 20) {
      int c = map.get(x, y) & 0x00ffffff;
      int r = (c >> 16) & 0xff;
      int g = (c >> 8) & 0xff;
      int b = c & 0xff;
      if (b == 255) {
        s = new Sprite(this, "box.png", 50);
        s.setXY(x, y);
        trees.add(s);
      }
      else if (r == 255) {
        s = new Sprite(this, "target.png", 50);
        s.setXY(x, y);
        targets.add(s);
      }
      else if (g == 255) {
        tankStartX = x;
        tankStartY = y;
      }
    }
  }
  // Convert arraylists to arrays for faster processing later
  forest = trees.toArray(new Sprite[trees.size()]);
  target = targets.toArray(new Sprite[targets.size()]);
  // Setup the tank sprite
  tBase = new Sprite(this, "tanks001a.png", 8, 8, 100);
  tBase.setFrameSequence(tcol * 8 + 7, tcol * 8);
  // Setup the tank turret sprite
  tTurret = new Sprite(this, "tanks001b.png", 1, 8, 104);
  tTurret.setFrame(tcol);
  // Setup the shell that is fired by the tank
  tShell = new Sprite(this, "tanks001c.png", 1, 8, 102);
  tShell.setFrame(tcol);
  // Initialise the game start position
  initGameStart();
}

public void initGameStart() {
  // Scoring - zero number of targets hit
  nbrTargetsHit = 0;
  // Start values for tank body
  tankSpeed = 0;
  tankHeading = 0;
  tBase.setXY(tankStartX, tankStartY);
  tBase.setRot(tankHeading);
  tBase.setSpeed(tankSpeed, tankHeading);
  tBase.stopImageAnim();
  // Start values for tank turret
  turretRot = 0;
  tTurret.setXY(tankStartX, tankStartY);
  tTurret.setRot(turretRot);
  // Start values for tank shell
  tShell.setVisible(false);
  // Make targets visible
  for (int i = 0; i < target.length; i++)
    target[i].setVisible(true);
  timer.reset();
}

public void draw() {
  // Start drawing now
  background(0, 64, 0);
  if (mode == INPLAY) {
    playTime = (float) timer.getRunTime();
    float deltaTime = (float) timer.getElapsedTime();
    processUserGameInput(deltaTime);
    updateAllSprites(deltaTime);
    processCollisions(deltaTime);
  }
  for (Sprite s : forest)
    s.draw();
  for (Sprite s : target)
    s.draw();
  tBase.draw();
  tShell.draw();
  tTurret.draw();
  showStatus((float) playTime, target.length - nbrTargetsHit);
  if (mode == SPLASH) {
    fill(255, 192);
    noStroke();
    rect(dispX, dispY, splash.width, splash.height);
    imageMode(CORNER);
    image(splash, dispX, dispY);
  }
}

/**
 * Show game time elapsed and number of targets hit
 * @param gameTime time game has been running (seconds)
 * @param targetsLeft number of targets left
 */
public void showStatus(float gameTime, int targetsLeft) {
  fill(255);
  noStroke();
  text("Game time", 230, 18);
  text("" + nf(gameTime, 4, 1), 300, 18);
  text("Targets left", 230, 35);
  text("" + targetsLeft, 300, 35);
}

/** 
 * Process all possible collisions that we are
 * interested in.
 * @param deltaTime elapsed time since last frame (seconds)
 */
public void processCollisions(float deltaTime) {
  // Test for tank hitting forest
  for (int i = 0; i < forest.length; i++) {
    if (tBase.bb_collision(forest[i])) {
      float px = (float) tBase.getX();
      float py = (float) tBase.getY();
      float vx = (float) tBase.getVelX();
      float vy = (float) tBase.getVelY();
      px -= 3 * vx * deltaTime;
      py -= 3 * vy * deltaTime;
      tBase.setXY(px, py);
      tTurret.setXY(px, py);
      tBase.setVelXY(0, 0);
      tankSpeed = 0;
      tBase.stopImageAnim();				
      break;
    }
  }
  // Check for shell hitting a forest
  if (tShell.isVisible()) {
    for (int i = 0; i < forest.length; i++) {
      if (tShell.bb_collision(forest[i])) {
        tShell.setVisible(false);
        break;
      }
    }
  }
  // Check for shell hitting a target
  if (tShell.isVisible()) {
    for (int i = 0; i < target.length; i++) {
      if (tShell.pp_collision(target[i])) {
        nbrTargetsHit++;
        tShell.setVisible(false);
        target[i].setVisible(false);
        break;
      }
    }
  }
  if (nbrTargetsHit == target.length) {
    tBase.setVelXY(0, 0);
    tankSpeed = 0;
    tBase.stopImageAnim();
    mode = SPLASH;
  }
}

/**
 * Update the tank, turret and shell positions
 * @param deltaTime elapsed time since last frame (seconds)
 */
public void updateAllSprites(float deltaTime) {
  tShell.update(deltaTime);
  tBase.update(deltaTime);
  tTurret.setXY(tBase.getX(), tBase.getY());
}

/**
 * Process user input during gameplay
 * 
 * Note: The control device maybe a joystick or gamepad
 * @param deltaTime elapsed time since last frame (seconds)
 */
public void processUserGameInput(float deltaTime) {
  // Calculate the tanks speed and set the sprite animation sequence 
  tankSpeed = -stick.getSlider("SPEED").getValue();
  if (tankSpeed > 0) {
    tankSpeed *= tankMaxSpeed;
    tBase.setFrameSequence(tcol * 8 + 7, tcol * 8, abs(1.0f/tankSpeed));
  }
  else if (tankSpeed < 0) {
    tankSpeed *= -tankMaxRevSpeed;
    tBase.setFrameSequence(tcol * 8, tcol * 8 + 7, abs(1.0f/tankSpeed));
  }
  else {
    tBase.setAnimInterval(0);
  }

  // Adjust direction tank moves
  float tankDir = stick.getSlider("DIR").getValue();
  //  println(tankDir + "\t\t" + stick.getSlider("DIR").getTolerance());
  if (tankDir < 0 || stick.getButton("LEFT").pressed())
    tankHeading -= tankRotateRate * deltaTime;
  else if (tankDir > 0 || stick.getButton("RIGHT").pressed())
    tankHeading += tankRotateRate * deltaTime;
  // tankHeading += change;
  // Now set the tank's base heading and speed
  tBase.setRot(tankHeading);
  tBase.setSpeed(tankSpeed, tankHeading);

  // Adjust direction of turret (relative to tank body)
  float change = 0;
  if (stick.getButton("T_ACLOCK").pressed())
    change -= turretRotRate * deltaTime;
  if (stick.getButton("T_CLOCK").pressed())
    change += turretRotRate * deltaTime;
  turretRot += change;
  tTurret.setRot(tankHeading + turretRot);

  // Fire shell?
  if (stick.getButton("FIRE").pressed() && tShell.isVisible() == false ) {
    tShell.setXY(tBase.getX(), tBase.getY());
    tShell.setSpeed(300, tankHeading + turretRot);
    tShell.setVisible(true);
  }
}


//public void keyPressed() {
//  keysDown.add(keyEvent.getKeyCode());
//}

public void keyReleased() {
//  keysDown.remove(keyEvent.getKeyCode());
  // Change tank colour
  if (key >= '0' && key <= '7') {
    tcol = key - '0';
    int newFrame = tBase.getFrame() % 8 + tcol * 8;
    tBase.setFrame(newFrame);
    tTurret.setFrame(tcol);
    tShell.setFrame(tcol);
  }
  if (key == 'p') {
    if (mode == SPLASH) {
      initGameStart();
      mode = INPLAY;
    }
    else {
      tBase.setVelXY(0, 0);
      tankSpeed = 0;
      tBase.stopImageAnim();				
      mode = SPLASH;
    }
  }
}

//public boolean keyDown(int kcode) {
//  return keysDown.contains(kcode);
//}

