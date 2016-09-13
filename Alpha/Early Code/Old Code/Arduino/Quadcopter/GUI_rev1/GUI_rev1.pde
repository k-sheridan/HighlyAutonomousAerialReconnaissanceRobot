import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

import controlP5.*;
import saito.objloader.*;
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;
import processing.serial.*;

//SERIAL COMMS VARIABLES_______________________________________________________________________
//RECEIVE----------
final int BUFFER_LIMIT = 100; //I NOW HAVE SOME ROOM TO SEND DATA
int readPacketLoss = 0;
int sendPacketLoss = 0;
boolean readError = false;
boolean sendError = false;
byte[] data = new byte[BUFFER_LIMIT];

boolean connected = false;
int noRead = 0;
boolean firstRead = false;
boolean reconnecting = false;
int timeOut = 0;

//SEND---------------
final char DC_WRITE = 'a';
final char SA_WRITE = 'b';
final char A_WRITE = 'c';
final char TUNING_WRITE = 'd';

byte[] FB = new byte[4];
int comMode = 1;

//GUI VARIABLES________________________________________________________________________________
ControlIO control;
ControlDevice gpad;

// SOUND (MINIM)
Minim minim;
AudioPlayer connectionAlert;

//ControlP5 Variables_______________________________________________

ControlP5 cp5;
PFont textFieldFont = createFont("arial",36);

Chart motorChart;

//-------------
float rX, rY, lX, lY, kZ;
boolean s1, s2, b1, s4, s5;

int screenX, screenY;
PImage quad_bg, quad_border, quad_side, quad_top, g_off, g_on, y_off, y_on, r_off, r_on, connect_up, connect_down, start_up, start_down, RTH_on, RTH_off,
AL_on, AL_off, stop_on, stop_off, m1_bg, m2_bg, m3_bg, m1_tab_up, m1_tab_down, m2_tab_up, m2_tab_down, m3_tab_up, m3_tab_down, m4_tab_up, m4_tab_down, m4_bg,
pr_vis_bg, pr_vis_bg_rot, speed_bg, num_box, tuning_on, tuning_off, textBox;
PFont text;
int stage = 1;
Serial port;

int modeTab = 1;

//--------------TEXT BOX VARIABLES----------------------------------
Textarea myTextarea;

int c = 0;

Println console;

//----------------QUAD VARIABLES---------------------------------------
float pitch = 0;
float roll = 0;
float yaw = 0;
float alt = 0;

int dist_1 = 0;
int dist_2 = 0;
int dist_3 = 0;
int dist_4 = 0;
int cycleTime = 0;

short pitch_lock = 0;
short roll_lock = 0;
short yaw_lock = 0;
short alt_lock = 0;
short motor_speed = 140;

float m1 = 0;
float m2 = 0;
float m3 = 0;
float m4 = 0;

float Tp = 0;
float Ti = 0;
float Td = 0;

float p_val = 0;
float i_val = 0;
float d_val = 0;
byte tuningParam = 1;

int kill = 0;
boolean start = false;
int mode = 1;
//------------------------------------------------------------------------

void setup() {
  screenX = round(displayWidth * 1);
  screenY = round(displayHeight * 0.90);
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
  m4_tab_up = loadImage("mode_4_tab_up.png");
  m4_tab_down = loadImage("mode_4_tab_down.png");
  m4_bg = loadImage("mode_4_bg.png");
  num_box = loadImage("number_box.png");
  tuning_on = loadImage("tuning_switch_on.png");
  tuning_off = loadImage("tuning_switch_off.png");
  speed_bg = loadImage("speed_bg.png");
  pr_vis_bg = loadImage("pr_vis_bg.png");
  pr_vis_bg_rot = loadImage("pr_vis_bg_rotated.png");
  
  controlP5Init();
  //INITIALIZE MINIM
  minim = new Minim(this);
  connectionAlert = minim.loadFile("connectionAlarm.mp3");
  
  text = createFont("font", 1000, true);
  port = new Serial(this, "COM9", 57600);
  control = ControlIO.getInstance(this);
  gpad = control.getMatchedDevice("fs_config");
  frameRate(30);
}

void draw() {
  
  if (stage == 1) {
    tint(64, 64, 64, 127);
    fill(64, 64, 64, 127);
    drawGraphics();
    textWrite();
    tint(255, 255, 255, 255);
    textAlign(CENTER);
    fill(255, 255, 255, 255);
    textSize(26);
    text("GUI is loaded", screenX / 2, screenY / 2.5);
    text("Press <ENTER> to begin...", screenX / 2, (screenY / 2.5) + 60);
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
  serialHandShake();  
  connectionCheck();
}




