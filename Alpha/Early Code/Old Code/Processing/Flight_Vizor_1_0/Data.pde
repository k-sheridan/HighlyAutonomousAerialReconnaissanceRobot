void loadData(String fileName) {
  flightData = loadStrings("/datasets/" + fileName);
  quadX = expand(quadX, flightData.length);
  quadY = expand(quadY, flightData.length);
  quadZ = expand(quadZ, flightData.length);
}

void convertData(int lineNumber) {
  String temp[] = split(flightData[lineNumber], ",");
  pitch = Float.parseFloat(temp[0]);
  roll = Float.parseFloat(temp[1]);
  yaw = Float.parseFloat(temp[2]);
  motor_speed = Integer.parseInt(temp[3]);
  m1 = Float.parseFloat(temp[4]);
  m2 = Float.parseFloat(temp[5]);
  m3 = Float.parseFloat(temp[6]);
  m4 = Float.parseFloat(temp[7]);
  dist_1 = Integer.parseInt(temp[8]);
  dist_2 = Integer.parseInt(temp[9]);
  dist_3 = Integer.parseInt(temp[10]);
  dist_4 = Integer.parseInt(temp[11]);
  relAlt = Integer.parseInt(temp[12]);
  Zaccel = Float.parseFloat(temp[13]);
  cycleTime = Integer.parseInt(temp[14]);
  xPos = Float.parseFloat(temp[15]);
  yPos = Float.parseFloat(temp[16]);
  SQUAL = Integer.parseInt(temp[17]);
  deltaMillis = Integer.parseInt(temp[18]);
  time = temp[19];
}

void generateCoordinates() { // only minor data filtering
  lastSystemMillis = millis();
  if (relAlt > minAlt && SQUAL > minSQUAL) {
    if (dist_1 < 50) {
      Xpoint[pointCount] = round((xPos * posMult) + (dist_1 * distMult + 10) * cos(radians(yaw - 90)));
      Ypoint[pointCount] = relAlt * -1;
      Zpoint[pointCount] = round((yPos * posMult) + (dist_1 * distMult + 10) * sin(radians(yaw - 90)));
      pointCount++;
    }
    if (dist_2 < maxDist) {
      Xpoint[pointCount] = round((xPos * posMult) + (dist_2 * distMult + 10) * cos(radians(yaw)));
      Ypoint[pointCount] = relAlt * -1;
      Zpoint[pointCount] = round((yPos * posMult) + (dist_2 * distMult + 10) * sin(radians(yaw)));
      pointCount++;
    }
    if (dist_4 < maxDist) {
      Xpoint[pointCount] = round((xPos * posMult) + (dist_4 * distMult + 10) * cos(radians(yaw - 180)));
      Ypoint[pointCount] = relAlt * -1;
      Zpoint[pointCount] = round((yPos * posMult) + (dist_4 * distMult + 10) * sin(radians(yaw - 180)));
      pointCount++;
    }
    if(relAlt >  colorDist){
      colorDist = relAlt;
    }
  }
  if (pointCount >= Xpoint.length - 4) {
    Xpoint = expand(Xpoint);
    Ypoint = expand(Ypoint);
    Zpoint = expand(Zpoint);
  }
  //put quad data position data into arrays
  for(int i = 0; i < quadX.length - 1; i++){
    quadX[i] = quadX[i + 1];
    quadY[i] = quadY[i + 1];
    quadZ[i] = quadZ[i + 1];
  }
  quadX[quadX.length - 1] = round(xPos * posMult);
  quadY[quadX.length - 1] = round(relAlt * -1);
  quadZ[quadX.length - 1] = round(yPos * posMult);
}


