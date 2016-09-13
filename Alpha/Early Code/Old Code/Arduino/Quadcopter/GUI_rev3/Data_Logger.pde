void logData(){
 if(logMode != 0){
  if(logMode == 1){
    dataLog.print(pitch + ",");
    dataLog.print(roll + ",");
    dataLog.print(yaw + ",");
    dataLog.print(motor_speed + ",");
    dataLog.print(m1 + ",");
    dataLog.print(m2 + ",");
    dataLog.print(m3 + ",");
    dataLog.print(m4 + ",");
    dataLog.print(dist_1 + ",");
    dataLog.print(dist_2 + ",");
    dataLog.print(dist_3 + ",");
    dataLog.print(dist_4 + ",");
    dataLog.print(relAlt + ",");
    dataLog.print(Zaccel + ",");
    dataLog.print(cycleTime + ",");
    dataLog.print(xPos + ",");
    dataLog.print(yPos + ",");
    dataLog.print(SQUAL + ",");
    dataLog.print((millis() - lastMillis) + ",");
    lastMillis = millis();
    dataLog.println(hour() + ":" + minute() + ":" + second());
  }
 } 
}
