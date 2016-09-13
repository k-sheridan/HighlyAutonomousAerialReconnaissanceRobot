
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

int noRead = 0;

//CONTROL METHODS_____________________________________________________________________________________________________________________________
//GAINS
//pitch/roll
float PR_p = 0.2;
float PR_i = 0;
float PR_d = 0;
float const PR_mult = 8;

//pitch
double pitchSP, pitchInput , pitchOutput;
PID pitchPID(&pitchInput, &pitchOutput, &pitchSP, PR_p, PR_i, PR_d, DIRECT);

//roll
double rollSP, rollInput , rollOutput;
PID rollPID(&rollInput, &rollOutput, &rollSP, PR_p, PR_i, PR_d, DIRECT);

int const min_speed = 1170;
int motor_speed = 1135;
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

float true_pitch = 0;
float true_roll = 0;
float true_yaw = 0;

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
  pitchPID.SetMode(AUTOMATIC);
  pitchPID.SetOutputLimits(-250, 250);
  pitchPID.SetSampleTime(10);
  rollPID.SetMode(AUTOMATIC);
  rollPID.SetOutputLimits(-250, 250);
  rollPID.SetSampleTime(10);
  motor1.attach(20);
  motor2.attach(21);
  motor3.attach(22);
  motor4.attach(23);
  motor1.writeMicroseconds(1135);
  motor2.writeMicroseconds(1135);
  motor3.writeMicroseconds(1135);
  motor4.writeMicroseconds(1135);
}
void loop() {
  if (start == true) {
    // MODE_1

    if (mode == 1) {
      readGyro();
      readMagnet();
      readAccel();
      IR_read();
      ImuFilter(.99);
      if(connection == true){
        if (motor_speed >= 1170) {
          stabilize(pitch_lock, roll_lock);
        }
        else if (motor_speed < 1170) {
          m1_speed = motor_speed;
          m2_speed = motor_speed;
          m3_speed = motor_speed;
          m4_speed = motor_speed;
        }
      }
      else if(connection == false){
         motor_speed = 1200;
         pitch_lock = 0;
         roll_lock = 0;
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
      ImuFilter(.99);
    }
    //MODE_3

    if (mode == 3) {
      readGyro();
      readMagnet();
      readAccel();
      IR_read();
      ImuFilter(.99);
    }
    serialHandShake();
  }


  if (start == false) { // this is the section for when the quad is in standby mode
    m1_speed = 1135;
    m2_speed = 1135;
    m3_speed = 1135;
    m4_speed = 1135;
    motor1.writeMicroseconds(1135);
    motor2.writeMicroseconds(1135);
    motor3.writeMicroseconds(1135);
    motor4.writeMicroseconds(1135);
    readGyro();
    readMagnet();
    readAccel();
    IR_read();
    ImuFilter(.99);
    serialHandShake();
  }

  connectionCheck();
  //HERTZ
    counter++;
  

}


