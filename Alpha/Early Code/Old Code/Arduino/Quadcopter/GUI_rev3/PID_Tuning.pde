void PIDtuningDisplay() {
  if (mode != 4) {
    cp5.controller("TP_apply").hide();
    cp5.controller("PR_activate").hide();
    cp5.controller("Yaw_activate").hide();
    cp5.controller("altHold_activate").hide();
    cp5.controller("RA_switch").hide();
    cp5.controller("proportional").hide();
    cp5.controller("integral").hide();
    cp5.controller("derivative").hide();
  }
  if (mode == 4) {     //tuning tab:
    image(m4_bg, 225, 60, 950, 400);
    processGraph(450, 200, Zaccel, pitch, Zsonar);
    if (cp5.controller("altHold_activate").value() == 1) {
      textAlign(CENTER); 
      if (s2 == true) {
        tuningParam = 4;
        text("MANUAL", 340, 290);
      } else {
        tuningParam = 5;
        text("AUTOMATIC", 340, 290);
      }
    }
    cp5.controller("TP_apply").show();
    textAlign(LEFT);
    text("Tuning Parameters:", 250, 180);
    textAlign(RIGHT);
    text("Pitch:", 1000, 370);
    textAlign(LEFT);
    text(pitch_lock, 1005, 370);
    textAlign(RIGHT);
    text("Roll:", 1000, 380);
    textAlign(LEFT);
    text(roll_lock, 1005, 380);
    textAlign(RIGHT);
    text("Yaw:", 1000, 390);
    textAlign(LEFT);
    text(yaw_lock, 1005, 390);
    textAlign(RIGHT);
    text("Alt Hold:", 1000, 400);
    textAlign(LEFT);
    text(alt_lock, 1005, 400);
    cp5.controller("PR_activate").show();
    cp5.controller("Yaw_activate").show();
    cp5.controller("altHold_activate").show();
    cp5.controller("proportional").show();
    cp5.controller("integral").show();
    cp5.controller("derivative").show();

    image(num_box, 500, 100, 150, 50);
    image(num_box, 700, 100, 150, 50);
    image(num_box, 900, 100, 150, 50);
    textSize(16);
    textAlign(LEFT);
    text("Tuning Mode", 250, 110);
    textSize(21);
    text("Proportional", 510, 90);
    text("Integral", 730, 90);
    text("Derivative", 910, 90);
    if (mode == 4) {
      if (cp5.controller("proportional").isActive() == true) {
        image(y_on, 450, 100, 50, 50);
        image(y_off, 650, 100, 50, 50);
        image(y_off, 850, 100, 50, 50);
      }
      if (cp5.controller("integral").isActive() == true) {
        image(y_off, 450, 100, 50, 50);
        image(y_on, 650, 100, 50, 50);
        image(y_off, 850, 100, 50, 50);
      }
      if (cp5.controller("derivative").isActive() == true) {
        image(y_off, 450, 100, 50, 50);
        image(y_off, 650, 100, 50, 50);
        image(y_on, 850, 100, 50, 50);
      } else if (cp5.controller("derivative").isActive() == false && cp5.controller("integral").isActive() == false 
        && cp5.controller("proportional").isActive() == false) {
        image(y_off, 450, 100, 50, 50);
        image(y_off, 650, 100, 50, 50);
        image(y_off, 850, 100, 50, 50);
      }
    }
    if (mode != 4) {
      image(y_off, 450, 100, 50, 50);
      image(y_off, 650, 100, 50, 50);
      image(y_off, 850, 100, 50, 50);
    }
    textSize(18);
    fill(255);
    text(Tp, 530, 165);
    text(Ti, 730, 165);
    text(Td, 930, 165);
  }
}

