#include <Wire.h>
#include <Servo.h>
#include <SPI.h>
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
int const BUFFER_LIMIT = 50;

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
float PR_Attitude_p = 40;
float PR_Attitude_i = 3;
float PR_Attitude_d = 0;

double pitchAttitudeSP, pitchAttitudeInput , pitchAttitudeOutput;
PID pitchAttitudePID(&pitchAttitudeInput, &pitchAttitudeOutput, &pitchAttitudeSP, PR_Attitude_p, PR_Attitude_i, PR_Attitude_d, DIRECT);

double rollAttitudeSP, rollAttitudeInput , rollAttitudeOutput;
PID rollAttitudePID(&rollAttitudeInput, &rollAttitudeOutput, &rollAttitudeSP, PR_Attitude_p, PR_Attitude_i, PR_Attitude_d, DIRECT);

//pitch/roll Rate PID
float PR_Rate_p = 0.083;
float PR_Rate_i = 0.035;
float PR_Rate_d = 0.0007;

double pitchRateSP, pitchRateInput, pitchRateOutput;
PID pitchRatePID(&pitchRateInput, &pitchRateOutput, &pitchRateSP, PR_Rate_p, PR_Rate_i, PR_Rate_d, DIRECT);

double rollRateSP, rollRateInput, rollRateOutput;
PID rollRatePID(&rollRateInput, &rollRateOutput, &rollRateSP, PR_Rate_p, PR_Rate_i, PR_Rate_d, DIRECT);

//yaw rate PID:
float Y_Rate_p = 0.200;
float Y_Rate_i = 0.100;
float Y_Rate_d = 0;

double yawRateSP, yawRateInput, yawRateOutput;
PID yawRatePID(&yawRateInput, &yawRateOutput, &yawRateSP, Y_Rate_p, Y_Rate_i, Y_Rate_d, DIRECT);

//altHold------------------------------------------------
float Alt_p = 0.000;
float Alt_i = 0.000;
float Alt_d = 0.000;

double altHoldSP, altHoldInput, altHoldOutput;
PID altHoldPID(&altHoldInput, &altHoldOutput, &altHoldSP, Alt_p, Alt_i, Alt_d, DIRECT);

//position hold--------------------------------------------
#define MOTION_BURST 0x50
#define DELTAX 0x03
#define SS 10
#define MOSI 11
#define MISO 12
#define SCK 13

int8_t Raw_dX = 0;
int8_t Raw_dY = 0;
float dX = 0;
float dY = 0;
float xPos;
float yPos;
float deltaPitch = 0;
float deltaRoll = 0;
float lastPitch = 0;
float lastRoll = 0;
byte SQUAL = 0;
byte OFconfig;
int OF_FOV = 130; //play with
int OF_Scalar = 5; //these
//END OF CONTROL METHODS--------------------_______________________________-----------------------------------
//-----------------------------
int const min_speed = 1160;
int motor_speed = 1110;
int const max_speed =  2000;
int revDownTimer = 0;

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

//SONAR_____________________________________
short relAlt = 0;
String inString;
short ZaccelSample = 0;
short ZaccelSampleCount = 0;
short sonarReading = 0;
short sonarSample[3];
boolean sonarRead = false;
unsigned long lastSonarMillis = 0;
short deltaSonarMillis = 0;
/* Inertial Filter */
float Zaccel = 0;
float Zsonar = 0;

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
byte alt_lock = 0;

int comMode = 1;

unsigned long lastMicros = 0;
short deltaMicros = 0;

boolean start = false;
int mode = 1;
int kill = 0; // "uh oh!" variable

void setup() {
  Wire.begin();
  //Serial.begin(9600);// delete if not debugging or programming!
  Serial1.begin(57600);
  //Optical flow init
  SPI.begin();
  OFInit();
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
  pitchAttitudePID.SetOutputLimits(-500, 500);
  pitchAttitudePID.SetSampleTime(1);
  rollAttitudePID.SetMode(AUTOMATIC);
  rollAttitudePID.SetOutputLimits(-500, 500);
  rollAttitudePID.SetSampleTime(1);
  //RATE CONTROLLERS
  pitchRatePID.SetMode(AUTOMATIC);
  pitchRatePID.SetOutputLimits(-100, 100);
  pitchRatePID.SetSampleTime(1);
  rollRatePID.SetMode(AUTOMATIC);
  rollRatePID.SetOutputLimits(-100, 100);
  rollRatePID.SetSampleTime(1);
  yawRatePID.SetMode(AUTOMATIC);
  yawRatePID.SetOutputLimits(-200, 200);
  yawRatePID.SetSampleTime(1);
  altHoldPID.SetMode(AUTOMATIC);
  altHoldPID.SetOutputLimits(1160, 1900);
  altHoldPID.SetSampleTime(1);
  //-----------------------------------
  sonarInit(); // INITIALIZE SONAR  
  //SETUP MOTORS
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

    if (mode == 1) { // MODE DESIGNED FOR NORMAL CONTROLLED FLIGHT WITH CONTROLLER
      //READ FROM IMU
      readGyro();
      readMagnet();
      readAccel();
      //READ SONAR DATA
      readSonar();
      InertialSonarFilter();
      //READ OPTICAL FLOW DATA
      OFReadMotion();
      OFCalculatePosition();
      //READ DISTANCE SENSORS
      IR_read();
      //FILTER IMU DATA
      ImuFilter(.997);
      if(connection == true){  //NORMAL MOTOR OPTION IF MOTORS ARE CONNECTED
        if (motor_speed >= 1150) {
          stabilize(pitch_lock, roll_lock, yaw_lock);
        }
        else if (motor_speed < 1150) { 
          m1_speed = motor_speed;
          m2_speed = motor_speed;
          m3_speed = motor_speed;
          m4_speed = motor_speed;
        }
      }
      else if(connection == false){  //DIRECT CONTROL DISCONNECTION FAILSAFE
        pitch_lock = 0; // AS SOON AS DISCONNECTED THE QUAD WILL LEVEL ITSELF OUT
        roll_lock = 0;
        yaw_lock = 0;
        if(noRead >= 600){ // IF DISCONNECTED FOR ABOUT 2 SECONDS
          if(motor_speed > 1110){
            if(revDownTimer >= 5){ // SLOWLY REV DOWN THE MOTORS; WAITS 5 CYCLES THEN SUBTRACTS 1 FROM MOTOR SPEED 
              motor_speed--;
              revDownTimer = 0;
            }
            revDownTimer++;
          }
        }
        stabilize(pitch_lock, roll_lock, yaw_lock);
      }
      //APPLY SPEED CHANGES TO MOTORS
      motor1.writeMicroseconds(m1_speed);
      motor2.writeMicroseconds(m2_speed);
      motor3.writeMicroseconds(m3_speed);
      motor4.writeMicroseconds(m4_speed);
    }
    //MODE_2

    if (mode == 2) { //MODE DESIGNED FOR SEMI-AUTONOMOUS FLIGHT USES GPS, OPTICAL FLOW, SONAR AND IMU
      readGyro();
      readMagnet();
      readAccel();
      readSonar();
      InertialSonarFilter();
      OFReadMotion();
      OFCalculatePosition();
      IR_read();
      ImuFilter(.997);
    }
    //MODE_3

    if (mode == 3) { // MODE DESIGNED FOR FULLY AUTONOMOUS FLIGHT
      readGyro();
      readMagnet();
      readAccel();
      readSonar();
      InertialSonarFilter();
      OFReadMotion();
      OFCalculatePosition();
      IR_read();
      ImuFilter(.997);
    }

    // MODE 4
    if(mode == 4){ // MODE DESIGNED FOR TUNING VAROIUS PID SETTINGS
      readGyro();
      readMagnet();
      readAccel();
      readSonar();
      InertialSonarFilter();
      OFReadMotion();
      OFCalculatePosition();
      IR_read();
      ImuFilter(.997);
      if(connection == true){  //NORMAL MOTOR OPTION IF MOTORS ARE CONNECTED
        if (motor_speed >= 1150) {
          tuningStabilize(pitch_lock, roll_lock, yaw_lock);
        }
        else if (motor_speed < 1150) { 
          m1_speed = motor_speed;
          m2_speed = motor_speed;
          m3_speed = motor_speed;
          m4_speed = motor_speed;
        }
      }
      else if(connection == false){  //TUNING MODE DISCONNECTION FAILSAFE
        pitch_lock = 0; // AS SOON AS DISCONNECTED THE QUAD WILL LEVEL ITSELF OUT
        roll_lock = 0;
        yaw_lock = 0;
        if(noRead >= 600){ // IF DISCONNECTED FOR ABOUT 2 SECONDS
          if(motor_speed > 1110){
            if(revDownTimer >= 5){ // SLOWLY REV DOWN THE MOTORS; WAITS 5 CYCLES THEN SUBTRACTS 1 FROM MOTOR SPEED 
              motor_speed--;
              revDownTimer = 0;
            }
            revDownTimer++;
          }
        }
        stabilize(pitch_lock, roll_lock, yaw_lock);
      }
      //APPLY SPEED CHANGES TO MOTORS
      motor1.writeMicroseconds(m1_speed);
      motor2.writeMicroseconds(m2_speed);
      motor3.writeMicroseconds(m3_speed);
      motor4.writeMicroseconds(m4_speed);
    }
    serialHandShake(); //RUNS A CHECK FOR SERIAL DATA THEN WRITES A PACKET
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
    readSonar();
    InertialSonarFilter();
    OFReadMotion();
    OFCalculatePosition();
    IR_read();
    ImuFilter(.997);
    serialHandShake();
  }
  //WILL CHECK FOR A CONNECTION EVERY CYCLE
  connectionCheck();
  deltaMicros = micros() - lastMicros; //CYCLE TIME 
  lastMicros = micros();  
  //------------------------  
}






