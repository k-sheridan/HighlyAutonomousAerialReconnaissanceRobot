#include <SPI.h>

//OPTICAL FLOW SENSOR CONST & VAR----------------------
#define MOTION_BURST 0x50
#define DELTAX 0x03
#define SS 10
#define MOSI 11
#define MISO 12
#define SCK 13

int8_t deltaX = 0;
int8_t deltaY = 0;
byte testByte;
byte SQUAL = 0;
unsigned long lastMicro;

void setup(){
  Serial.begin(9600);
  SPI.begin();
  OFInit();
}

void loop(){
  lastMicro = micros();
  OFReadMotion();
  Serial.print(deltaX);
  Serial.print(" , ");
  Serial.print(deltaY);
  Serial.print(" , ");
  Serial.println(SQUAL);
  //delay(10);
}
