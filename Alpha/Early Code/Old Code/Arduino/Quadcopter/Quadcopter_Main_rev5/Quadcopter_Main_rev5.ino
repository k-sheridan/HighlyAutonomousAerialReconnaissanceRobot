#include <Wire.h>
#include <PID_v1.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP085_U.h>

//COMMUNICATION PROTOCOLS____________________________________________________________________________________________________________________
//SEND PACKET----------------------------------------


//RECIEVE PACKET--------------------------------------------
enum RECEIVE_COMMANDS {       //a,b,c commands in that order(use these characters!)
  COMMAND = 'a',
  PITCH_ROLL_LOCK,
  YAW_ALT_LOCK,

};

byte packetLoss = 0;
int const BUFFER_LIMIT = 24;

byte data[BUFFER_LIMIT];

boolean correctPacket = false;
boolean readComplete = false;

boolean firstRead = false;
boolean connection = false;

int noRead = 0;

//CONTROL METHODS_____________________________________________________________________________________________________________________________
//GAINS
//pitch/roll
float PR_p = 0.2;
float PR_i = 0;
float PR_d = 0;

//pitch
double pitchSP, pitchInput , pitchOutput;
PID pitchPID(&pitchInput, &pitchOutput, &pitchSP, PR_p, PR_i, PR_d, DIRECT);

//roll
double rollSP, rollInput , rollOutput;
PID rollPID(&rollInput, &rollOutput, &rollSP, PR_p, PR_i, PR_d, DIRECT);

int min_speed = 147;
int motor_speed = 140;
int max_speed = 254;

float m1_speed;
float m2_speed;
float m3_speed;
float m4_speed;

int motor1 = 7;
int motor2 = 6;
int motor3 = 5;
int motor4 = 3;

// SHARP IR Distance Sensors

int dist_1;
int dist_2;
int dist_3;
int dist_4;

//ALTIMETER___________________________________________________--

Adafruit_BMP085_Unified bmp = Adafruit_BMP085_Unified(10085);

float true_altitude;


// IMU VARIABLES______________________________________________________________________________________________________________________________________


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


//SETTINGS______________________________________________________________________________________________________________________

short pitch_lock = 0;
short roll_lock = 0;
short yaw_lock = 0;
short alt_lock = 0;

int comMode = 1;

boolean start = false;
int mode = 1;
int kill = 0; // "oh shit!" variable

void setup() {
  Wire.begin();
  //Serial.begin(9600);// delete if not debugging or programming!
  Serial1.begin(57600);
  bmp.begin();
  //IMU intit
  ADLX_writeTo(Accel_data_format, 0x01);
  ADLX_writeTo(Accel_power_control, 0x08);
  delay(100);
  initGyro();
  delay(100);
  GyroCalibrate();
  MagnetInit();
  delay(250);
  pitchPID.SetMode(AUTOMATIC);
  pitchPID.SetOutputLimits(-30, 30);
  rollPID.SetMode(AUTOMATIC);
  rollPID.SetOutputLimits(-30, 30);
  analogWrite(motor1, 140);
  analogWrite(motor2, 140);
  analogWrite(motor3, 140);
  analogWrite(motor4, 140);
}
void loop() {
  if (start == true) {
    // MODE_1

    if (mode == 1) {
      readGyro();
      readMagnet();
      readAccel();
      true_altitude = BMP_read();
      IR_read();
      ImuFilter(.99);
      if (motor_speed >= 147) {
        stabilize(pitch_lock, roll_lock);
      }
      else if (motor_speed < 147) {
        m1_speed = motor_speed;
        m2_speed = motor_speed;
        m3_speed = motor_speed;
        m4_speed = motor_speed;
      }
      analogWrite(motor1, m1_speed);
      analogWrite(motor2, m2_speed);
      analogWrite(motor3, m3_speed);
      analogWrite(motor4, m4_speed);
    }
    //MODE_2

    if (mode == 2) {
      readGyro();
      readMagnet();
      readAccel();
      true_altitude = BMP_read();
      IR_read();
      ImuFilter(.99);
    }
    //MODE_3

    if (mode == 3) {
      readGyro();
      readMagnet();
      readAccel();
      true_altitude = BMP_read();
      IR_read();
      ImuFilter(.99);
    }

    serialHandShake();
  }


  if (start == false) { // this is the section for when the quad is in standby mode
    m1_speed = 140;
    m2_speed = 140;
    m3_speed = 140;
    m4_speed = 140;
    analogWrite(motor1, m1_speed);
    analogWrite(motor2, m2_speed);
    analogWrite(motor3, m3_speed);
    analogWrite(motor4, m4_speed);
    readGyro();
    readMagnet();
    readAccel();
    true_altitude = BMP_read();
    IR_read();
    ImuFilter(.99);
    serialHandShake();
  }

  connectionCheck();
//  Serial.print(true_pitch);
//  Serial.print(" ");
//  Serial.print(dist_1);
//  Serial.print(" ");
//  Serial.print(dist_2);
//  Serial.print(" ");
//  Serial.print(dist_3);
//  Serial.print(" ");
//  Serial.println(dist_4);
}

