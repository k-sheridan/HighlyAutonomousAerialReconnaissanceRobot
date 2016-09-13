void tuningMode(){
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
  
}

