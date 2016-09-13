void sonarInit(){
  Serial3.begin(9600, SERIAL_8N1_RXINV_TXINV);
  Serial3.flush();
}

void InertialSonarFilter(){
  if(sonarRead == false){ // if the sonar hasn't been read from save a raw resolved force into a buffer  
    ZaccelSample += sqrt(pow(accel_x, 2) + pow(accel_y, 2) + pow(accel_z, 2));
    ZaccelSampleCount++; // keeps track of how many values are in the buffer 
  }
  else if(sonarRead){
    Zaccel = mapFloat((ZaccelSample / ZaccelSampleCount), 0, 122, -9.8, 0); // averages resolved force buffer and converts the raw data into m/s2
    ZaccelSample = 0; // zero out all of the buffer's variables
    ZaccelSampleCount = 0;
    Zsonar = (float)(sonarSample[0] - sonarSample[1]) - (float)(sonarSample[1] - sonarSample[2]); // Finds second derivative of sonar readings
    if(abs(Zaccel - Zsonar) <= 10){
      relAlt = sonarSample[1];
    }
    sonarSample[2] = sonarSample[1];
    sonarSample[1] = sonarSample[0];
    sonarSample[0] = sonarReading;
    sonarRead = false;
  }
}

void readSonar(){
  if(Serial3.available() >= 5){
    if(Serial3.read() == 'R'){
      sonarRead = true; //Tell Filter that the sonar has just been read from
      deltaSonarMillis = millis() - lastSonarMillis; // tell filter how long it has been since the last read
      inString = "";
      for(int i = 0; i < 3; i++){
        inString += (char)Serial3.read();
      }
      Serial3.read();
      int temp = inString.toInt();
      if(temp != 765){
        sonarReading = temp;
      }
      lastSonarMillis = millis();
    }
    else{
      Serial3.flush(); 
    }
  } 
}


float mapFloat(long x, float in_min, float in_max, float out_min, float out_max)
 {
   return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
 }



