void writePacket() {
  byte ITEM[97];
  int PACKET_LOSS;
  int payLoadSum = 0;
  int CHKSUM;
  int remainder;
  byte* myFloatPtr;

  //DATA BREAK DOWN:

  myFloatPtr = (byte*) & true_pitch;

  ITEM[0] = myFloatPtr[0];
  ITEM[1] = myFloatPtr[1];
  ITEM[2] = myFloatPtr[2];
  ITEM[3] = myFloatPtr[3];

  myFloatPtr = (byte*) & true_roll;

  ITEM[4] = myFloatPtr[0];
  ITEM[5] = myFloatPtr[1];
  ITEM[6] = myFloatPtr[2];
  ITEM[7] = myFloatPtr[3];

  myFloatPtr = (byte*) & true_yaw;

  ITEM[8] = myFloatPtr[0];
  ITEM[9] = myFloatPtr[1];
  ITEM[10] = myFloatPtr[2];
  ITEM[11] = myFloatPtr[3];

  myFloatPtr = (byte*) & m1_speed;

  ITEM[12] = myFloatPtr[0];
  ITEM[13] = myFloatPtr[1];
  ITEM[14] = myFloatPtr[2];
  ITEM[15] = myFloatPtr[3];

  myFloatPtr = (byte*) & m2_speed;

  ITEM[16] = myFloatPtr[0];
  ITEM[17] = myFloatPtr[1];
  ITEM[18] = myFloatPtr[2];
  ITEM[19] = myFloatPtr[3];

  myFloatPtr = (byte*) & m3_speed;

  ITEM[20] = myFloatPtr[0];
  ITEM[21] = myFloatPtr[1];
  ITEM[22] = myFloatPtr[2];
  ITEM[23] = myFloatPtr[3];

  myFloatPtr = (byte*) & m4_speed;

  ITEM[24] = myFloatPtr[0];
  ITEM[25] = myFloatPtr[1];
  ITEM[26] = myFloatPtr[2];
  ITEM[27] = myFloatPtr[3];

  //    myFloatPtr = (byte*) & true_altitude;   // TRUE ALTITUDE
  //  
  //    ITEM[28] = myFloatPtr[0];
  //    ITEM[29] = myFloatPtr[1];
  //    ITEM[30] = myFloatPtr[2];
  //    ITEM[31] = myFloatPtr[3];
  
  ITEM[32] = (relAlt >> 8) & 0xFF;
  ITEM[33] = relAlt & 0xFF;

  // dist 5 goes HERE!
  
  ITEM[36] = (dist_1 >> 8) & 0xFF;
  ITEM[37] = dist_1 & 0xFF;

  ITEM[38] = (dist_2 >> 8) & 0xFF;
  ITEM[39] = dist_2 & 0xFF;

  ITEM[40] = (dist_3 >> 8) & 0xFF;
  ITEM[41] = dist_3 & 0xFF;

  ITEM[42] = (dist_4 >> 8) & 0xFF;
  ITEM[43] = dist_4 & 0xFF;

  ITEM[44] = deltaMicros >> 8;   //TIME IT TAKES FOR CODE TO LOOP
  ITEM[45] = deltaMicros & 0xFF;
  
  ITEM[46] = gyro_x >> 8;  //GYRO DATA RAW
  ITEM[47] = gyro_x & 0xFF;
  
  ITEM[48] = gyro_y >> 8;
  ITEM[49] = gyro_y & 0xFF;
  
  ITEM[50] = gyro_z >> 8;
  ITEM[51] = gyro_z & 0xFF;
  
  float temp;
  temp = float(sonarReading); //pitchAttitudeOutput
  myFloatPtr = (byte*) & temp;

  ITEM[52] = myFloatPtr[0];
  ITEM[53] = myFloatPtr[1];
  ITEM[54] = myFloatPtr[2];
  ITEM[55] = myFloatPtr[3];
  
  temp = float(rollAttitudeOutput);
  myFloatPtr = (byte*) & temp;

  ITEM[56] = myFloatPtr[0];
  ITEM[57] = myFloatPtr[1];
  ITEM[58] = myFloatPtr[2];
  ITEM[59] = myFloatPtr[3];
  
  temp = float(pitchRateOutput);
  myFloatPtr = (byte*) & temp;

  ITEM[60] = myFloatPtr[0];
  ITEM[61] = myFloatPtr[1];
  ITEM[62] = myFloatPtr[2];
  ITEM[63] = myFloatPtr[3];
  
  temp = float(rollRateOutput);
  myFloatPtr = (byte*) & temp;

  ITEM[64] = myFloatPtr[0];
  ITEM[65] = myFloatPtr[1];
  ITEM[66] = myFloatPtr[2];
  ITEM[67] = myFloatPtr[3];
  
  temp = float(yawRateOutput);
  myFloatPtr = (byte*) & temp;

  ITEM[68] = myFloatPtr[0];
  ITEM[69] = myFloatPtr[1];
  ITEM[70] = myFloatPtr[2];
  ITEM[71] = myFloatPtr[3];
  
  temp = float(altHoldOutput);
  myFloatPtr = (byte*) & temp;

  ITEM[72] = myFloatPtr[0];
  ITEM[73] = myFloatPtr[1];
  ITEM[74] = myFloatPtr[2];
  ITEM[75] = myFloatPtr[3];

  //------------OUTPUTS END--------------------
  /* Inertial Sonar Filter */
  
  temp = float(Zaccel); // Change!
  myFloatPtr = (byte*) & temp;

  ITEM[76] = myFloatPtr[0];
  ITEM[77] = myFloatPtr[1];
  ITEM[78] = myFloatPtr[2];
  ITEM[79] = myFloatPtr[3];
  
  temp = float(Zsonar);
  myFloatPtr = (byte*) & temp;

  ITEM[80] = myFloatPtr[0];
  ITEM[81] = myFloatPtr[1];
  ITEM[82] = myFloatPtr[2];
  ITEM[83] = myFloatPtr[3];
  
  //Optical Flow
  temp = float(xPos);
  myFloatPtr = (byte*) & temp;

  ITEM[84] = myFloatPtr[0];
  ITEM[85] = myFloatPtr[1];
  ITEM[86] = myFloatPtr[2];
  ITEM[87] = myFloatPtr[3];
  
  temp = float(yPos);
  myFloatPtr = (byte*) & temp;

  ITEM[88] = myFloatPtr[0];
  ITEM[89] = myFloatPtr[1];
  ITEM[90] = myFloatPtr[2];
  ITEM[91] = myFloatPtr[3];
  
  ITEM[92] = SQUAL;
  
  ITEM[96] = packetLoss;

  //check sum------------------------
  for (int i = 0; i < 97; i++) {
    payLoadSum += ITEM[i];
  }

  CHKSUM = 97 + payLoadSum;
  remainder = CHKSUM / 23;
  remainder = CHKSUM - (remainder * 23);
  CHKSUM = remainder;

  //WRITE PACKET:

  Serial1.write('a');
  for (int i = 0; i < 97; i++) {
    Serial1.write(ITEM[i]);
  }
  Serial1.write(CHKSUM);
  Serial1.write('\n');

}



