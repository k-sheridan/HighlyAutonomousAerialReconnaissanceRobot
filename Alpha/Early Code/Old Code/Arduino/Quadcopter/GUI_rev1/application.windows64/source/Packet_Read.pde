void readPacket() {
  int remainder = 0;
  int x;
  int checkSum = 0;
  int payLoadSum = 0;
  int[] fix = new int[BUFFER_LIMIT - 2];

  // read the bytes in 
  for (int i = 0; i < BUFFER_LIMIT; i++) {
    data[i] = (byte)port.read();
  }

  //BYTE FIX-----------------
  for (int i = 0; i < 54; i++) {
    if (data[i] < 0) {
      payLoadSum += (256 + (int)data[i]);
    } else {
      payLoadSum += (int)data[i];
    }
  }
  //---------------------------

  //CHECK SUM---------------------------
  x = payLoadSum;
  remainder = x / 23;
  remainder = x - (remainder * 23);
  //-------------------------------

  checkSum = data[54];

  switch(data[0]) {

  case 'a':
    if(checkSum == remainder){
      pitch = get4ByteFloat(data, 1);
      roll = get4ByteFloat(data, 5);
      yaw = get4ByteFloat(data, 9);
      m1 = get4ByteFloat(data, 13);
      m2 = get4ByteFloat(data, 17);
      m3 = get4ByteFloat(data, 21);
      m4 = get4ByteFloat(data, 25);
      
      if(data[53] == 100){
        sendError = true;
        sendPacketLoss++;
      }
      else if(data[53] == 0){
        sendError = false;
      }
      
      readError = false;
    }
    else if(checkSum != remainder){
      readError = true;
      readPacketLoss++;
    }
    break;

  default:
    readPacketLoss++;
    readError = true;
    break;
  }
}


float get4ByteFloat(byte[] data, int offset) { 
  String hexint=hex(data[offset+3])+hex(data[offset+2])+hex(data[offset+1])+hex(data[offset]); 
  return Float.intBitsToFloat(unhex(hexint));
} 

