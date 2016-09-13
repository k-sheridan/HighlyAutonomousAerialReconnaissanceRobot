#include <Wire.h>

#define ITG3205 0x68
#define ADLX345 0x53
#define HMC 0x1E

int last_time;

int true_pitch = 0;
int true_roll = 0;
int true_yaw = 0;

float speed_mult = .07;
int angle; 
int d_speed;

boolean accel_print = false;
boolean gyro_print = false;
boolean magnet_print = false;
boolean Imu_print = false;

byte _buff[6]; //accel

char Accel_power_control = 0x2D; //accel
char Accel_data_format = 0x31;
char Accel_data_x0 = 0x32;
char Accel_data_x1 = 0x33;
char Accel_data_y0 = 0x34;
char Accel_data_y1 = 0x35;
char Accel_data_z0 = 0X36;
char Accel_data_z1 = 0x37;

int accel_x;
int accel_y;
int accel_z;

float accel_angle_x;
float accel_angle_y;

float accel_angle_x_last = 0;
float accel_angle_y_last = 0;

float accel_angle_x_filter;
float accel_angle_y_filter;

float gyro_x; //gyro
float gyro_y;
float gyro_z;
float gyro_temp;

float gyro_zero_valX;
float gyro_zero_valY;
float gyro_zero_valZ;

int g_offset_x = 0; //gyro
int g_offset_y = 0;
int g_offset_z = 0;

float g_pitch = 0;
float g_roll = 0;
float g_yaw = 0;

int mag_x; //magnetometer
int mag_y;
int mag_z;

int heading;

void setup(){
  
  Wire.begin();
  Serial.begin(115200);
  
  pinMode(3, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(9, OUTPUT);
  analogWrite(3, 140);
  analogWrite(9, 140);
  analogWrite(10, 140);
  analogWrite(11, 140);
  delay(5000);
  
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
  readGyro();
  readMagnet();
  readAccel();
  ImuFilter(1);
  ImuPrint(accel_print, gyro_print, magnet_print, Imu_print);
  stabilize();
  delay(0);
}

//ACCELEROMETER----------------------------------------------------------------------
void stabilize(){
 if(true_pitch << 180){
  angle = true_pitch;
 } 
 if(true_pitch >= 180){
  angle = 360 - true_pitch;
 }
 //Serial.println(angle);
 if(true_pitch < 180 && true_pitch >= 0){
   d_speed = angle * speed_mult;
   d_speed += 187;
   if(d_speed > 200){
     d_speed = 200;
   }
   analogWrite(9, d_speed);
   analogWrite(10, d_speed);
   Serial.print("B & C: ");
   Serial.println(d_speed);
   analogWrite(3, 185);
   analogWrite(11, 185);
 }
 else if(true_pitch >= 180 && true_pitch <= 359){
   d_speed = angle * speed_mult;
   d_speed += 185;
   if(d_speed > 200){
     d_speed = 200;
   }
   analogWrite(9, 187);
   analogWrite(10, 187);
   analogWrite(3, d_speed);
   analogWrite(11, d_speed);
   Serial.print("   A & D: ");
   Serial.println(d_speed); 
 }
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
  
  accel_x = (((int)_buff[1]) << 8) | _buff[0];
  accel_y = (((int)_buff[3]) << 8) | _buff[2];
  accel_z = (((int)_buff[5]) << 8) | _buff[4];
  
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
   Wire.write(0x1E);   
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
}

//IMU FILTER------------------------------------------------------

void ImuPrint(boolean accelerometer, boolean gyroscope, boolean magnetometer, boolean imu){
  if(accelerometer == true){
  Serial.print("ACCELEROMETER ");
  Serial.print(" X: ");
  Serial.print( accel_angle_x );
  Serial.print(" Y: ");
  Serial.print( accel_angle_y );
  Serial.print(" Z: ");
  Serial.print( accel_z );
  }
  
  if(gyroscope == true){
  Serial.print(" GYROSCOPE ");
  Serial.print(" PITCH: ");
  Serial.print(g_pitch);
  Serial.print(" ROLL: ");
  Serial.print(g_roll);
  Serial.print(" YAW: ");
  Serial.print(g_yaw);
  }
  
  if(magnetometer == true){
  Serial.print(" MAGNETOMETER ");
  Serial.print("x: ");
  Serial.print(mag_x);
  Serial.print("  y: ");
  Serial.print(mag_y);
  Serial.print("  z: ");
  Serial.print(mag_z);
  }
  
  if(imu == true){
  Serial.print("  IMU:  ");
  Serial.print(" Pitch: ");
  Serial.print(true_pitch);
  Serial.print(" Roll: ");
  Serial.print(true_roll);
  Serial.print(" Yaw: ");
  Serial.print(true_yaw);
  }
  
  Serial.println("");
}

void ImuFilter(float tilt_bias){
//Magnetometer-------------------

  if(mag_y > 0){
    heading = 90.0 - ((atan(mag_x / mag_y)) * 180.0 / 3.141592);
  }
  if(mag_y < 0){
    heading = 270.0 - ((atan(mag_x / mag_y)) * 180.0 / 3.141592);
  }
  if(mag_y == 0 && mag_x < 0){
    heading = 180.0;
  }
  if(mag_y == 0 && mag_x > 0){
    heading = 0.0;
  }
//accel------------------

  accel_angle_x = atan( accel_x / (sqrt(pow(accel_y, 2) + pow(accel_z, 2))));
  accel_angle_x = accel_angle_x * (180.0 / 3.141592);
  
  accel_angle_y = atan( accel_y / (sqrt(pow(accel_x, 2) + pow(accel_z, 2))));
  accel_angle_y = accel_angle_y * (180.0 / 3.141592);
  
  accel_angle_x_filter = (accel_angle_x + accel_angle_x_last) / 2;
  accel_angle_y_filter = (accel_angle_y + accel_angle_y_last) / 2;
  
  accel_angle_x_last = accel_angle_x;
  accel_angle_y_last = accel_angle_y;
  
//Gyro------------------
  int delta_time;
  delta_time = millis() - last_time;
  
  g_pitch += (gyro_x / 14.375) / (1000 / delta_time);
  g_roll += (gyro_y / 14.375) / (1000 / delta_time);
  g_yaw += (gyro_z / 14.375) / (1000 / delta_time);
  
  last_time = millis();
  
  if(g_yaw < 0){
    g_yaw = g_yaw + 360;
  }
  if(g_yaw >= 360){
    g_yaw = g_yaw - 360;
  }
  if(g_pitch < 0){
    g_pitch = g_pitch + 360;
  }
  if(g_pitch >= 360){
    g_pitch = g_pitch - 360;
  }
  if(g_roll < 0){
    g_roll = g_roll + 360;
  }
  if(g_roll >= 360){
    g_roll = g_roll - 360;
  }
  
  if(accel_angle_x == 0 && accel_z >= 0){
    g_pitch = 0;
  }
  if(accel_angle_y == 0 && accel_z >= 0){
    g_roll = 0;
  }
  if(accel_z < 0){
    true_pitch = g_pitch;
    true_roll = g_roll; 
  }
  if(accel_z >= 0){
    true_pitch = tilt_bias * g_pitch + (1 - tilt_bias) * accel_angle_x;
    true_roll = tilt_bias * g_roll + (1 - tilt_bias) * accel_angle_y;
  }
  
  if(heading >= 0 && heading <=10 && g_yaw != heading && true_pitch <= 5 && true_pitch >= 355 && true_roll <= 5 && true_roll >= 355){
    g_yaw = heading;
    true_yaw = heading;
  }
  if(heading >= 345 && heading <= 355 && g_yaw != heading && true_pitch <= 5 && true_pitch >= 355 && true_roll <= 5 && true_roll >= 355){
    g_yaw = heading;
    true_yaw = heading;
  }
  if(heading >= 165 && heading <= 190 && g_yaw != heading && true_pitch <= 5 && true_pitch >= 355 && true_roll <= 5 && true_roll >= 355){
    g_yaw = heading;
    true_yaw = heading;
  }
  else{
    true_yaw = g_yaw;
  }
}
