#include <Wire.h>
#include <Servo.h>
#include <SPI.h>
#include <PID_v1.h>


//COMMUNICATION PROTOCOLS____________________________________________________________________________________________________________________
int bytesAvailable = 0;
boolean connection = false;
int connectionCounter = 0;

//CONTROL METHODS_____________________________________________________________________________________________________________________________
//GAINS
float p, i, d;
int pid_mode;
boolean pid_sync;

//pitch/roll Attitude PID
float PR_Attitude_p = 20;
float PR_Attitude_i = 7;
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
float Alt_p = 1.500;
float Alt_i = 3.000;
float Alt_d = 0.000;

double altHoldSP, altHoldInput, altHoldOutput;
PID altHoldPID(&altHoldInput, &altHoldOutput, &altHoldSP, Alt_p, Alt_i, Alt_d, DIRECT);

//rate hold
float lrate_p = 20.000;
float lrate_i = 0.000;
float lrate_d = 0.000;

double lrateXHoldSP, lrateXHoldInput, lrateXHoldOutput;
PID lrateXHoldPID(&lrateXHoldInput, &lrateXHoldOutput, &lrateXHoldSP, lrate_p, lrate_i, lrate_d, DIRECT);

double lrateYHoldSP, lrateYHoldInput, lrateYHoldOutput;
PID lrateYHoldPID(&lrateYHoldInput, &lrateYHoldOutput, &lrateYHoldSP, lrate_p, lrate_i, lrate_d, DIRECT);



//position hold--------------------------------------------
#define MOTION_BURST 0x50
#define DELTAX 0x03
#define SS 10
#define MOSI 11
#define MISO 12
#define SCK 13

int8_t Raw_dX = 0;
int8_t Raw_dY = 0;
int Raw_dX_Sum, Raw_dY_Sum;
float OFSrateX;
float OFSrateY;
unsigned int OFSLastMillis;
int OFSDeltaMillis;
float OFSDeltaRoll, OFSDeltaPitch, OFSLastRoll, OFSLastPitch;
byte SQUAL = 0;
byte OFconfig;
int OF_FOV = 130; //play with
int OF_Scalar = 5; //these

float slamRotZ;

float rateX;
float rateY;

float rateLockX;
float rateLockY;
float rateLockRotZ;
//END OF CONTROL METHODS--------------------_______________________________-----------------------------------
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

//ALTIMETER___________________________________________________--

//SONAR_____________________________________
int relAlt = 0;
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
float rateW = 0;

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

float pitch_lock = 0;
float roll_lock = 0;
float yaw_lock = 0;
int alt_lock = 0;

unsigned long lastMicros = 0;
short deltaMicros = 0;

boolean start = false;
int mode = 1;

void setup() {
  Wire.begin();
  Serial.begin(9600);// delete if not debugging or programming!
  Serial1.begin(115200);
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
  //ALT HOLD
  altHoldPID.SetMode(AUTOMATIC);
  altHoldPID.SetOutputLimits(1160, 1900);
  altHoldPID.SetSampleTime(100);
  //POS HOLD
  lrateXHoldPID.SetMode(AUTOMATIC);
  lrateXHoldPID.SetOutputLimits(-10, 10);
  lrateXHoldPID.SetSampleTime(167);
  lrateYHoldPID.SetMode(AUTOMATIC);
  lrateYHoldPID.SetOutputLimits(-10, 10);
  lrateYHoldPID.SetSampleTime(167);
  
  //-----------------------------------
  sonarInit(); // INITIALIZE SONAR
  //SETUP MOTORS
  motor1.attach(23);
  motor2.attach(22);
  motor3.attach(21);
  motor4.attach(20);
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
      OFCalculateRate();
      //FILTER IMU DATA
      ImuFilter(.997);

      if (connection == true) { //NORMAL MOTOR OPTION IF MOTORS ARE CONNECTED
        altHold(alt_lock);
        //motor_speed = map(alt_lock, 0, 150, 1120, 1600);
        linearRateHold(rateLockX, rateLockY);
        //Serial.print("starting");
        //Serial.println(rateLockRotZ);
        yaw_lock = calculateYawLock((float)rateLockRotZ);
        if (motor_speed >= 1150) {
          stabilize(pitch_lock, roll_lock, yaw_lock);
        }
        else if (motor_speed < 1150) {
          m1_speed = motor_speed;
          m2_speed = motor_speed;
          m3_speed = motor_speed;
          m4_speed = motor_speed;
        }
        motor1.writeMicroseconds(m1_speed);
        motor2.writeMicroseconds(m2_speed);
        motor3.writeMicroseconds(m3_speed);
        motor4.writeMicroseconds(m4_speed);
      }
      else if (connection == false) { //DIRECT CONTROL DISCONNECTION FAILSAFE
        pitch_lock = 0; // AS SOON AS DISCONNECTED THE QUAD WILL LEVEL ITSELF OUT
        roll_lock = 0;
        yaw_lock = 0;
        motor_speed = 1110;

        m1_speed = motor_speed;
        m2_speed = motor_speed;
        m3_speed = motor_speed;
        m4_speed = motor_speed;
        //APPLY SPEED CHANGES TO MOTORS
        motor1.writeMicroseconds(m1_speed);
        motor2.writeMicroseconds(m2_speed);
        motor3.writeMicroseconds(m3_speed);
        motor4.writeMicroseconds(m4_speed);
      }
      serialHandShake(); //RUNS A CHECK FOR SERIAL DATA THEN WRITES A PACKET
    }
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
    //yaw_lock = calculateYawLock(0.6);
    OFReadMotion();
    OFCalculateRate();
    ImuFilter(.997);
    serialHandShake();
  }
  //WILL CHECK FOR A CONNECTION EVERY CYCLE
  connectionCheck();
  deltaMicros = micros() - lastMicros; //CYCLE TIME
  lastMicros = micros();
  //------------------------
  //FOR TEST PURPOSES
  Serial.print(yaw_lock);
  Serial.print(" : ");
  Serial.print(alt_lock);
  Serial.print(" : ");
  Serial.print(rateY);
  Serial.print(" : ");
  Serial.print(roll_lock);
  Serial.print(" : ");
  Serial.print(start);
  Serial.print(" : ");
  Serial.print(gyro_z);
  Serial.print(" : ");
  Serial.println(rateLockRotZ);
}







