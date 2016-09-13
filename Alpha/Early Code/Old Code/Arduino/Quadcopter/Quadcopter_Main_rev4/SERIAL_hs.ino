void serialHandShake() {
  if (Serial.available() >= 24) {
    readPacket();
    writePacket();
  }
}
