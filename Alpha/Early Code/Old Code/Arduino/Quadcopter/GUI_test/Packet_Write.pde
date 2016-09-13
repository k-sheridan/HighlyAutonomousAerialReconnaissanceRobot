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

  // DIRECT mode:
  if (CMD == 'a') {
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

    //Tuning mode:
  } else if (CMD == 'd') {
    ITEM[5] = pitch_lock >> 8;
    ITEM[6] = pitch_lock & 0xFF;

    ITEM[7] = motor_speed;
    
    float2bytes(p_val);
    ITEM[8] = FB[0];
    ITEM[9] = FB[1];
    ITEM[10] = FB[2];
    ITEM[11] = FB[3];
    
    float2bytes(i_val);
    ITEM[12] = FB[0];
    ITEM[13] = FB[1];
    ITEM[14] = FB[2];
    ITEM[15] = FB[3];
    
    float2bytes(d_val);
    ITEM[16] = FB[0];
    ITEM[17] = FB[1];
    ITEM[18] = FB[2];
    ITEM[19] = FB[3];
  }


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

  for (int i = 0; i < 22; i++) {
    port.write(ITEM[i]);
  }
  port.write(CHKSUM);
  port.write('\n');
}



void float2bytes(float myFloat) {
  int bits = Float.floatToIntBits(myFloat);
  FB[0] = (byte)(bits & 0xff);
  FB[1] = (byte)((bits >> 8) & 0xff);
  FB[2] = (byte)((bits >> 16) & 0xff);
  FB[3] = (byte)((bits >> 24) & 0xff);
}

