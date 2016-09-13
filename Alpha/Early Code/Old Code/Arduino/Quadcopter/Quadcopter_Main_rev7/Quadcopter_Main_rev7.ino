
#include <Wire.h>
#include <Servo.h>
#include <PID_v1.h>


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
byte tuningParam = 1;

int noRead = 0;

//CONTROL METHODS_____________________________________________________________________________________________________________________________
//GAINS
//pitch/roll Attitude PID
float PR_Attitude_p = 0;
float PR_Attitude_i = 0;
float PR_Attitude_d = 0;

double pitchAttitudeSP, pitchAttitudeInput , pitchAttitudeOutput;
PID pitchAttitudePID(&pitchAttitudeInput, &pitchAttitudeOutput, &pitchAttitudeSP, PR_Attitude_p, PR_Attitude_i, PR_Attitude_d, DIRECT);

double rollAttitudeSP, rollAttitudeInput , rollAttitudeOutput;
PID rollAttitudePID(&rollAttitudeInput, &rollAttitudeOutput, &rollAttitudeSP, PR_Attitude_p, PR_Attitude_i, PR_Attitude_d, DIRECT);

//pitch/roll Rate PID
float PR_Rate_p = 0.02;
float PR_Rate_i = 0;
float PR_Rate_d = 0;

double pitchRateSP, pitchRateInput, pitchRateOutput;
PID pitchRatePID(&pitchRateInput, &pitchRateOutput, &pitchRateSP, PR_Rate_p, PR_Rate_i, PR_Rate_d, DIRECT);

double rollRateSP, rollRateInput, rollRateOutput;
PID rollRatePID(&rollRateInput, &rollRateOutput, &rollRateSP, PR_Rate_p, PR_Rate_i, PR_Rate_d, DIRECT);

//-----------------------------
int const min_speed = 1160;
int motor_speed = 1110;
int const max_speed =  2000;

float m1_speed;
float m2_speed;
float m3_speed;
float m4_speed;

Servo motor1; //7
Servo motor2; //6
Servo motor3; //5
Servo motor4; //3

// SHARP IR Distance Sensors

short dist_1;
short dist_2;
short dist_3;
short dist_4;

//ALTIMETER___________________________________________________--


// IMU VARIABLES______________________________________________________________________________________________________________________________________


#define ITG3205 0x68
#define ADLX345 0x53

#define HMC 0x1E

int last_time;

float pitch = 0;
float roll = 0;

float true_pitch = 0;
float true_roll = 0;
float true_yaw = 0;

float pitchBuff[4];
float rollBuff[4];

byte _buff[6]; //accel

char const Accel_power_control = 0x2D; //accel
char const Accel_data_format = 0x31;
char const Accel_data_x0 = 0x32;
char const Accel_data_x1 = 0x33;
char const Accel_data_y0 = 0x34;
char const Accel_data_y1 = 0x35;
char const Accel_data_z0 = 0X36;
char const Accel_data_z1 = 0x37;

short accel_x;
short accel_y;
short accel_z;

float accel_angle_x;
float accel_angle_y;

float accel_angle_x_last = 0;
float accel_angle_y_last = 0;

float accel_angle_x_filter;
float accel_angle_y_filter;

short gyro_x; //gyro
short gyro_y;
short gyro_z;
short gyro_temp;

float gyro_zero_valX;
float gyro_zero_valY;
float gyro_zero_valZ;

int g_offset_x = 0; //gyro
int g_offset_y = 0;
int g_offset_z = 0;

float g_pitch = 0;
float g_roll = 0;
float g_yaw = 0;

short mag_x; //magnetometer
short mag_y;
short mag_z;

float heading;


//SETTINGS______________________________________________________________________________________________________________________

short pitch_lock = 0;
short roll_lock = 0;
short yaw_lock = 0;
short alt_lock = 0;

int comMode = 1;

unsigned long counter = 0;
float hertz = 0.00;
//unsigned long lastMicro = 0;

boolean start = false;
int mode = 1;
int kill = 0; // "oh shit!" variable

void setup() {
  Wire.begin();
  //Serial.begin(9600);// delete if not debugging or programming!
  Serial1.begin(57600);
  //IMU intit
  ADLX_writeTo(Accel_data_format, 0x01);
  ADLX_writeTo(Accel_power_control, 0x08);
  delay(50);
  initGyro();
  delay(100);
  GyroCalibrate();
  MagnetInit();
  delay(50);
  //ATTITUDE CONTROLLERS
  pitchAttitudePID.SetMode(AUTOMATIC);
  pitchAttitudePID.SetOutputLimits(-250, 250);
  pitchAttitudePID.SetSampleTime(1);
  rollAttitudePID.SetMode(AUTOMATIC);
  rollAttitudePID.SetOutputLimits(-250, 250);
  rollAttitudePID.SetSampleTime(1);
  //RATE CONTROLLERS
  pitchRatePID.SetMode(AUTOMATIC);
  pitchRatePID.SetOutputLimits(-100, 100);
  pitchRatePID.SetSampleTime(1);
  rollRatePID.SetMode(AUTOMATIC);
  rollRatePID.SetOutputLimits(-100, 100);
  rollRatePID.SetSampleTime(1);
  //-----------------------------------
  motor1.attach(20);
  motor2.attach(21);
  motor3.attach(22);
  motor4.attach(23);
  motor1.writeMicroseconds(1110);
  motor2.writeMicroseconds(1110);
  motor3.writeMicroseconds(1110);
  motor4.writeMicroseconds(1110);
}
void loop() {
  if (start == true) {
    // MODE_1

    if (mode == 1) {
      readGyro();
      readMagnet();
      readAccel();
      IR_read();
      ImuFilter(.997);
      if(connection == true){
        if (motor_speed >= 1150) {
          stabilize(pitch_lock, roll_lock);
        }
        else if (motor_speed < 1150) {
          m1_speed = motor_speed;
          m2_speed = motor_speed;
          m3_speed = motor_speed;
          m4_speed = motor_speed;
        }
      }
      else if(connection == false){
         pitch_lock = 0;
         roll_lock = 0;
         if(noRead >= 600){
           motor_speed = 1200;
         }
         stabilize(pitch_lock, roll_lock);
         
      }
      motor1.writeMicroseconds(m1_speed);
      motor2.writeMicroseconds(m2_speed);
      motor3.writeMicroseconds(m3_speed);
      motor4.writeMicroseconds(m4_speed);
    }
    //MODE_2

    if (mode == 2) {
      readGyro();
      readMagnet();
      readAccel();
      IR_read();
      ImuFilter(.997);
    }
    //MODE_3

    if (mode == 3) {
      readGyro();
      readMagnet();
      readAccel();
      IR_read();
      ImuFilter(.997);
    }
    serialHandShake();
  }


  if (start == false) { // this is the section for when the quad is in standby mode
    m1_speed = 1110;
    m2_speed = 1110;
    m3_speed = 1110;
    m4_speed = 1110;
    motor1.writeMicroseconds(1110);
    motor2.writeMicroseconds(1110);
    motor3.writeMicroseconds(1110);
    motor4.writeMicroseconds(1110);
    readGyro();
    readMagnet();
    readAccel();
    IR_read();
    ImuFilter(.997);
    serialHandShake();
  }

  connectionCheck();
  //HERTZ
    //counter++;
  
}


