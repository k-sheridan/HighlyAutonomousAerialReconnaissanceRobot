void OFInit(){ // set up SPI 
  pinMode(MOSI, OUTPUT);
  pinMode(MISO, INPUT);
  pinMode(SCK, OUTPUT);
  pinMode(SS, OUTPUT);
  digitalWrite(SS, HIGH);
  SPI.setDataMode(SPI_MODE3);
  SPI.setBitOrder(MSBFIRST);
  SPI.setClockDivider(SPI_CLOCK_DIV8); // MAKE SURE THE SPEED IS AT 2MHZ!!!!!!!
  delay(100);
  //OFSetResolution(); // SET RESOLUTION
}

void OFReadMotion(){ //Read from Optical flow
  byte _motion;
  digitalWrite(SS, LOW);
  SPI.transfer(0x50);
  delayMicroseconds(75);
  _motion = SPI.transfer(0x00);
  Raw_dX = SPI.transfer(0x00);
  Raw_dY = SPI.transfer(0x00);
  SQUAL = SPI.transfer(0x00);
  digitalWrite(SS, HIGH);
}

void OFCalculatePosition(){
  dX = (float)(Raw_dX * (float)(relAlt / 100.0));
  dX = dX + (float)(((true_roll - lastRoll) * 30.0 * OF_Scalar) / (OF_FOV));
  dY = (float)(Raw_dY * (float)(relAlt / 100.0));
  dY = dY + (float)(((true_pitch - lastPitch) * 30.0 * OF_Scalar) / (OF_FOV));
  xPos += dY * cos(radians(true_yaw - 90));
  yPos += dY * sin(radians(true_yaw - 90));
  xPos += dX * cos(radians(true_yaw));
  yPos += dX * sin(radians(true_yaw));
  
  
  lastPitch = true_pitch;
  lastRoll = true_roll;
}

void OFSetResolution(){ //SETS RES TO 1400
  digitalWrite(SS, LOW);
  SPI.transfer(0x0a);
  delayMicroseconds(50);
  SPI.transfer(0x10 | 0x80);
  digitalWrite(SS, HIGH);
  delay(50);
  digitalWrite(SS, LOW);
  SPI.transfer(0x0a);
  delayMicroseconds(50);
  OFconfig = SPI.transfer(0x00);
  digitalWrite(SS, HIGH);
}

void OFReadConfig(){
  digitalWrite(SS, LOW);
  SPI.transfer(0x0a);
  delayMicroseconds(50);
  OFconfig = SPI.transfer(0x00);
  digitalWrite(SS, HIGH);
}

