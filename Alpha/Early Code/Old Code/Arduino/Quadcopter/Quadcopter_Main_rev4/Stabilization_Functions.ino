void stabilize(int pitch_lock) {
  float p_error;
  float d_speed;
  float i_max = 20;
  float i_min = -20;
  
  //PITCH____________________________________________________________________________________
  
  p_error = pitch_lock - true_pitch;
  p_error_speed = p_error - last_p_error;
  
  p_i_adder += (p_error * i_mult);
  if(p_i_adder <= i_min){
    p_i_adder = i_min;
  }
  if(p_i_adder >= i_max){
    p_i_adder = i_max;
  }
  
  d_speed = (p_mult * p_error) + (d_mult * p_error_speed) + p_i_adder; 
  //d_speed = (p_mult * p_error) + (d_mult * p_error_speed);
  
  m1_speed = motor_speed + d_speed;
  m2_speed = motor_speed - d_speed;
  m3_speed = motor_speed - d_speed;
  m4_speed = motor_speed + d_speed;
  
  last_p_error = p_error;
  
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
  analogWrite(motor1, m1_speed);
  analogWrite(motor2, m2_speed);
  analogWrite(motor3, m3_speed);
  analogWrite(motor4, m4_speed);

}


