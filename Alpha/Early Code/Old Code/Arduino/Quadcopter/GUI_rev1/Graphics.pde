void drawGraphics() {
  noStroke();
  image(y_on, 975, 7, 20, 20);
  image(y_off, 1175, 7, 20, 20);
  image(y_off, 1330, 7, 20, 20);
  if (modeTab == 1) {              //Direct control visualisation tab
    image(m1_bg, 225, 60, 950, 400);
    quadDraw();
    image(pr_vis_bg, 320, 100, 15, 250);
    noStroke();
    fill(#008911);
    int pitch_lock_bar = (int)(225 + ((pitch_lock * -1) * 2.7));
    constrain(pitch_lock_bar, -45, 45);
    rect(322, pitch_lock_bar, 10, 3);
    fill(#8E0000);
    int pitch_bar = (int)(225 + ((pitch * -1) * 2.7));
    constrain(pitch_bar, -45, 45);
    rect(322, pitch_bar, 10, 3);
    fill(255);
    textAlign(RIGHT);
    text("Pitch", 340, 90);
    textSize(10);
    text("Pitch:", 280, (pitch_bar + 5));
    text("Pitch Lock:", 390, (pitch_lock_bar + 5));
    textAlign(LEFT);
    text(pitch_lock, 393, (pitch_lock_bar + 5));
    text(pitch, 285, (pitch_bar + 5));
    image(pr_vis_bg_rot, 350, 370, 250, 15);
    fill(#008911);
    int roll_lock_bar = (int)(475 + (roll_lock * 2.7));
    constrain(roll_lock_bar, -45, 45);
    rect(roll_lock_bar, 372, 3, 10);
    fill(#8E0000);
    int roll_bar = (int)(475 + (roll * 2.7));
    constrain(roll_lock_bar, -45, 45);
    rect(roll_bar, 372, 3, 10);
    fill(255);
    textAlign(RIGHT);
    textSize(12);
    text("Roll", 345, 380);
    textSize(10);
    textAlign(RIGHT);
    text("Roll:", roll_bar, 360);
    text("Roll Lock:", roll_lock_bar, 400);
    textAlign(LEFT);
    text(roll, (roll_bar + 3), 360);
    text(roll_lock, (roll_lock_bar + 3), 400);
    textSize(9);
    text("Pitch Lock:", 230, 380);
    text("Roll Lock:", 230, 390);
    text("Yaw Lock:", 230, 400);
    text("Motor Speed:", 230, 410);
    text(pitch_lock, 280, 380);
    text(roll_lock, 280, 390);
    text(yaw_lock, 280, 400);
    text(motor_speed, 290, 410);
    if (mode != 1) {
      fill(#FF0303);
      textSize(18);
      text("Mode 1 is not selected", 950, 90);
      textSize(10);
      fill(255);
    }
  }
  if (modeTab == 2) {
    image(m2_bg, 225, 60, 950, 400);
  }
  if (modeTab == 3) {
    image(m2_bg, 225, 60, 950, 400);
  }
  
  //TUNING------------------------------------------------------------
  PIDtuningDisplay();
  //TUNING------------------------------------------------------------
  //CHART-----------------
  motorChart.unshift("Motor 1", m1);
  motorChart.unshift("Motor 2", m2);
  motorChart.unshift("Motor 3", m3);
  motorChart.unshift("Motor 4", m4);
  //CHART------------------
  image(quad_bg, 0, 0, screenX, screenY);
  if (modeTab == 1) {
    image(m1_tab_up, 350, 30, 120, 30);
    image(m2_tab_down, 470, 30, 120, 30);
    image(m3_tab_down, 590, 30, 120, 30);
    image(m4_tab_down, 710, 30, 120, 30);
  }
  if (modeTab == 2) {
    image(m1_tab_down, 350, 30, 120, 30);
    image(m2_tab_up, 470, 30, 120, 30);
    image(m3_tab_down, 590, 30, 120, 30);
    image(m4_tab_down, 710, 30, 120, 30);
  }
  if (modeTab == 3) {
    image(m1_tab_down, 350, 30, 120, 30);
    image(m2_tab_down, 470, 30, 120, 30);
    image(m3_tab_up, 590, 30, 120, 30);
    image(m4_tab_down, 710, 30, 120, 30);
  }
  if (modeTab == 4) {
    image(m1_tab_down, 350, 30, 120, 30);
    image(m2_tab_down, 470, 30, 120, 30);
    image(m3_tab_down, 590, 30, 120, 30);
    image(m4_tab_up, 710, 30, 120, 30);
  }
  fill(255);
  image(quad_border, 0, 0, screenX, screenY);
  image(quad_top, 275, 500, 150, 150);
  image(quad_side, 525, 575, 150, 30);
  textSize(12);
  textAlign(LEFT);
  text("DIAGNOSTIC", 795, 467);
  text("M1     M2     M3     M4", 1008, 467);
  
  //-----------------------DISTANCE VISUALIZATION-----------------------------------
  
  if (connected == true) {
    textSize(12);
    fill(255);
    color visColor;
    float colorVal;
    int delta = (int)map(dist_1, 0, 200, 0, 50);
    colorVal = dist_1 * 0.005;
    visColor = lerpColor(color(255, 0, 0), color(0, 255, 0), colorVal);
    fill(visColor);
    rect(328, (515 - delta), 50, 5);
    fill(255);
    text(dist_1, 380, (520 - delta));

    delta = (int)map(dist_2, 0, 200, 0, 50);
    colorVal = dist_2 * 0.005;
    visColor = lerpColor(color(255, 0, 0), color(0, 255, 0), colorVal);
    fill(visColor);
    rect((410 + delta), 550, 5, 50);
    fill(255);
    text(dist_2, (410 + delta), 610);

    delta = (int)map(dist_3, 0, 200, 0, 50);
    colorVal = dist_3 * 0.005;
    visColor = lerpColor(color(255, 0, 0), color(0, 255, 0), colorVal);
    fill(visColor);
    rect(328, (630 + delta), 50, 5);
    textAlign(RIGHT);
    fill(255);
    text(dist_3, 323, (635 + delta));

    delta = (int)map(dist_4, 0, 200, 0, 50);
    colorVal = dist_4 * 0.005;
    visColor = lerpColor(color(255, 0, 0), color(0, 255, 0), colorVal);
    fill(255);
    text(dist_4, (300 - delta), 545);
    fill(visColor);
    rect((295 - delta), 550, 5, 50);

    //----------------------------------------------------------------------------

    fill(255);
    textAlign(LEFT);
  }
  image(connect_up, 15, 600, 90, 30);
  image(start_up, 15, 640, 90, 30);
  if (kill != 1) {
    image(RTH_off, 1230, 30, 150, 150);
  }
  if (kill == 1) {
    image(RTH_on, 1230, 30, 150, 150);
  }
  if (kill != 2) {
    image(AL_off, 1230, 180, 150, 150);
  }
  if (kill == 2) {
    image(AL_on, 1230, 180, 150, 150);
  }
  if (start == true) {
    image(stop_off, 1230, 330, 150, 150);
  }
  if (start == false) {
    image(stop_on, 1230, 330, 150, 150);
  }
  if (mode == 1) {
    image(y_on, 975, 7, 20, 20);
    image(y_off, 1175, 7, 20, 20);
    image(y_off, 1330, 7, 20, 20);
  }
  if (mode == 2) {
    image(y_off, 975, 7, 20, 20);
    image(y_on, 1175, 7, 20, 20);
    image(y_off, 1330, 7, 20, 20);
  }
  if (mode == 3) {
    image(y_off, 975, 7, 20, 20);
    image(y_off, 1175, 7, 20, 20);
    image(y_on, 1330, 7, 20, 20);
  }
  if (readError == false) {
    image(r_off, 5, 445, 20, 20);
  }
  if (readError == true) {
    image(r_on, 5, 445, 20, 20);
  }
  if (sendError == false) {
    image(r_off, 5, 465, 20, 20);
  }
  if (sendError == true) {
    image(r_on, 5, 465, 20, 20);
  }
  int Quad_ms = round(map(motor_speed, 140, 254, 1110, 2000));
  image(speed_bg, 1080, 110, 50, 300);
  textSize(12);
  fill(255);
  text("Motor Speed", 1065, 100);
  int motorS_height = 254 - motor_speed;
  float lerpVal = (float)motorS_height / 114.0;
  motorS_height = round(motorS_height * 2.474);
  color c1, c2;
  c1 = color(0, 255, 0);
  c2 = color(255, 0, 0);
  c2 = lerpColor(c2, c1, lerpVal);   //LERCOLOR!
  motorS_height += 118;
  setGradient(1093, motorS_height, 22, (400 - motorS_height), c2, c1, 1);
  textAlign(RIGHT);
  text(Quad_ms, 1085, motorS_height + 4);
  textAlign(LEFT);
  fill(255);
  
}





void setGradient(int x, int y, float w, float h, color c1, color c2, int axis ) {

  noFill();

  if (axis == 1) {  // Top to bottom gradient
    for (int i = y; i <= y+h; i++) {
      float inter = map(i, y, y+h, 0, 1);
      color c = lerpColor(c1, c2, inter);
      stroke(c);
      line(x, i, x+w, i);
    }
  } else if (axis == 2) {  // Left to right gradient
    for (int i = x; i <= x+w; i++) {
      float inter = map(i, x, x+w, 0, 1);
      color c = lerpColor(c1, c2, inter);
      stroke(c);
      line(i, y, i, y+h);
    }
  }
}

