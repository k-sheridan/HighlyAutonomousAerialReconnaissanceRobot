#include <Wire.h>

#define ADLX345 0x53

byte _buff[6];

char Accel_power_control = 0x2D;
char Accel_data_format = 0x31;
char Accel_data_x0 = 0x32;
char Accel_data_x1 = 0x33;
char Accel_data_y0 = 0x34;
char Accel_data_y1 = 0x35;
char Accel_data_z0 = 0X36;
char Accel_data_z1 = 0x37;
int readNum = 0;

void setup(){
  
  Wire.begin();
  Serial.begin(115200);
  
  //ADLX345 initialize
  ADLX_writeTo(Accel_data_format, 0x01);
  
  ADLX_writeTo(Accel_power_control, 0x08);
  
  Serial.println("initialize");
}

void loop(){
  
  readAccel();
  readNum++;
  delay(100);
}

void ADLX_writeTo(byte address, byte val){
  
  Wire.beginTransmission(ADLX345);
  Wire.write(address);
  Wire.write(val);
  Wire.endTransmission();

}

void ADLX_readFrom(byte address, int num, byte _buff[]){
  
  Wire.beginTransmission(ADLX345);
  Wire.write(address);
  Wire.endTransmission();
  
  Wire.beginTransmission(ADLX345);
  Wire.requestFrom(ADLX345, num);
  
  int i = 0;
  while(Wire.available()){
    _buff[i] = Wire.read();
    i++;
  }
  Wire.endTransmission();
}

void readAccel(){
  uint8_t howManyBytesToRead = 6;
  ADLX_readFrom(Accel_data_x0, howManyBytesToRead, _buff);
  
  int x = (((int)_buff[1]) << 8) | _buff[0];
  int y = (((int)_buff[3]) << 8) | _buff[2];
  int z = (((int)_buff[5]) << 8) | _buff[4];

  Serial.print(" X: ");
  Serial.print( x );
  Serial.print(" Y: ");
  Serial.print( y );
  Serial.print(" Z: ");
  Serial.print( z );
  Serial.println("        READ NUM:      ");
  
  
}


