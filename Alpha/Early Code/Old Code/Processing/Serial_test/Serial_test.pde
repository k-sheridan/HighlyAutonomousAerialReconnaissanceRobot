import processing.serial.*;
Serial port;

//COMMANDS - RECEIVE:
final char potRead = 'a';
final int BUFFER_LIMIT = 5;
int packetLoss = 0;
byte[] data = new byte[5];
boolean correctPacket = false;
int lastTimerHit = 0;
String readStatus = "GOOD";

//COMMANDS - SEND:
final char lcd_print = 'a';
final char int_print = 'b';
final char int_print_long = 'c';
//-------------------------------

int potVal = 0;

void setup()
{
  size (400, 400);
  port = new Serial(this, "COM3", 9600);
  port.bufferUntil('\n');
}
void draw()
{
  background(0);
  delay(50);
  textAlign(CENTER);
  textSize(26);
  text("POTENTIOMETER:", 200, 150);
  textSize(40);
  text(potVal, 200, 200);
  textSize(14);
  text("READ STATUS:", 150, 250);
  textAlign(LEFT);
  text(readStatus, 210, 250);
  text("READ ERROR COUNT:", 55, 280);
  text(packetLoss, 210, 280);

  if (mousePressed) {            //connect
    writePacket(int_print_long, mouseX);
  }
}

void serialEvent(Serial port) {
  println(port.available());
  readPacket();
  delay(50);
  writePacket(int_print_long, mouseX);
  port.clear();
}

