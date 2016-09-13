void textWrite() {
  textAlign(LEFT);
  if (mode == 4) {
    fill(255, 0, 0);
    textSize(16);
    text("WARNING: Tuning mode is on. Control limited", 900, 50);
  }
  fill(255);
  textSize(14);
  text("Motor 1:", 10, 110);
  text(m1, 70, 110);
  text("Motor 2:", 10, 130);
  text(m2, 70, 130);
  text("Motor 3:", 10, 150);
  text(m3, 70, 150);
  text("Motor 4:", 10, 170);
  text(m4, 70, 170);
  text("Pitch:", 10, 200);
  text(pitch, 50, 200);
  text("Roll:", 10, 220);
  text(roll, 50, 220);
  text("Yaw:", 10, 240);
  text(yaw, 50, 240);
  text("Altitude:", 10, 270);
  text(alt, 70, 270);
  text("Relative Altitude:", 10, 290);
  text(relAlt, 130, 290); 
  text("GPS Lattitude:", 10, 320);
  text("GPS Longitude:", 10, 340);
  text("Cycle Time(uS):", 10, 360);
  text(cycleTime, 120, 360);
  text("Cycle Hertz:", 10, 380);
  if (cycleTime > 0) {
    text(1000000 / cycleTime, 120, 380);
  } else if (cycleTime == 0) {
    text(0, 120, 380);
  }
  textAlign(RIGHT);
  text("Direct Control - (1)", 810, 20);
  text("Semi-Direct Control - (2)", 1040, 20);
  text("Autonomous - (3)", 1210, 20);
  text("Tuning - (4)", 1330, 20);
  textAlign(LEFT);
  text("Read Error:", 30, 460);
  text(readPacketLoss, 108, 460);
  text("Write Error:", 30, 480);
  text(sendPacketLoss, 110, 480);
  textSize(11);
  text("Connection Status:", 10, 500);
  if (connected == true) {
    fill(0, 255, 0);
    text("CONNECTED", 120, 500);
  }
  if (connected == false) {
    fill(255, 0, 0);
    text("NOT CONNECTED", 120, 500);
  }
  fill(255);
  textSize(10);
  // EXTRA STATE INFORMATION
  text("Diagnostic Sensor Data:", 1200, 485);
  text("Gyro X:", 1155, 500);
  text(gyro_x, 1190, 500);
  text("Gyro Y:", 1155, 510);
  text(gyro_y, 1190, 510);
  text("Gyro Z:", 1155, 520);
  text(gyro_z, 1190, 520);
  text("Pitch Att:", 1155, 530);
  text(PAO, 1200, 530);
  text("Roll Att:", 1155, 540);
  text(RAO, 1200, 540);
  text("Pitch Rate:", 1155, 550);
  text(PRO, 1210, 550);
  text("Roll Rate:", 1155, 560);
  text(RRO, 1200, 560);
  text("Alt Hold:", 1155, 570);
  text(AHO, 1200, 570);
  text("Z Accel:", 1155, 580);
  text(Zaccel, 1200, 580);
  text("Z Sonar:", 1155, 590);
  text(Zsonar, 1200, 590);
  textAlign(RIGHT);
  textSize(12);
  text("Return To Home", 1260, 90);
  text("Auto Land", 1260, 240);
  text("Stop - (Space)", 1260, 390);
}

