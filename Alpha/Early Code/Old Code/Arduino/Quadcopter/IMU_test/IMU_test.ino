#include <Wire.h>

#define ITG3205 0x68
#define ADLX345 0x53
#define HMC 0x1E

byte _buff[6]; //accel

char Accel_power_control = 0x2D; //accel
char Accel_data_format = 0x31;
char Accel_data_x0 = 0x32;
char Accel_data_x1 = 0x33;
char Accel_data_y0 = 0x34;
char Accel_data_y1 = 0x35;
char Accel_data_z0 = 0X36;
char Accel_data_z1 = 0x37;

short gyro_x; //gyro
short gyro_y;
short gyro_z;
short gyro_temp;

int g_offset_x = 0; //gyro
int g_offset_y = 0;
int g_offset_z = 0;

int mag_x;
int mag_y;
int mag_z;

int counter;
void setup(){
  
  Wire.begin();
  Serial.begin(115200);
  
  //ADLX INITIALIZE
  ADLX_writeTo(Accel_data_format, 0x01);
  ADLX_writeTo(Accel_power_control, 0x08);
  //ITG INITIALIZE
  delay(100);
  initGyro();
  delay(100);
  GyroCalibrate();
  MagnetInit();
  
}

void loop(){
  
  //readAccel();
  readGyro();
  //readMagnet();
  Serial.println("");
  counter++;
  Serial.print(counter);
  Serial.print("  ");
  Serial.println(millis()); 
}

//ACCELEROMETER----------------------------------------------------------------------

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
  
  short x = (((int)_buff[1]) << 8) | _buff[0];
  short y = (((int)_buff[3]) << 8) | _buff[2];
  short z = (((int)_buff[5]) << 8) | _buff[4];

  Serial.print("ACCELEROMETER");
  Serial.print(" X: ");
  Serial.print( x );
  Serial.print(" Y: ");
  Serial.print( y );
  Serial.print(" Z: ");
  Serial.print( z );
  
}

//GYROSCOPE--------------------------------------------------------------------

void GyroCalibrate(){

 int tmpx = 0;
 int tmpy = 0;
 int tmpz = 0; 

 g_offset_x = 0;
 g_offset_y = 0;
 g_offset_z = 0;
 
 for (char i = 0;i<10;i++)
    {
    delay(10);  
    readGyro();
    tmpx += gyro_x;
    tmpy += gyro_y;
    tmpz += gyro_z; 
    }  
 g_offset_x = tmpx/10;
 g_offset_y = tmpy/10;
 g_offset_z = tmpz/10;
}


void readGyro() {
  Wire.beginTransmission(ITG3205); 
  Wire.write(0x1B);       
  Wire.endTransmission(); 
  
  Wire.beginTransmission(ITG3205); 
  Wire.requestFrom(ITG3205, 8);    
  
  int i = 0;
  byte buff[8];
  while(Wire.available())    
  { 
    buff[i] = Wire.read(); 
    i++;
  }
  Wire.endTransmission(); 
    
  gyro_x = ((buff[4] << 8) | buff[5]) - g_offset_x;
  gyro_y = ((buff[2] << 8) | buff[3]) - g_offset_y;
  gyro_z = ((buff[6] << 8) | buff[7]) - g_offset_z;
  gyro_temp = (buff[0] << 8) | buff[1]; 
 
  Serial.print("    GYROSCOPE  ");
  Serial.print(" PITCH: ");
  Serial.print(gyro_x);
  Serial.print(" ROLL: ");
  Serial.print(gyro_y);
  Serial.print(" YAW: ");
  Serial.print(gyro_z / 14.375);
  Serial.print("     TEMP:");
  Serial.print((35+(gyro_temp+13200) / 280)) ; 
 }
 
void initGyro() {
   Wire.beginTransmission(ITG3205); 
   Wire.write(0x3E);  
   Wire.write(0x00);   
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3205); 
   Wire.write(0x15);  
   Wire.write(0x07);   
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3205); 
   Wire.write(0x16);  
   Wire.write(0x1E);   // +/- 2000 dgrs/sec, 1KHz, 1E, 19
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3205); 
   Wire.write(0x17);  
   Wire.write(0x00);   
   Wire.endTransmission();
   
}

//MAGNETOMETER-----------------------------------------------------------------------------------------

void MagnetInit(){
  Wire.beginTransmission(HMC);
  Wire.write(0x02);
  Wire.write(0x00);
  Wire.endTransmission();
}

void readMagnet(){
  Wire.beginTransmission(HMC);
  Wire.write(0x03); //select register 3, X MSB register
  Wire.endTransmission();
 
  Wire.requestFrom(HMC, 6);
  if(6<=Wire.available()){
    mag_x = Wire.read()<<8; //X msb
    mag_x |= Wire.read(); //X lsb
    mag_z = Wire.read()<<8; //Z msb
    mag_z |= Wire.read(); //Z lsb
    mag_y = Wire.read()<<8; //Y msb
    mag_y |= Wire.read(); //Y lsb
  }
  
  Serial.print("  MAGNETOMETER:  ");
  Serial.print("x: ");
  Serial.print(mag_x);
  Serial.print("  y: ");
  Serial.print(mag_y);
  Serial.print("  z: ");
  Serial.print(mag_z);
}



