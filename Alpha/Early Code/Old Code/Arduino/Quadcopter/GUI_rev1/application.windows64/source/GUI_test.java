import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import saito.objloader.*; 
import org.gamecontrolplus.gui.*; 
import org.gamecontrolplus.*; 
import net.java.games.input.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GUI_test extends PApplet {







//SERIAL COMMS VARIABLES_______________________________________________________________________
//RECEIVE----------
final int BUFFER_LIMIT = 56;
int readPacketLoss = 0;
int sendPacketLoss = 0;
boolean readError = false;
boolean sendError = false;
byte[] data = new byte[BUFFER_LIMIT];

boolean connected = false;
int noRead = 0;

//SEND---------------
final char DC_WRITE = 'a';
final char SA_WRITE = 'b';
final char A_WRITE = 'c';


//GUI VARIABLES________________________________________________________________________________
ControlIO control;
ControlDevice gpad;

float rX, rY, lX, lY;
boolean s1, s2, b3, s4, s5;

int screenX, screenY;
PImage quad_bg, quad_border, quad_side, quad_top, g_off, g_on, y_off, y_on, r_off, r_on, connect_up, connect_down, start_up, start_down, RTH_on, RTH_off,
AL_on, AL_off, stop_on, stop_off, m1_bg, m2_bg, m3_bg, m1_tab_up, m1_tab_down, m2_tab_up, m2_tab_down, m3_tab_up, m3_tab_down, pr_vis_bg, pr_vis_bg_rot;
PFont text;
int stage = 1;
Serial port;

int modeTab = 1;

//----------------QUAD VARIABLES---------------------------------------
float pitch = 0;
float roll = 0;
float yaw = 0;

short pitch_lock = 0;
short roll_lock = 0;
short yaw_lock = 0;
short alt_lock = 0;
short motor_speed = 140;

float m1 = 0;
float m2 = 0;
float m3 = 0;
float m4 = 0;

int kill = 0;
boolean start = false;
int mode = 1;
//------------------------------------------------------------------------

public void setup() {
  screenX = round(displayWidth * 1);
  screenY = round(displayHeight * 0.90f);
  size(screenX, screenY, P3D);
  frame.setResizable(true);
  quad_bg = loadImage("Quad_gui.png");
  image(quad_bg, 0, 0, screenX, screenY);
  quad_border = loadImage("quad_borders.png");
  quad_side = loadImage("Quad_side.png");
  quad_top = loadImage("Quad_top.png");
  r_on = loadImage("quad_light_red_on.png");
  r_off = loadImage("quad_light_red_off.png");
  g_on = loadImage("quad_light_green_on.png");
  g_off = loadImage("quad_light_green_off.png");
  y_on = loadImage("quad_light_yellow_on.png");
  y_off = loadImage("quad_light_yellow_off.png");
  connect_up = loadImage("connect_btn_up.png");
  connect_down = loadImage("connect_btn_down.png");
  start_up = loadImage("start_btn_up.png");
  start_down = loadImage("start_btn_down.png");
  RTH_on = loadImage("RTH_btn_on.png");
  RTH_off = loadImage("RTH_btn_off.png");
  AL_on = loadImage("AL_btn_on.png");
  AL_off = loadImage("AL_btn_off.png");
  stop_on = loadImage("stop_btn_on.png");
  stop_off = loadImage("stop_btn_off.png");
  m1_bg = loadImage("mode_1_bg.png");
  m1_tab_up = loadImage("mode_1_tab_up.png");
  m1_tab_down = loadImage("mode_1_tab_down.png");
  m2_bg = loadImage("mode_2_bg.png");
  m2_tab_up = loadImage("mode_2_tab_up.png");
  m2_tab_down = loadImage("mode_2_tab_down.png");
  m3_tab_up = loadImage("mode_3_tab_up.png");
  m3_tab_down = loadImage("mode_3_tab_down.png");
  pr_vis_bg = loadImage("pr_vis_bg.png");
  pr_vis_bg_rot = loadImage("pr_vis_bg_rotated.png");
  text = createFont("font", 1000, true);
  port = new Serial(this, "COM5", 9600);
  port.bufferUntil('\n');
  control = ControlIO.getInstance(this);
  gpad = control.getMatchedDevice("fs_config");
}

public void draw() {
  if (stage == 1) {
    tint(64, 64, 64, 127);
    fill(64, 64, 64, 127);
    drawGraphics();
    textWrite();
    tint(255, 255, 255, 255);
    textAlign(CENTER);
    fill(255, 255, 255, 255);
    textSize(26);
    text("GUI is loaded", screenX / 2, screenY / 2.5f);
    text("Press <ENTER> to begin...", screenX / 2, (screenY / 2.5f) + 60);
  }
  if (stage == 2) {
    if (mode == 1) {               //DIRECT CONTROL!
      getUserInput();
      gamePadCompute();
      drawGraphics();
      textWrite();
    }
    if (mode == 2) {                        //SEMI-AUTONOMOUS!
      getUserInput();
      gamePadCompute();
      drawGraphics();
      textWrite();
    }
    if (mode == 3) {                         //AUTONOMOUS MODE!
      drawGraphics();
      textWrite();
    }
  }
  
  connectionCheck();
}




public void getUserInput() {
  rX = gpad.getSlider("rX").getValue();
  rY = gpad.getSlider("rY").getValue();
  lX = gpad.getSlider("lX").getValue();
  lY = gpad.getSlider("lY").getValue();
  s1 = gpad.getButton("s1").pressed();
  s2 = gpad.getButton("s2").pressed();
  b3 = gpad.getButton("b3").pressed();
  s4 = gpad.getButton("s4").pressed();
  s5 = gpad.getButton("s5").pressed();
  //println(rX + " " + rY + " " + lX + " " + lY + " " + s1 + " " + s2 + " " + b3 + " " + s4 + " " + s5);
}

public void gamePadCompute() {
  motor_speed = (short)round(map(lY, -1, 1, 140, 210));
  constrain(motor_speed, 140, 210);
  pitch_lock = (short)round(map(rY, -1, 1, -15, 15));
  roll_lock = (short)round(map(rX, -1, 1, 15, -15));
  yaw_lock += (short)round(lX * 2);
  if(yaw_lock > 359){
    yaw_lock = 0;
  }
  if(yaw_lock < 0){
    yaw_lock = 359;
  }
  if(b3 == true){
    start = false;
    kill = 0;
  }
  //println(motor_speed + " " + pitch_lock + " " + roll_lock + " " + yaw_lock);
}

public void drawGraphics() {
  image(y_on, 975, 7, 20, 20);
  image(y_off, 1175, 7, 20, 20);
  image(y_off, 1330, 7, 20, 20);
  if(modeTab == 1){
    image(m1_bg, 225, 60, 950, 400);
    quadDraw();
    image(pr_vis_bg, 320, 100, 15, 250);
    noStroke();
    fill(0xff008911);
    int pitch_lock_bar = (int)(225 + ((pitch_lock * -1) * 2.7f));
    constrain(pitch_lock_bar, -45, 45);
    rect(322, pitch_lock_bar, 10, 3);
    fill(0xff8E0000);
    int pitch_bar = (int)(225 + ((pitch * -1) * 2.7f));
    constrain(pitch_bar, -45, 45);
    rect(322, pitch_bar, 10, 3);
    fill(255);
    textAlign(RIGHT);
    text("Pitch", 340, 90);
    textSize(10);
    text("Pitch:", 280, (pitch_bar + 5));
    text("Pitch Lock:", 390, (pitch_lock_bar + 5));
    textAlign(LEFT);
    text(pitch_lock, 393, (pitch_lock_bar + 5));
    text(pitch, 285, (pitch_bar + 5));
    image(pr_vis_bg_rot, 350, 370, 250, 15);
    fill(0xff008911);
    int roll_lock_bar = (int)(475 + (roll_lock * 2.7f));
    constrain(roll_lock_bar, -45, 45);
    rect(roll_lock_bar, 372, 3, 10);
    fill(0xff8E0000);
    int roll_bar = (int)(475 + (roll * 2.7f));
    constrain(roll_lock_bar, -45, 45);
    rect(roll_bar, 372, 3, 10);
    fill(255);
    textAlign(RIGHT);
    textSize(12);
    text("Roll", 345, 380);
    textSize(10);
    textAlign(RIGHT);
    text("Roll:", roll_bar, 360);
    text("Roll Lock:", roll_lock_bar, 400);
    textAlign(LEFT);
    text(roll, (roll_bar + 3), 360);
    text(roll_lock, (roll_lock_bar + 3), 400);
    textSize(9);
    text("Pitch Lock:", 230, 380);
    text("Roll Lock:", 230, 390);
    text("Yaw Lock:", 230, 400);
    text("Motor Speed:", 230, 410);
    text(pitch_lock, 280, 380);
    text(roll_lock, 280, 390);
    text(yaw_lock, 280, 400);
    text(motor_speed, 290, 410);
    if(mode != 1){
    fill(0xffFF0303);
    textSize(18);
    text("Mode 1 is not selected", 950, 90);
    textSize(10);
    }
    fill(255);
  }
  if(modeTab == 2){
    image(m2_bg, 225, 60, 950, 400);
  }
  if(modeTab == 3){
    image(m2_bg, 225, 60, 950, 400);
  }
  image(quad_bg, 0, 0, screenX, screenY);
  if(modeTab == 1){
    image(m1_tab_up, 350, 30, 120, 30);
    image(m2_tab_down, 470, 30, 120, 30);
    image(m3_tab_down, 590, 30, 120, 30);
  }
  if(modeTab == 2){
    image(m1_tab_down, 350, 30, 120, 30);
    image(m2_tab_up, 470, 30, 120, 30);
    image(m3_tab_down, 590, 30, 120, 30);
  }
  if(modeTab == 3){
    image(m1_tab_down, 350, 30, 120, 30);
    image(m2_tab_down, 470, 30, 120, 30);
    image(m3_tab_up, 590, 30, 120, 30);
  }
  image(quad_border, 0, 0, screenX, screenY);
  image(quad_top, 275, 500, 150, 150);
  image(quad_side, 525, 575, 150, 30);
  image(connect_up, 15, 600, 90, 30);
  image(start_up, 15, 640, 90, 30);
  if (kill != 1) {
    image(RTH_off, 1230, 30, 150, 150);
  }
  if (kill == 1) {
    image(RTH_on, 1230, 30, 150, 150);
  }
  if(kill != 2){
    image(AL_off, 1230, 180, 150, 150);
  }
  if(kill == 2){
    image(AL_on, 1230, 180, 150, 150);
  }
  if(start == true){
   image(stop_off, 1230, 330, 150, 150); 
  }
  if(start == false){
   image(stop_on, 1230, 330, 150, 150); 
  }
  if (mode == 1) {
    image(y_on, 975, 7, 20, 20);
    image(y_off, 1175, 7, 20, 20);
    image(y_off, 1330, 7, 20, 20);
  }
  if (mode == 2) {
    image(y_off, 975, 7, 20, 20);
    image(y_on, 1175, 7, 20, 20);
    image(y_off, 1330, 7, 20, 20);
  }
  if (mode == 3) {
    image(y_off, 975, 7, 20, 20);
    image(y_off, 1175, 7, 20, 20);
    image(y_on, 1330, 7, 20, 20);
  }
  if (readError == false) {
    image(r_off, 5, 445, 20, 20);
  }
  if (readError == true) {
    image(r_on, 5, 445, 20, 20);
  }
  if (sendError == false) {
    image(r_off, 5, 465, 20, 20);
  }
  if (sendError == true) {
    image(r_on, 5, 465, 20, 20);
  }
}

public void keyPressed() {
  if (key == ENTER && stage == 1) {
    stage = 2;
  }
  if (key == ' ' && stage == 2 && start == true) {
    start = false;
  }
  if (key == '1' && mode != 1) {
    mode = 1;
  }
  if (key == '2' && mode != 2) {
    mode = 2;
  }
  if (key == '3' && mode != 3) {
    mode = 3;
  }
  if (key == 'c' && connected == false) {
    readPacket();
    readPacket();
    writePacket('a');
  }
  if (key == 's') {
    start = true;
  }
}

public void mousePressed() {
  if(mouseX >= 350 && mouseX <= 470 && mouseY >= 30 && mouseY <= 60){
    modeTab = 1;
  }
  if(mouseX > 470 && mouseX <= 590 && mouseY >= 30 && mouseY <= 60){
    modeTab = 2;
  }
  if(mouseX > 590 && mouseX <= 710 && mouseY >= 30 && mouseY <= 60){
    modeTab = 3;
  }
  if (mouseX >= 1260 && mouseX <= 1350 && mouseY >= 60 && mouseY <= 150) {
    if (kill != 1) {
      kill = 1;
    } else if (kill == 1) {
      kill = 0;
    }
  }
  if(mouseX >= 1260 && mouseX <= 1350 && mouseY >= 210 && mouseY <= 300){
    if (kill != 2) {
      kill = 2;
    } else if (kill == 2) {
      kill = 0;
    }
  }
  if(mouseX >= 1260 && mouseX <= 1350 && mouseY >= 360 && mouseY <= 450){
    kill = 0;
    start = false;
  }

  if (mouseX >= 15 && mouseX <= 105 && mouseY >= 600 && mouseY <= 630) {
    readPacket();
    readPacket();
    writePacket('a');
    image(connect_down, 15, 600, 90, 30);
  }

  if (mouseX >= 15 && mouseX <= 105 && mouseY >= 640 && mouseY <= 670) {
    start = true;
    image(start_down, 15, 640, 90, 30);
  }
}

public void readPacket() {
  int remainder = 0;
  int x;
  int checkSum = 0;
  int payLoadSum = 0;
  int[] fix = new int[BUFFER_LIMIT - 2];

  // read the bytes in 
  for (int i = 0; i < BUFFER_LIMIT; i++) {
    data[i] = (byte)port.read();
  }

  //BYTE FIX-----------------
  for (int i = 0; i < 54; i++) {
    if (data[i] < 0) {
      payLoadSum += (256 + (int)data[i]);
    } else {
      payLoadSum += (int)data[i];
    }
  }
  //---------------------------

  //CHECK SUM---------------------------
  x = payLoadSum;
  remainder = x / 23;
  remainder = x - (remainder * 23);
  //-------------------------------

  checkSum = data[54];

  switch(data[0]) {

  case 'a':
    if(checkSum == remainder){
      pitch = get4ByteFloat(data, 1);
      roll = get4ByteFloat(data, 5);
      yaw = get4ByteFloat(data, 9);
      m1 = get4ByteFloat(data, 13);
      m2 = get4ByteFloat(data, 17);
      m3 = get4ByteFloat(data, 21);
      m4 = get4ByteFloat(data, 25);
      
      if(data[53] == 100){
        sendError = true;
        sendPacketLoss++;
      }
      else if(data[53] == 0){
        sendError = false;
      }
      
      readError = false;
    }
    else if(checkSum != remainder){
      readError = true;
      readPacketLoss++;
    }
    break;

  default:
    readPacketLoss++;
    readError = true;
    break;
  }
}


public float get4ByteFloat(byte[] data, int offset) { 
  String hexint=hex(data[offset+3])+hex(data[offset+2])+hex(data[offset+1])+hex(data[offset]); 
  return Float.intBitsToFloat(unhex(hexint));
} 

public void writePacket(char CMD) {
  int[] ITEM = new int[22];
  int payLoadSum = 0;
  int CHKSUM = 1;
  int remainder;

  ITEM[0] = CMD;

  if (start == false) {
    ITEM[1] = 0;
  }
  if (start == true) {
    ITEM[1] = 100;
  }

  ITEM[2] = mode;
  ITEM[3] = kill;
  ITEM[4] = 66; // unused at the moment

  ITEM[5] = pitch_lock >> 8;
  ITEM[6] = pitch_lock & 0xFF;
  
  //println(binary(ITEM[5]) + " " + binary(ITEM[6]));

  ITEM[7] = roll_lock >> 8;
  ITEM[8] = roll_lock & 0xFF;

  ITEM[9] = yaw_lock >> 8;
  ITEM[10] = yaw_lock & 0xFF;

  ITEM[11] = alt_lock >> 8;
  ITEM[12] = alt_lock & 0xFF;

  ITEM[13] = motor_speed;



  //FIX BYTES AND CHECK SUM__---

  for (int i = 0; i < 22; i++) {
    if (ITEM[i] < 0) {
      payLoadSum += (256 + (int)ITEM[i]);
    } else {
      payLoadSum += (int)ITEM[i];
    }
  }
  
  CHKSUM = payLoadSum;
  remainder = CHKSUM / 23;
  remainder = CHKSUM - (remainder * 23);
  
  CHKSUM = remainder;
  //WRITE DATA:
  
  for(int i = 0; i < 22; i++){
    port.write(ITEM[i]);
  }
  port.write(CHKSUM);
  port.write('\n');
}

public void serialEvent(Serial port) {
  if (port.available() == 56) {
    readPacket();
    writePacket(DC_WRITE);
    noRead = 0;
  }
}


public void connectionCheck() {
  noRead++;
  if (noRead >= 15) {
    connected = false;
  } else if (noRead < 15) {
    connected = true;
  }
}

public void textWrite() {
  textAlign(LEFT);
  fill(255);
  textSize(14);
  text("Motor 1:", 10, 110);
  text(m1, 70, 110);
  text("Motor 2:", 10, 130);
  text(m2, 70, 130);
  text("Motor 3:", 10, 150);
  text(m3, 70, 150);
  text("Motor 4:", 10, 170);
  text(m4, 70, 170);
  text("Pitch:", 10, 200);
  text(pitch, 50, 200);
  text("Roll:", 10, 220);
  text(roll, 50, 220);
  text("Yaw:", 10, 240);
  text(yaw, 50, 240);
  text("Altitude:", 10, 270);
  text("Relative Altitude:", 10, 290); 
  text("GPS Lattitude:", 10, 320);
  text("GPS Longitude:", 10, 340);
  text("Direct Control - (1)", 835, 20);
  text("Semi-Direct Control - (2)", 1000, 20);
  text("Autonomous - (3)", 1200, 20);
  text("Read Error:", 30, 460);
  text(readPacketLoss, 108, 460);
  text("Write Error:", 30, 480);
  text(sendPacketLoss, 110, 480);
  textSize(11);
  text("Connection Status:", 10, 500);
  if (connected == true) {
    fill(0, 255, 0);
    text("CONNECTED", 120, 500);
  }
  if (connected == false) {
    fill(255, 0, 0);
    text("NOT CONNECTED", 120, 500);
  }
  fill(255);
  textAlign(RIGHT);
  textSize(12);
  text("Return To Home", 1260, 90);
  text("Auto Land", 1260, 240);
  text("Stop - (Space)", 1260, 390);
}

public void quadDraw() {
  pushMatrix();
  lights();
  translate(550, 240, 38);
  rotateX(radians(pitch - 10));
  rotateZ(radians(roll));
  rotateY(radians(5));
  box(150, 30, 100);
  noLights();
  popMatrix();
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GUI_test" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
