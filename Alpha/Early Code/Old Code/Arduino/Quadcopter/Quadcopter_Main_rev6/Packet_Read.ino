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

  checkSum = data[22];



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
    alt_lock |= data[11];
    alt_lock = alt_lock << 8;
    alt_lock |= data[12];

    motor_speed = (int)data[13];
    motor_speed = map(motor_speed, 140, 254, 1135, 2000);
  }
  
  //Tuning mode
  if(data[0] == 'd'){
    
    comMode = 4;
    
    pitch_lock = 0;
    pitch_lock |= data[5];
    pitch_lock = pitch_lock << 8;
    pitch_lock |= data[6];
    
    motor_speed = (int)data[7];
    motor_speed = map(motor_speed, 140, 254, 1135, 2000);
    
    PR_p = bytes2Float(8);
    PR_i = bytes2Float(12);
    PR_d = bytes2Float(16);
    
    pitchPID.SetTunings(PR_p, PR_i, PR_d);
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
  } u;
  
  u.b[0] = data[0 + offset];
  u.b[1] = data[1 + offset];
  u.b[2] = data[2 + offset];
  u.b[3] = data[3 + offset];
  
  return u.fVal;
}
