void readPacket() {
  int x;
  int remainder;
  int payLoadSum = 0;
  int checkSum;

  for (int i = 0; i < BUFFER_LIMIT; i++) {
    data[i] = Serial1.read();
  }

  // CHECK SUM
  for (int i = 0; i < (BUFFER_LIMIT - 2); i++) {
    payLoadSum += data[i];
  }

  x = payLoadSum;
  remainder = x / 23; // divisor
  remainder = x - (remainder * 23);

  checkSum = data[48];



  if (checkSum == remainder) {

    if (data[1] == 100) {
      start = true;
    }
    if (data[1] == 0) {
      start = false;
    }
    mode = data[2];
    kill = data[3];

    //Direct Control
    if(data[0] == 'a'){

      comMode = 1;

      pitch_lock = 0;
      pitch_lock |= data[5];
      pitch_lock = pitch_lock << 8;
      pitch_lock |= data[6];

      roll_lock = 0;
      roll_lock |= data[7];
      roll_lock = roll_lock << 8;
      roll_lock |= data[8];

      yaw_lock = 0;
      yaw_lock |= data[9];
      yaw_lock = yaw_lock << 8;
      yaw_lock |= data[10];

      alt_lock = 0;
      alt_lock = data[11];

      motor_speed = (int)data[13];
      motor_speed = map(motor_speed, 140, 254, 1110, 2000);
    }

    //Tuning mode
    if(data[0] == 'd'){

      comMode = 4;

      pitch_lock = 0;
      pitch_lock |= data[5];
      pitch_lock = pitch_lock << 8;
      pitch_lock |= data[6];

      motor_speed = (int)data[7];
      motor_speed = map(motor_speed, 140, 254, 1110, 2000);
      tuningParam = data[20];
      
      roll_lock = 0;
      roll_lock |= data[21];
      roll_lock = roll_lock << 8;
      roll_lock |= data[22];

      yaw_lock = 0;
      yaw_lock |= data[23];
      yaw_lock = yaw_lock << 8;
      yaw_lock |= data[24];
      
      alt_lock = data[25];

      if(data[20] == 1){
        PR_Attitude_p = bytes2Float(8);
        PR_Attitude_i = bytes2Float(12);
        PR_Attitude_d = bytes2Float(16);
        pitchAttitudePID.SetTunings(PR_Attitude_p, PR_Attitude_i, PR_Attitude_d);
        rollAttitudePID.SetTunings(PR_Attitude_p, PR_Attitude_i, PR_Attitude_d); 
      }
      if(data[20] == 2){
        PR_Rate_p = bytes2Float(8);
        PR_Rate_i = bytes2Float(12);
        PR_Rate_d = bytes2Float(16);
        pitchRatePID.SetTunings(PR_Rate_p, PR_Rate_i, PR_Rate_d);
        rollRatePID.SetTunings(PR_Rate_p, PR_Rate_i, PR_Rate_d);
      }
      if(data[20] == 3){
        Y_Rate_p = bytes2Float(8);
        Y_Rate_i = bytes2Float(12);
        Y_Rate_d = bytes2Float(16);
        yawRatePID.SetTunings(Y_Rate_p, Y_Rate_i, Y_Rate_d);
      }
      if(data[20] == 4){
        Alt_p = bytes2Float(8);
        Alt_i = bytes2Float(12);
        Alt_d = bytes2Float(16);
        altHoldPID.SetTunings(Alt_p, Alt_i, Alt_d);
      }
    }

    packetLoss = 0;
  }
  else if (checkSum != remainder) {
    packetLoss = 100;
  }

}




float bytes2Float(int offset){

  union u_tag {
    byte b[4];
    float fVal;
  } 
  u;

  u.b[0] = data[0 + offset];
  u.b[1] = data[1 + offset];
  u.b[2] = data[2 + offset];
  u.b[3] = data[3 + offset];

  return u.fVal;
}



