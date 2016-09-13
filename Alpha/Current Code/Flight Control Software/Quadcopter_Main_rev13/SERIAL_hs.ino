void serialHandShake() {

  bytesAvailable = Serial1.available();
  if (bytesAvailable > 0) {
    if (Serial1.read() == 'a') { // check for first byte

      Serial1.read(); //clear out the linefeed

      delay(10); // wait for the rest of the data to come in adjust based on size of data
      int char_i = 0;
      String dataString;

      while (Serial1.available() > 0) {
        dataString += (char)Serial1.read(); // read in all data
        char_i++;
      }
      //Serial.println(dataString);
      //split and convert
      String strVals[25]; // a max of 20 string values
      int beginChar = 0;
      int strValIt = 0;

      for (int i = 0; i < dataString.length(); i++) {
        if (dataString.charAt(i) == ',') {
          strVals[strValIt] = dataString.substring(beginChar, i);
          strValIt++;
          beginChar = i + 1;
        }
      }
      //Serial.println(strVals[0][0]);
      if (strVals[0][0] != 'f') {
        if (strValIt >= 4) {
          alt_lock = round(strVals[2].toFloat() * 100);
          start = stringToBoolean(strVals[1]);
          if (stringToBoolean(strVals[3])) {
            rateX = strVals[4].toFloat();
            rateY = strVals[5].toFloat();
            slamRotZ = strVals[6].toFloat();
            //Serial.println("read pose");
            if (stringToBoolean(strVals[7])) {
              rateLockX = strVals[8].toFloat();
              rateLockY = strVals[9].toFloat();
              rateLockRotZ = strVals[10].toFloat();
              //Serial.println("read rates");
            }
          }
          else {
            if (stringToBoolean(strVals[4])) {
              rateLockX = strVals[5].toFloat();
              rateLockY = strVals[6].toFloat();
              rateLockRotZ = strVals[7].toFloat();
              //Serial.println("read rates");
            }
          }

          if (strVals[0][0] == 't') {
            //Serial.println("connection established!");
            connection = true;
            connectionCounter = 0; // connection established!
          }
        }
      }
      else{
        if (stringToBoolean(strVals[2])) {
            rateX = strVals[3].toFloat();
            rateY = strVals[4].toFloat();
            slamRotZ = strVals[5].toFloat();
            //Serial.println("read pose");
            if (stringToBoolean(strVals[6])) {
              rateLockX = strVals[7].toFloat();
              rateLockY = strVals[8].toFloat();
              rateLockRotZ = strVals[9].toFloat();
              //Serial.println("read rates");
              //Serial.println(rateLockRotZ);
            }
          }
          else {
            if (stringToBoolean(strVals[3])) {
              rateLockX = strVals[4].toFloat();
              rateLockY = strVals[5].toFloat();
              rateLockRotZ = strVals[6].toFloat();
              //Serial.print("read rates");
              //Serial.println(rateLockRotZ);
            }
          }
      }

      //finally write the packet
      writePacket();
    }
  }
}

void writePacket() {

  Serial1.print(true_pitch);
  Serial1.print(",");
  Serial1.print(true_roll);
  Serial1.print(",");
  Serial1.print(rateW);
  Serial1.print(",");
  Serial1.print(rateX);
  Serial1.print(",");
  Serial1.print(rateY);
  Serial1.print(",");
  Serial1.print(alt_lock);
  Serial1.print(",");
  Serial1.print(relAlt);
  Serial1.print(",");
  Serial1.print(m1_speed);
  Serial1.print(",");
  Serial1.print(m2_speed);
  Serial1.print(",");
  Serial1.print(m3_speed);
  Serial1.print(",");
  Serial1.print(m4_speed);
  Serial1.print(",end");
}

void connectionCheck() {
  if (connectionCounter >= 10000) {
    connection = false;
  }
  connectionCounter++;
}

bool stringToBoolean(String theString) {
  if (theString == "true") {
    return true;
  }
  else if (theString == "false") {
    return false;
  }
  else {
    return false;
  }
}
