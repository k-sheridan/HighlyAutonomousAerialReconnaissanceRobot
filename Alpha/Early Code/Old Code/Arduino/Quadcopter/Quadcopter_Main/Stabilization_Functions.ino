void stabilize(int pitch_lock) {
  float angle;
  float m1_speed;
  float m2_speed;
  float m3_speed;
  float m4_speed;
  float d_speed;
  
  pitch_speed = true_pitch - last_pitch;
  
  if (true_pitch < pitch_lock) {
    angle = -1 * (true_pitch - pitch_lock);
    
    if(pitch_speed <= 0){
     d_speed = (angle * p_mult) + ((-1 * pitch_speed) * d_mult); 
    }
    if(pitch_speed > 0 && pitch_speed <= 1){
     d_speed = angle * p_mult; 
    }
    if(pitch_speed > 1){
     d_speed = 0; 
    }
    m1_speed = motor_speed + d_speed;
    m2_speed = motor_speed - (d_speed + 1);
    m3_speed = motor_speed - (d_speed + 1);
    m4_speed = motor_speed + d_speed;
  }
  
  if (true_pitch >= pitch_lock) {
    angle = true_pitch - pitch_lock;
    
    if(pitch_speed >= 0){
     d_speed = (angle * p_mult) + (pitch_speed * d_mult); 
    }
    if(pitch_speed < 0 && pitch_speed >= -1){
     d_speed = angle * p_mult; 
    }
    if(pitch_speed < -1){
     d_speed = 0; 
    }
    
    m1_speed = motor_speed - d_speed;
    m2_speed = motor_speed + (d_speed + 1);
    m3_speed = motor_speed + (d_speed + 1);
    m4_speed = motor_speed - d_speed;
  }
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

  last_pitch = true_pitch;

  Serial.print("MOTOR 1 & 4: ");
  Serial.print(m1_speed);
  Serial.print("  MOTOR 2 & 3: ");
  Serial.print(m2_speed);
  Serial.print("  Pitch Speed: ");
  Serial.print(pitch_speed);
  Serial.print("  D multiplier: ");
  Serial.print(d_mult);
  Serial.print("  Angle: ");
  Serial.print(angle);
  Serial.print("  Delta Speed: ");
  Serial.print(d_speed);
  Serial.print("      ");

}


