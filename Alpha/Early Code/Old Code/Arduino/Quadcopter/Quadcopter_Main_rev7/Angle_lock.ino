void stabilize(float pitch_lock, float roll_lock) {

  //PITCH____________________________________________________________________________________
    //ATTITUDE >>>>> RATE SETPOINT
    if(comMode == 4){
      if(tuningParam == 1){
    pitchAttitudeInput = (double)true_pitch;
    pitchAttitudeSP = (double)pitch_lock;
    pitchAttitudePID.Compute();
      }
    //RATE >>>>>>> MOTOR SPEED
    pitchRateInput = (double)gyro_x;
    if(tuningParam == 1){
    pitchRateSP = pitchAttitudeOutput;
    }
    if(tuningParam == 2){
    pitchRateSP = map(pitch_lock, -15, 15, -500, 500);  
    }
    pitchRatePID.Compute();
    }
    
    m1_speed = motor_speed - pitchRateOutput;
    m2_speed = motor_speed + pitchRateOutput;
    m3_speed = motor_speed + pitchRateOutput;
    m4_speed = motor_speed - pitchRateOutput;
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


