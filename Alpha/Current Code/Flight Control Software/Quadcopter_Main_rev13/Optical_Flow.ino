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
  OFSetResolution(); // SET RESOLUTION
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
  Raw_dX_Sum += Raw_dX;
  Raw_dY_Sum += Raw_dY;
}

void OFCalculateRate(){
  OFSDeltaMillis = millis() - OFSLastMillis;
  if(OFSDeltaMillis >= 100){ // 10 hertz
    OFSrateX = ((float)Raw_dX_Sum / (4 * 7052.0 * (1 / (float)relAlt))) / ((float)OFSDeltaMillis / 1000.0); // set for 1600 resolution
    OFSrateY = ((float)Raw_dY_Sum / (4 * 7052.0 * (1 / (float)relAlt))) / ((float)OFSDeltaMillis / 1000.0);
    
    OFSDeltaRoll = true_roll - OFSLastRoll;
    OFSDeltaPitch = true_pitch - OFSLastPitch;
    
    const float OFSAngleCompGain = 2.5; //modify to adjust angle compensation
    
    OFSrateX = OFSrateX - (OFSAngleCompGain * (float)relAlt / 100.0) * tan(radians(OFSDeltaRoll));
    OFSrateY = OFSrateY - (OFSAngleCompGain * (float)relAlt / 100.0) * tan(radians(OFSDeltaPitch));
    //OFSDeltaPitch = (OFSAngleCompGain * ((float)relAlt / 100.0) * tan(radians(OFSDeltaPitch))); // the 10 is a constant multiplier
    
    Raw_dX_Sum = 0;
    Raw_dY_Sum = 0;
    OFSLastRoll = true_roll;
    OFSLastPitch = true_pitch;
    OFSLastMillis = millis();
  }
  //Serial.println(OFSDeltaPitch);
}

void OFSetResolution(){ //SETS RES TO 1600
  digitalWrite(SS, LOW);
  SPI.transfer(0x0a | 0x80);
  delayMicroseconds(50);
  SPI.transfer(0x10);
  digitalWrite(SS, HIGH);
}

void OFReadConfig(){
  digitalWrite(SS, LOW);
  SPI.transfer(0x0a);
  delayMicroseconds(50);
  OFconfig = SPI.transfer(0x00);
  digitalWrite(SS, HIGH);
  Serial.println(OFconfig);
}

