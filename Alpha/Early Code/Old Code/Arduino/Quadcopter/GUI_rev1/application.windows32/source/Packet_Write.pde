void writePacket(char CMD) {
  int[] ITEM = new int[22];
  int payLoadSum = 0;
  int CHKSUM = 1;
  int remainder;

  ITEM[0] = CMD;

  if (start == false) {
    ITEM[1] = 0;
  }
  if (start == true) {
    ITEM[1] = 100;
  }

  ITEM[2] = mode;
  ITEM[3] = kill;
  ITEM[4] = 66; // unused at the moment

  ITEM[5] = pitch_lock >> 8;
  ITEM[6] = pitch_lock & 0xFF;
  
  //println(binary(ITEM[5]) + " " + binary(ITEM[6]));

  ITEM[7] = roll_lock >> 8;
  ITEM[8] = roll_lock & 0xFF;

  ITEM[9] = yaw_lock >> 8;
  ITEM[10] = yaw_lock & 0xFF;

  ITEM[11] = alt_lock >> 8;
  ITEM[12] = alt_lock & 0xFF;

  ITEM[13] = motor_speed;



  //FIX BYTES AND CHECK SUM__---

  for (int i = 0; i < 22; i++) {
    if (ITEM[i] < 0) {
      payLoadSum += (256 + (int)ITEM[i]);
    } else {
      payLoadSum += (int)ITEM[i];
    }
  }
  
  CHKSUM = payLoadSum;
  remainder = CHKSUM / 23;
  remainder = CHKSUM - (remainder * 23);
  
  CHKSUM = remainder;
  //WRITE DATA:
  
  for(int i = 0; i < 22; i++){
    port.write(ITEM[i]);
  }
  port.write(CHKSUM);
  port.write('\n');
}

