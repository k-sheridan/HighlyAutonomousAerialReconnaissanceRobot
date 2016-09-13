#include <Wire.h>

float last_p_error = 0;
float p_error_speed;
float i_adder = 0;

int min_speed = 147;
int motor_speed = 147;
int max_speed = 170;

float m1_speed;
float m2_speed;
float m3_speed;
float m4_speed;

int motor1 = 3;
int motor2 = 9;
int motor3 = 10;
int motor4 = 11;

boolean start = false;

#define ITG3205 0x68
#define ADLX345 0x53

#define HMC 0x1E

int last_time;

float true_pitch = 0;
float true_roll = 0;
float true_yaw = 0;

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

float heading;

//settings:

boolean accel_print = false;
boolean gyro_print = false;
boolean magnet_print = false;
boolean Imu_print = true;

int pitch_lock = 0;

float p_mult = .03;
float i_mult = .002;
float d_mult = 2.00;

void setup() {
  Wire.begin();
  Serial.begin(9600);
  //IMU intit
  ADLX_writeTo(Accel_data_format, 0x01);
  ADLX_writeTo(Accel_power_control, 0x08);
  delay(100);
  initGyro();
  delay(100);
  GyroCalibrate();
  MagnetInit();
  delay(250);
  analogWrite(motor1, 140);
  analogWrite(motor2, 140);
  analogWrite(motor3, 140);
  analogWrite(motor4, 140);
}
void loop() {
  if (start == true) {
    readGyro();
    readMagnet();
    readAccel();
    ImuFilter(.97);
    stabilize(pitch_lock);
  }
  if (start == false) {
    analogWrite(motor1, 140);
    analogWrite(motor2, 140);
    analogWrite(motor3, 140);
    analogWrite(motor4, 140);
    readGyro();
    readMagnet();
    readAccel();
    ImuFilter(.97);
  }
}

