void serialEvent(Serial port) {
  if (port.available() == 56) {
    readPacket();
    writePacket(DC_WRITE);
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

