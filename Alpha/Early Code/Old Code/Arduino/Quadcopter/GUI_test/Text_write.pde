void textWrite() {
  textAlign(LEFT);
  if(comMode == 4){
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
  text("GPS Lattitude:", 10, 320);
  text("GPS Longitude:", 10, 340);
  text("Direct Control - (1)", 835, 20);
  text("Semi-Direct Control - (2)", 1000, 20);
  text("Autonomous - (3)", 1200, 20);
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
  textAlign(RIGHT);
  textSize(12);
  text("Return To Home", 1260, 90);
  text("Auto Land", 1260, 240);
  text("Stop - (Space)", 1260, 390);
}

