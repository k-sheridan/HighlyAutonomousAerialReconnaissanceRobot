void serialHandShake() {
  if (Serial1.available() >= 24) {
    readPacket();
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
    motor_speed = 142;
  }
  else if (noRead < 300 && firstRead == true) {
    connection = true;
  }
  noRead++;
}
