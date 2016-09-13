void altHold(int alt_lock){
  altHoldInput = (double)relAlt; //ALTITUDE HOLD
  altHoldSP = (double)alt_lock;
  altHoldPID.Compute();
  motor_speed = altHoldOutput;
}

void linearRateHold(float _ratex, float _ratey){
  lrateXHoldInput = (double)rateX;
  lrateXHoldSP = (double)_ratex;
  lrateXHoldPID.Compute();
  pitch_lock = lrateXHoldOutput;
  
  lrateYHoldInput = (double)rateY;
  lrateYHoldSP = (double)_ratey;
  lrateYHoldPID.Compute();
  roll_lock = -1.0 * lrateYHoldOutput;
}

float calculateYawLock(float _reqrate){
  //Serial.println(_reqrate);
  float tempLock = (_reqrate / PI) * 180.0;
  //Serial.println(tempLock);
  return tempLock * 14.375 * -1;
}



