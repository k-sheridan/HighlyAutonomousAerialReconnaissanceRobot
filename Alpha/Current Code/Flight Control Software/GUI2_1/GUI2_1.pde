import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

import controlP5.*;

import processing.net.*;

//SERVER VARIABLES----------------------------------------------
Server gs_server;
int port = 10002;
Client currentClient;
boolean packetReceived = false;
int comCounter = 0;
//GUI VARIABLES----------------------------------------------------
//graphs
//posGraph odomGraph = new posGraph(10, 500, 200, 200);
rateGraph rg = new rateGraph(10, 300, 200, 200);
//gamepad

//controlp5

ControlP5 cp5;
Textarea myTextarea;
Println console;
DropdownList d1;
Chart motorChart;
//-------------------------------------------------------------------
//GUI Varialbles
boolean start = false;
boolean connected = false;

float tp, ti, td;
float p, i, d;
byte mode, pid_mode;
boolean pid_sync = false;
byte[] FB = new byte[4];

int pitch_lock, roll_lock, yaw_lock;
int alt_lock;
float pitch, roll, yaw;
float relalt;
float rateX, rateY;
int SQUAL;
float m1_speed, m2_speed, m3_speed, m4_speed;
//get rid of
float zeroRateX, zeroRateY;
float slamX, slamY, slamW;


void setup() {
  size(550, displayHeight - 50); //start window
  //frame.setResizable(true);

  gs_server = new Server(this, port); // Initialize server object

  controlP5Init(); // initialize cp5
  
  mode = 1;

  frameRate(30);
}


void draw() {
  background(75);

  drawGUI();

  serverPoll();
}

