void stabilize(float pitch_lock, float roll_lock, float yaw_lock) {
  
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
    m1_speed = motor_speed - pitchRateOutput + rollRateOutput + yawRateOutput;
    m2_speed = motor_speed + pitchRateOutput + rollRateOutput - yawRateOutput;
    m3_speed = motor_speed + pitchRateOutput - rollRateOutput + yawRateOutput;
    m4_speed = motor_speed - pitchRateOutput - rollRateOutput - yawRateOutput;

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






