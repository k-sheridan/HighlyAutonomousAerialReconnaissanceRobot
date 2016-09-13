
#include <Wire.h>


float GyroX,GyroY,GyroZ,GyroTemp;

int g_offx = 0;
int g_offy = 0;
int g_offz = 0;

#define ITG3200_Address 0x68

void setup()
{
  pinMode(13, OUTPUT);  
  Wire.begin(); 
  Serial.begin(115200);
  delay(100);
  initGyro();
  delay(100);
  GyroCalibrate();
  delay(100);
 
}


void loop()
{
   digitalWrite(13, LOW);
   delay(50);
   digitalWrite(13, HIGH);
  
   GyroRead();
   
  

}



// **************************
// I2C Gyroscope ITG3200 
// **************************
void initGyro() {
   Wire.beginTransmission(ITG3200_Address); 
   Wire.write(0x3E);  
   Wire.write(0x00);   
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3200_Address); 
   Wire.write(0x15);  
   Wire.write(0x07);   
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3200_Address); 
   Wire.write(0x16);  
   Wire.write(0x1E);   // +/- 2000 dgrs/sec, 1KHz, 1E, 19
   Wire.endTransmission(); 
   
   Wire.beginTransmission(ITG3200_Address); 
   Wire.write(0x17);  
   Wire.write(0x00);   
   Wire.endTransmission(); 
   
   
}

void GyroCalibrate(){

 int tmpx = 0;
 int tmpy = 0;
 int tmpz = 0; 

 g_offx = 0;
 g_offy = 0;
 g_offz = 0;
 
 for (char i = 0;i<10;i++)
    {
    delay(10);  
    GyroRead();
    tmpx += GyroX;
    tmpy += GyroY;
    tmpz += GyroZ; 
    }  
 g_offx = tmpx/10;
 g_offy = tmpy/10;
 g_offz = tmpz/10;
 
 
  
}


void GyroRead() {
  Wire.beginTransmission(ITG3200_Address); 
  Wire.write(0x1B);       
  Wire.endTransmission(); 
  
  Wire.beginTransmission(ITG3200_Address); 
  Wire.requestFrom(ITG3200_Address, 8);    
  
  int i = 0;
  byte buff[8];
  while(Wire.available())    
  { 
    buff[i] = Wire.read(); 
    i++;
  }
  Wire.endTransmission(); 
    
  GyroX = ((buff[4] << 8) | buff[5]) - g_offx;
  GyroY = ((buff[2] << 8) | buff[3]) - g_offy;
  GyroZ = ((buff[6] << 8) | buff[7]) - g_offz;
  GyroTemp = (buff[0] << 8) | buff[1]; 
 
  Serial.print("Gyro(degree/s): ");
  Serial.print(GyroX / 14.375);
  Serial.print(", ");
  Serial.print(GyroY / 14.375);
  Serial.print(", ");
  Serial.print(GyroZ / 14.375);
  Serial.print("     Temperature: ");
  Serial.println(35+((GyroTemp+13200) / 280)) ; 
 }


