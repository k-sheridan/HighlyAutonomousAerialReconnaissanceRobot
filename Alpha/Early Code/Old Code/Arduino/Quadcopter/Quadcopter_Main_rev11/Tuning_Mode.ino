void tuningStabilize(float pitch_lock, float roll_lock, float yaw_lock){
  if(tuningParam == 1 || tuningParam == 2){
    if(tuningParam == 1){
      pitchAttitudeInput = (double)true_pitch;
      pitchAttitudeSP = (double)pitch_lock;
      pitchAttitudePID.Compute();

      pitchRateInput = (double)gyro_x;
      pitchRateSP = pitchAttitudeOutput;
      pitchRatePID.Compute();
    }
    if(tuningParam == 2){
      pitchRateSP = map(pitch_lock, -15, 15, -500, 500);
      pitchRateInput = (double)gyro_x;
      pitchRatePID.Compute();  
    }

    m1_speed = motor_speed - pitchRateOutput;
    m2_speed = motor_speed + pitchRateOutput;
    m3_speed = motor_speed + pitchRateOutput;
    m4_speed = motor_speed - pitchRateOutput;
  }
  if(tuningParam == 3){  //YAW TUNING MODE, ESSENTIALLY DC MODE BUT WITH THE ABILITY TO ADJUST YAW PID ON THE FLY (LITERALY HAHA)
    pitchAttitudeInput = (double)true_pitch; //PITCH ATTITUDE
    pitchAttitudeSP = (double)pitch_lock;
    pitchAttitudePID.Compute();

    pitchRateInput = (double)gyro_x;   //PITCH RATE
    pitchRateSP = pitchAttitudeOutput;
    pitchRatePID.Compute();

    rollAttitudeInput = (double)true_roll; //ROLL ATTITUDE
    rollAttitudeSP = (double)roll_lock;
    rollAttitudePID.Compute();

    rollRateInput = (double)gyro_y;   //ROLL RATE
    rollRateSP = rollAttitudeOutput;
    rollRatePID.Compute();

    yawRateInput = (double)gyro_z; //YAW RATE
    yawRateSP = yaw_lock;
    yawRatePID.Compute();

    //CALCULATING THE FINAL MOTOR SPEEDS
    m1_speed = motor_speed - pitchRateOutput + rollRateOutput - yawRateOutput;
    m2_speed = motor_speed + pitchRateOutput + rollRateOutput + yawRateOutput;
    m3_speed = motor_speed + pitchRateOutput - rollRateOutput - yawRateOutput;
    m4_speed = motor_speed - pitchRateOutput - rollRateOutput + yawRateOutput;
  }
  
  if(tuningParam == 4){ //MEANT TO TUNE THE ALTITUDE HOLD PID TUNINGS WHILE IN FLIGHT but in manual
    pitchAttitudeInput = (double)true_pitch; //PITCH ATTITUDE
    pitchAttitudeSP = (double)pitch_lock;
    pitchAttitudePID.Compute();

    pitchRateInput = (double)gyro_x;   //PITCH RATE
    pitchRateSP = pitchAttitudeOutput;
    pitchRatePID.Compute();

    rollAttitudeInput = (double)true_roll; //ROLL ATTITUDE
    rollAttitudeSP = (double)roll_lock;
    rollAttitudePID.Compute();

    rollRateInput = (double)gyro_y;   //ROLL RATE
    rollRateSP = rollAttitudeOutput;
    rollRatePID.Compute();

    yawRateInput = (double)gyro_z; //YAW RATE
    yawRateSP = yaw_lock;
    yawRatePID.Compute();

    altHoldPID.SetMode(MANUAL);
    altHoldInput = (double)relAlt; //ALTITUDE HOLD
    altHoldSP = (double)alt_lock;
    altHoldPID.Compute();
    altHoldOutput = motor_speed;

    //CALCULATING THE FINAL MOTOR SPEEDS
    m1_speed = altHoldOutput - pitchRateOutput + rollRateOutput - yawRateOutput;
    m2_speed = altHoldOutput + pitchRateOutput + rollRateOutput + yawRateOutput;
    m3_speed = altHoldOutput + pitchRateOutput - rollRateOutput - yawRateOutput;
    m4_speed = altHoldOutput - pitchRateOutput - rollRateOutput + yawRateOutput;
  }
  
  if(tuningParam == 5){ //MEANT TO TUNE THE ALTITUDE HOLD PID TUNINGS WHILE IN FLIGHT but in auto
    pitchAttitudeInput = (double)true_pitch; //PITCH ATTITUDE
    pitchAttitudeSP = (double)pitch_lock;
    pitchAttitudePID.Compute();

    pitchRateInput = (double)gyro_x;   //PITCH RATE
    pitchRateSP = pitchAttitudeOutput;
    pitchRatePID.Compute();

    rollAttitudeInput = (double)true_roll; //ROLL ATTITUDE
    rollAttitudeSP = (double)roll_lock;
    rollAttitudePID.Compute();

    rollRateInput = (double)gyro_y;   //ROLL RATE
    rollRateSP = rollAttitudeOutput;
    rollRatePID.Compute();

    yawRateInput = (double)gyro_z; //YAW RATE
    yawRateSP = yaw_lock;
    yawRatePID.Compute();

    altHoldPID.SetMode(AUTOMATIC);
    altHoldInput = (double)relAlt; //ALTITUDE HOLD
    altHoldSP = (double)alt_lock;
    altHoldPID.Compute();

    //CALCULATING THE FINAL MOTOR SPEEDS
    m1_speed = altHoldOutput - pitchRateOutput + rollRateOutput - yawRateOutput;
    m2_speed = altHoldOutput + pitchRateOutput + rollRateOutput + yawRateOutput;
    m3_speed = altHoldOutput + pitchRateOutput - rollRateOutput - yawRateOutput;
    m4_speed = altHoldOutput - pitchRateOutput - rollRateOutput + yawRateOutput;
  }
}



