import peasy.*;
import controlP5.*;
import processing.opengl.*;

PeasyCam cam;
ControlP5 cp5;

//GUI
Textarea myTextarea;
Println console;
boolean start = false;
int lineNumber = 2;
int lastSystemMillis = 0;
String startTime = hour() + "-" + minute();

float colorDist = 50;

//DATA
boolean dataLoaded = false; 
String flightData[];

float pitch, roll, yaw;
float m1, m2, m3, m4;
float xPos, yPos;
int SQUAL;
int motor_speed;
int dist_1, dist_2, dist_3, dist_4;
int cycleTime;
int relAlt;
float Zaccel;

int deltaMillis;
String time = "-:-:-";

//PROCESSED DATA

int Xpoint[] = new int[100];
int Ypoint[] = new int[100];
int Zpoint[] = new int[100];
int pointCount;

int quadX[] = new int[100];
int quadY[] = new int[100];
int quadZ[] = new int[100];

//Tuning
int maxDist = 150;
float posMult = 1.7;
float distMult = 1.4;
int minAlt = 20;
int minSQUAL = 0;
//Images
PImage logo, quadTop;


void setup() {
  frameRate(30);
  size(displayWidth, displayHeight, OPENGL);
  cam = new PeasyCam(this, 1000);
  cam.rotateX(0.375);
  cam.rotateY(0.729);
  cam.rotateZ(-0.267);
  cam.setDistance(2000);
  cam.lookAt(0, 0, 0);
  cp5 = new ControlP5(this);
  cp5Init();
  cp5.setAutoDraw(false);
  imageInit();
}
void draw() {
  cameraLogic();
  background(50);
  drawGrid();
  if (start == true) {
    //Processing data
    if ((millis() - lastSystemMillis) >= deltaMillis) {
      convertData(lineNumber);
      generateCoordinates();
      if (lineNumber < flightData.length - 1) {
        lineNumber++;
      } else {
        println("Recording is Finished");
        start = false;
      }
    }
    //Display data
    displayData();
  }
  if(start == false){
    displayData();
  }
  // VERY IMPORTANT OVERLAYS GUI
  gui();
  saveFrame("FV-" + startTime + "/####.tga"); //only for recording
}

