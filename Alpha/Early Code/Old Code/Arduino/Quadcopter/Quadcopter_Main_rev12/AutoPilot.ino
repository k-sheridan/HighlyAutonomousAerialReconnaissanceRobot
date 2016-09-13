void autoPilot(int alt_lock){
  altHoldInput = (double)relAlt; //ALTITUDE HOLD
  altHoldSP = (double)alt_lock;
  altHoldPID.Compute();
  motor_speed = altHoldOutput;
}

