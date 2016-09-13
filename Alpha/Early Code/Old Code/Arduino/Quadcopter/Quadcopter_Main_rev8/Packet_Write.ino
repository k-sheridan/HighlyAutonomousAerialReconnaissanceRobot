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

  //    myFloatPtr = (byte*) & true_altitude;
  //  
  //    ITEM[28] = myFloatPtr[0];
  //    ITEM[29] = myFloatPtr[1];
  //    ITEM[30] = myFloatPtr[2];
  //    ITEM[31] = myFloatPtr[3];

  //Relative altitude goes HERE!

  ITEM[36] = dist_1 >> 8;
  ITEM[37] = dist_1 & 0xFF;

  ITEM[38] = dist_2 >> 8;
  ITEM[39] = dist_2 & 0xFF;

  ITEM[40] = dist_3 >> 8;
  ITEM[41] = dist_3 & 0xFF;

  ITEM[42] = dist_4 >> 8;
  ITEM[43] = dist_4 & 0xFF;

  ITEM[44] = deltaMicros >> 8;   //TIME IT TAKES FOR CODE TO LOOP
  ITEM[45] = deltaMicros & 0xFF;

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



