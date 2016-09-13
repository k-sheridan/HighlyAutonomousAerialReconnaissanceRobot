//IMU FILTER------------------------------------------------------

void ImuFilter(float tilt_bias) {
  
//accel---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  accel_angle_x = atan( accel_x / (sqrt(pow(accel_y, 2) + pow(accel_z, 2))));
  accel_angle_x = accel_angle_x * (180.0 / 3.141592);
  
  accel_angle_x *= -1;
  
  accel_angle_y = atan( accel_y / (sqrt(pow(accel_x, 2) + pow(accel_z, 2))));
  accel_angle_y = accel_angle_y * (180.0 / 3.141592);
  
  accel_angle_x_filter = (accel_angle_x + accel_angle_x_last) / 2;
  accel_angle_y_filter = (accel_angle_y + accel_angle_y_last) / 2;
  
  accel_angle_y = accel_angle_y_filter;
  accel_angle_x = accel_angle_x_filter;
  
  accel_angle_x_last = accel_angle_x;
  accel_angle_y_last = accel_angle_y;
  
//Gyro/Complementary Filter----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  g_pitch = gyro_x / 14.375;
  g_roll = gyro_y / 14.375;
  g_yaw = (gyro_z / 14.375) * -1;
  
  int delta_time;
  delta_time = millis() - last_time;
  
  g_pitch = tilt_bias * (true_pitch + g_pitch / (1000 / delta_time));
  g_roll = tilt_bias * (true_roll + g_roll / (1000 / delta_time));
  g_yaw = 0.5 * (true_yaw + g_yaw / (1000 / delta_time));
  
  last_time = millis();
  
  true_pitch = g_pitch + ((1 - tilt_bias) * accel_angle_x);
  //Roll fix
  accel_angle_y += 4.00;
  true_roll = g_roll + ((1 - tilt_bias) * accel_angle_y);
  
  
  //Magnetometer-------------------
  heading = atan2(mag_y , mag_x) * (180 / 3.14159265) + 180;
  
  true_yaw = g_yaw + (0.5 * heading);
  
}


  
