void serialHandShake() {
  if(Serial1.available() > 0 && Serial1.available() < BUFFER_LIMIT){
    
  }
  if (Serial1.available() >= BUFFER_LIMIT) {
    readPacket();
    while(Serial1.available() > 0){
      Serial1.read();
    }
    writePacket();

    if (firstRead == false) {
      firstRead = true;
    }
    noRead = 0;
  }

}

void connectionCheck() {
  if (noRead >= 300 && firstRead == true) {
    connection = false;
  }
  else if (noRead < 300 && firstRead == true) {
    connection = true;
  }
  noRead++;
}


