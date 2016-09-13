void readPacket() {
  int x;
  int remainder;
  int payLoadSum = 0;
  int checkSum;

  for (int i = 0; i < BUFFER_LIMIT; i++) {
    data[i] = Serial.read();
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

    pitch_lock |= data[5];
    pitch_lock = pitch_lock << 8;
    pitch_lock |= data[6];
    
    
    roll_lock |= data[7];
    roll_lock = pitch_lock << 8;
    roll_lock |= data[8];
    
    yaw_lock |= data[9];
    yaw_lock = pitch_lock << 8;
    yaw_lock |= data[10];
    
    alt_lock |= data[11];
    alt_lock = pitch_lock << 8;
    alt_lock |= data[12];
    
    motor_speed = (int)data[13];

    packetLoss = 0;
  }
  else if (checkSum != remainder) {
    packetLoss = 100;
  }

}
