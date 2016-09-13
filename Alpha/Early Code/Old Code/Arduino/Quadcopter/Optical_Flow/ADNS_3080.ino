void OFInit(){ // set up SPI 
  pinMode(MOSI, OUTPUT);
  pinMode(MISO, INPUT);
  pinMode(SCK, OUTPUT);
  pinMode(SS, OUTPUT);
  digitalWrite(SS, HIGH);
  SPI.setDataMode(SPI_MODE3);
  SPI.setBitOrder(MSBFIRST);
  SPI.setClockDivider(SPI_CLOCK_DIV8); // MAKE SURE THE SPEED IS AT 2MHZ!!!!!!!
}

void OFReadMotion(){ //Read from Optical flow
  byte _motion;
  digitalWrite(SS, LOW);
  SPI.transfer(0x50);
  delayMicroseconds(75);
  _motion = SPI.transfer(0x00);
  deltaX = SPI.transfer(0x00);
  deltaY = SPI.transfer(0x00);
  SQUAL = SPI.transfer(0x00);
  digitalWrite(SS, HIGH);
}
