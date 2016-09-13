void serialEvent(Serial port) {
  if (port.available() == 56) {
    readPacket();
    if (comMode == 1) {
      writePacket(DC_WRITE);
    } else if (comMode == 2) {
      writePacket(SA_WRITE);
    } else if (comMode == 3) {
      writePacket(A_WRITE);
    } else if (comMode == 4) {
      writePacket(TUNING_WRITE);
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
  }
}

