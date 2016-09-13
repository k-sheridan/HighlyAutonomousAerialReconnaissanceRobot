//IMU FILTER------------------------------------------------------

void ImuFilter(float tilt_bias) {

  //accel---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  accel_angle_x = atan( accel_x / (sqrt(pow(accel_y, 2) + pow(accel_z, 2))));
  accel_angle_x = accel_angle_x * (180.0 / 3.141592);

  accel_angle_x *= -1;

  accel_angle_y = atan( accel_y / (sqrt(pow(accel_x, 2) + pow(accel_z, 2))));
  accel_angle_y = accel_angle_y * (180.0 / 3.141592);

  accel_angle_y -= 4;

  accel_angle_x_filter = (accel_angle_x + accel_angle_x_last) / 2;
  accel_angle_y_filter = (accel_angle_y + accel_angle_y_last) / 2;

  accel_angle_y = accel_angle_y_filter;
  accel_angle_x = accel_angle_x_filter;

  accel_angle_x_last = accel_angle_x;
  accel_angle_y_last = accel_angle_y;

  //Gyro/Complementary Filter----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  g_pitch = gyro_x / 14.375;
  g_roll = gyro_y / 14.375;
  g_yaw = gyro_z / 14.375;

  int delta_time;
  delta_time = millis() - last_time;

  rateW = radians(g_yaw); //angular veolcity in radians persecond

  g_pitch = tilt_bias * (pitch + g_pitch / (1000 / delta_time));
  g_roll = tilt_bias * (roll + g_roll / (1000 / delta_time));
  g_yaw = 0.5 * (true_yaw + g_yaw / (1000 / delta_time));


  last_time = millis();
  accel_angle_x += 2.00;
  pitch = g_pitch + ((1 - tilt_bias) * accel_angle_x);
  //Roll fix
  accel_angle_y += 3.00;
  roll = g_roll + ((1 - tilt_bias) * accel_angle_y);


  //LOW PASS-----------------------

  true_pitch = (pitch + pitchBuff[0] + pitchBuff[1]) / 3;
  pitchBuff[1] = pitchBuff[0];
  pitchBuff[0] = pitch;

  true_roll = (roll + rollBuff[0] + rollBuff[1]) / 3;
  rollBuff[1] = rollBuff[0];
  rollBuff[0] = roll;


  //Magnetometer-------------------

  heading = atan2(mag_y , mag_x) * (180 / 3.14159265) + 180;

  true_yaw = g_yaw + (0.5 * heading);

}





