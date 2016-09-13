void stabilize(short pitch_lock, short roll_lock) {

  //PITCH____________________________________________________________________________________
  pitchInput = (double)true_pitch;
  pitchSP = (double)pitch_lock;

  pitchPID.Compute();

  m1_speed = motor_speed - pitchOutput;
  m2_speed = motor_speed + pitchOutput;
  m3_speed = motor_speed + pitchOutput;
  m4_speed = motor_speed - pitchOutput;

  //ROLL_______________________________________________
  if (comMode != 4) {
    rollInput = (double)true_roll;
    rollSP = (double)roll_lock;

    rollPID.Compute();

    m1_speed = m1_speed + rollOutput;
    m2_speed = m2_speed + rollOutput;
    m3_speed = m3_speed - rollOutput;
    m4_speed = m4_speed - rollOutput;
  }

  //YAW____________________________________________
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


