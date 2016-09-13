void stabilize(float pitch_lock, float roll_lock) {

  if(comMode == 4){  // IF TUNING MODE IS ACTIVATED
    tuningMode();

    //CALCULATING THE FINAL MOTOR SPEEDS
    m1_speed = motor_speed - pitchRateOutput;
    m2_speed = motor_speed + pitchRateOutput;
    m3_speed = motor_speed + pitchRateOutput;
    m4_speed = motor_speed - pitchRateOutput;
  }
  else if(comMode != 4){ // IF OTHER MODE IS ACTIVATED
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

    //CALCULATING THE FINAL MOTOR SPEEDS
    m1_speed = motor_speed - pitchRateOutput + rollRateOutput;
    m2_speed = motor_speed + pitchRateOutput + rollRateOutput;
    m3_speed = motor_speed + pitchRateOutput - rollRateOutput;
    m4_speed = motor_speed - pitchRateOutput - rollRateOutput;
  }

  //------------------------------------------------------------------------------------------

  if (m1_speed >= max_speed) {
    m1_speed = max_speed;
  }
  if (m2_speed >= max_speed) {
    m2_speed = max_speed;
  }
  if (m3_speed >= max_speed) {
    m3_speed = max_speed;
  }
  if (m4_speed >= max_speed) {
    m4_speed = max_speed;
  }
  if (m1_speed <= min_speed) {
    m1_speed = min_speed;
  }
  if (m2_speed <= min_speed) {
    m2_speed = min_speed;
  }
  if (m3_speed <= min_speed) {
    m3_speed = min_speed;
  }
  if (m4_speed <= min_speed) {
    m4_speed = min_speed;
  }

}






