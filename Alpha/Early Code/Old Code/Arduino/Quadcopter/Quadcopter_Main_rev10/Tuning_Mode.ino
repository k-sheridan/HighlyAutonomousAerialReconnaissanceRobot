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
  if(tuningParam == 3){
    
  }
}


