void serialHandShake() {
  
  if(port.available() > 0 && port.available() < BUFFER_LIMIT){  // THIS IS IF I RECEIVE LESS DATA THAN IS EXPECTED
    timeOut++;
    if(timeOut > 30){
      println("Attempting to reconnect... Cause: TIME OUT");
      println("Bytes in Buffer: " + port.available());
      reconnect();
      timeOut = 0;
      readPacketLoss++;
    }
  }
  if (port.available() >= BUFFER_LIMIT) {  // THIS IS IF I RECEIVE THE EXPECTED AMOUNT OF DATA OR MORE
    
    timeOut = 0;
    
    if(firstRead == false){
      firstRead = true;
    }
    readPacket();
    while (port.available () > 0) { // THIS WILL FLUSH THE EXCESS DATA FROM THE BUFFER
      port.read();
    }
    if (mode == 1) {
      writePacket(DC_WRITE);
    } else if (mode == 2) {
      writePacket(SA_WRITE);
    } else if (mode == 3) {
      writePacket(A_WRITE);
    } else if (mode == 4) {
      writePacket(TUNING_WRITE);
    }
    if(reconnecting == true){
      println("RECONNECT SUCCESSFUL");
      reconnecting = false;
    }
    noRead = 0;
  }
}


void connectionCheck() {
  noRead++;
  if (noRead >= 15) {
    connected = false;
  } else if (noRead < 15) {
    connected = true;
    connectionAlert.pause();
  }
  if (firstRead == true) { //ATTEMPTS TO RECONNECT AND PLAYS SOUND ALERTS
    if(noRead == 15){
      connectionAlert.rewind();
      connectionAlert.play();
    }
    if (noRead == 30) {
      println("Attempting to reconnect... Cause: DISCONNECTED");
      reconnect();
    }
    if(noRead == 120){
      println("RECONNECT FAILED!");
    }
  }
}

void reconnect() {
  reconnecting = true;
  
  while (port.available () > 0) {
    port.read();
  }
  if (mode == 1) {
    writePacket(DC_WRITE);
  } else if (mode == 2) {
    writePacket(SA_WRITE);
  } else if (mode == 3) {
    writePacket(A_WRITE);
  } else if (mode == 4) {
    writePacket(TUNING_WRITE);
  }
}

