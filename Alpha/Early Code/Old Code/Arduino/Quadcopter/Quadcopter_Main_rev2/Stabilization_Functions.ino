void stabilize(int pitch_lock) {
  float p_error;
  float m1_speed;
  float m2_speed;
  float m3_speed;
  float m4_speed;
  float d_speed;
  
  p_error = pitch_lock - true_pitch;
  p_error_speed = p_error - last_p_error;
  
  i_adder += (p_error * i_mult);
  
  d_speed = (p_mult * p_error) + (d_mult * p_error_speed) + i_adder; 
  
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

  Serial.print("MOTOR 1 & 4: ");
  Serial.print(m1_speed);
  Serial.print("  MOTOR 2 & 3: ");
  Serial.print(m2_speed);
  Serial.print("  Pitch Speed: ");
  Serial.print(p_error_speed);
  Serial.print("  D multiplier: ");
  Serial.print(d_mult);
  Serial.print("  P error: ");
  Serial.print(p_error);
  Serial.print("  Delta Speed: ");
  Serial.print(d_speed);
  Serial.print("      ");

}


