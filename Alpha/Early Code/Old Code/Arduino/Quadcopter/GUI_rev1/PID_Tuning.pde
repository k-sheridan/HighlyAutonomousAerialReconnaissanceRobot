void PIDtuningDisplay() {
  if (modeTab != 4) {
    cp5.controller("TP_apply").hide();
    cp5.controller("PR_activate").hide();
    cp5.controller("RA_switch").hide();
    cp5.controller("proportional").hide();
    cp5.controller("integral").hide();
    cp5.controller("derivative").hide();
  }
  if (modeTab == 4) {                //tuning tab:
    image(m4_bg, 225, 60, 950, 400);
    cp5.controller("TP_apply").show();
    textAlign(LEFT);
    text("Tuning Parameters:", 250, 180);
    cp5.controller("PR_activate").show();
    cp5.controller("proportional").show();
    cp5.controller("integral").show();
    cp5.controller("derivative").show();
    
    if (comMode == 4) {
      image(tuning_on, 270, 80, 100, 50);
      image(g_on, 230, 80, 50, 50);
    }
    if (comMode != 4) {
      image(tuning_off, 270, 80, 100, 50);
      image(r_on, 230, 80, 50, 50);
    }
    image(num_box, 500, 100, 150, 50);
    image(num_box, 700, 100, 150, 50);
    image(num_box, 900, 100, 150, 50);
    textSize(16);
    textAlign(LEFT);
    text("Tuning Mode Toggle", 250, 150);
    textSize(21);
    text("Proportional", 510, 90);
    text("Integral", 730, 90);
    text("Derivative", 910, 90);
    if (comMode == 4) {
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
    if (comMode != 4) {
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

