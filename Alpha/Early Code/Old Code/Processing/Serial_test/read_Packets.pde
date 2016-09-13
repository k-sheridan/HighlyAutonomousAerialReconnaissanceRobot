void readPacket() {
  int _remainder = 0;
  int x;
  int checkSum = 0;
  int fix1;
  int fix2;
  int fix3;
  
  // read the bytes in 
  for(int i = 0; i < BUFFER_LIMIT; i++){
    data[i] = (byte)port.read();
  }
  
  switch(data[0]) {
  case potRead:
    //BYTE FIX-----------------
    fix1 = data[0];
    fix2 = data[1];
    fix3 = data[2];
    if (fix1 < 0) {
      fix1 = 256 + fix1;
    }
    if (fix2 < 0) {
      fix2 = 256 + fix2;
    }
    if (fix3 < 0) {
      fix3 = 256 + fix3;
    }
    //CHECK SUM---------------------------
    x = fix1 + fix2 + fix3;
    _remainder = x / 23;
    _remainder = x - (_remainder * 23);
    //-------------------------------

    checkSum = data[3];

    if (checkSum == _remainder) {
      potVal = 0;
      potVal = potVal | (data[1] & 0xFF);
      potVal = potVal << 8;
      potVal = potVal | (data[2] & 0xFF);
      
      if(potVal > 32767){        // 16 bit to 32 bit correction!
       potVal = 65536 - potVal;
       potVal *= -1; 
      }
      
      println(binary(potVal));
      readStatus = "GOOD";
    } else if (checkSum != _remainder || data[4] != '\n') {
      packetLoss++;
      readStatus = "PACKET LOSS";
    }
    println(binary(data[0]) + " " + binary(data[1]) + " " + binary(data[2]) + " " + binary(data[3]) + " " + binary(data[4]));
    break;

  default:
    readStatus = "COMMAND ERROR";
    packetLoss++;
    println(binary(data[0]) + " " + binary(data[1]) + " " + binary(data[2]) + " " + binary(data[3]) + " " + binary(data[4]));
    break;
    
  }
}

