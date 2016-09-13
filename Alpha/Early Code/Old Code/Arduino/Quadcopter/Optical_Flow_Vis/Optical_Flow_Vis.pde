import processing.serial.*;

Serial port;

int xPos = 0;
int yPos = 0;
int dx, dy;
int SQUAL;

int x[] = new int[10];
int y[] = new int[10];

void setup() {
  size(700, 700);
  port = new Serial(this, Serial.list()[0], 9600); 
  port.bufferUntil(10);
}

void draw() {
  background(0);
  fill(255);
  stroke(255, 255, 255, 40);
  strokeWeight(1);
  for (int i = 0; i <= width; i += 50) {
    line(i, 0, i, height);
  }
  for (int i = 0; i <= width; i += 50) {
    line(0, i, width, i);
  }
  stroke(255);
  strokeWeight(2);
  line(width / 2, 0, width / 2, height);
  line(0, height / 2, width, height / 2);
  textSize(40);
  fill(255, 0, 0);
  ellipse(width / 2 + (xPos * 2.5), height / 2 + (yPos * 2.5), 20, 20);
  fill(255);
  for(int i = 0; i < 9; i++){
    line(350 + x[i] * 2.5,350 + y[i] * 2.5,350 + x[i + 1] * 2.5,350 + y[i + 1] * 2.5);
  }
  text("X:", 550, 50);
  text(xPos, 600, 50);
  text("Y:", 550, 90);
  text(yPos, 600, 90);
  text("SQUAL:", 450, 130);
  text(SQUAL, 600, 130);
  text("dX:", 520, 170);
  text(dx, 600, 170);
  text("dY:", 520, 210);
  text(dy, 600, 210);
  saveFrame("OFV" + minute() + "/####.tga");
}

void serialEvent(Serial port) {
  int[] data = int(split(port.readString(), ","));
  dx = data[0];
  //dy = data[1];
  SQUAL = data[2];
  xPos = data[3];
  yPos = data[1];
  x[9] = x[8];
  x[8] = x[7];
  x[7] = x[6];
  x[6] = x[5];
  x[5] = x[4];
  x[4] = x[3];
  x[3] = x[2];
  x[2] = x[1];
  x[1] = x[0];
  x[0] = xPos;
  y[9] = y[8];
  y[8] = y[7];
  y[7] = y[6];
  y[6] = y[5];
  y[5] = y[4];
  y[4] = y[3];
  y[3] = y[2];
  y[2] = y[1];
  y[1] = y[0];
  y[0] = yPos;
}

void mousePressed() { 
  
} 

