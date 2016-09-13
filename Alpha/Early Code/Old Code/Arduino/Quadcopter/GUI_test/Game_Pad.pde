void getUserInput() {
  rX = gpad.getSlider("rX").getValue();
  rY = gpad.getSlider("rY").getValue();
  lX = gpad.getSlider("lX").getValue();
  lY = gpad.getSlider("lY").getValue();
  kZ = gpad.getSlider("kZ").getValue();
  s1 = gpad.getButton("s1").pressed();
  s2 = gpad.getButton("s2").pressed();
  b1 = gpad.getButton("b3").pressed();
  s4 = gpad.getButton("s4").pressed();
  s5 = gpad.getButton("s5").pressed();
  //println(rX + " " + rY + " " + lX + " " + lY + " " + s1 + " " + s2 + " " + b3 + " " + s4 + " " + s5);
}

void gamePadCompute() {
  motor_speed = (short)round(map(lY, -1, 1, 140, 254));
  motor_speed = (short)(constrain(motor_speed, 140, 254));
  pitch_lock = (short)round(map(rY, -1, 1, -15, 15));
  roll_lock = (short)round(map(rX, -1, 1, 15, -15));
  yaw_lock += (short)round(lX * 2);
  if(yaw_lock > 359){
    yaw_lock = 0;
  }
  if(yaw_lock < 0){
    yaw_lock = 359;
  }
  if(b1 == true){
    start = false;
    kill = 0;
  }
  //println(motor_speed + " " + pitch_lock + " " + roll_lock + " " + yaw_lock);
}

