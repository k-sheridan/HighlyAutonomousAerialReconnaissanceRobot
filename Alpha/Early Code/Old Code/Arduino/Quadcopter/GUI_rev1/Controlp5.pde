void controlP5Init() {
  cp5 = new ControlP5(this);
  cp5.enableShortcuts();

  cp5.addButton("TP_apply")
    .setValue(0)
      .setPosition(250, 400)
        .setSize(85, 20)
          .setColorBackground(#797C00)
            .setColorForeground(#A7AA00)
              .setColorActive(#F5FA00)
                .setCaptionLabel("Apply PID Settings")
                  ;

  cp5.addToggle("PR_activate")
    .setPosition(250, 190)
      .setSize(50, 20)
        .setCaptionLabel("Pitch / Roll")
          .setColorBackground(#797C00)
            .setColorForeground(#A7AA00)
              .setColorActive(#F5FA00)
                ;

  cp5.addToggle("RA_switch")
    .setPosition(320, 190)
      .setSize(60, 20)
        .setCaptionLabel("RATE  -  ATTITUDE")
          .setMode(ControlP5.SWITCH)
            .setColorBackground(#797C00)
              .setColorForeground(#A7AA00)
                .setColorActive(#F5FA00)
                  ;


  myTextarea = cp5.addTextarea("txt")
    .setPosition(690, 470)
      .setSize(300, 210)
        .setFont(createFont("", 12))
          .setLineHeight(14)
            .setColor(color(200))
              .setColorBackground(color(0, 190))
                .setColorForeground(color(255, 190))
                  ;

  console = cp5.addConsole(myTextarea);

  cp5.addTextfield("proportional")
    .setPosition(509, 107)
      .setSize(130, 35)
        .setFont(textFieldFont)
          .setFocus(false)
            .setColor(color(255))
              .setColorActive(#FFFFFF)
                .setColorBackground(#000000)
                  .setColorForeground(#DEDEDE)
                    .setColorValueLabel(#FFFFFF)
                      .setAutoClear(true)
                        .setCaptionLabel("");
  ;
  cp5.addTextfield("integral")
    .setPosition(709, 107)
      .setSize(130, 35)
        .setFont(textFieldFont)
          .setFocus(false)
            .setColor(color(255))
              .setColorActive(#FFFFFF)
                .setColorBackground(#000000)
                  .setColorForeground(#DEDEDE)
                    .setColorValueLabel(#FFFFFF)
                      .setAutoClear(true)
                        .setCaptionLabel("");
  ;
  cp5.addTextfield("derivative")
    .setPosition(909, 107)
      .setSize(130, 35)
        .setFont(textFieldFont)
          .setFocus(false)
            .setColor(color(255))
              .setColorActive(#FFFFFF)
                .setColorBackground(#000000)
                  .setColorForeground(#DEDEDE)
                    .setColorValueLabel(#FFFFFF)
                      .setAutoClear(true)
                        .setCaptionLabel("");
  ;

  motorChart = cp5.addChart("Motor Speeds")
    .setPosition(1000, 470)
      .setSize(150, 210)
        .setRange(1110, 2000)
          .setView(Chart.BAR)
            .setColorActive(#FFFFFF)
              .setColorBackground(#000000)
                .setColorForeground(#DEDEDE)
                  .setColorValueLabel(#FFFFFF)
                    ;
  
  motorChart.addDataSet("Motor 1");
  motorChart.setColors("Motor 1", color(#FF0000));
  motorChart.setData("Motor 1", m1);
  
  motorChart.addDataSet("Motor 2");
  motorChart.setColors("Motor 2", color(#0BFF00));
  motorChart.setData("Motor 2", m2);
  
  motorChart.addDataSet("Motor 3");
  motorChart.setColors("Motor 3", color(#0046FF));
  motorChart.setData("Motor 3", m3);
  
  motorChart.addDataSet("Motor 4");
  motorChart.setColors("Motor 4", color(#FAFF00));
  motorChart.setData("Motor 4", m4);
}



public void controlEvent(ControlEvent Event) {
  if (Event.controller().name() == "TP_apply") {
    p_val = Tp;
    i_val = Ti;
    d_val = Td;
    print("PID Settings Have Been Set" + " P: " + p_val + " I: " + i_val + " D: " + d_val);
    switch(tuningParam) {
    case 1:
      println(" to Pitch/Roll ATTITUDE PID");
      break;
    case 2:
      println(" to Pitch/Roll RATE PID");
      break;
    default:
      println(" to UNKNOWN!");
      break;
    }
  }

  if (Event.controller().name() == "PR_activate") {
    if (Event.controller().value() == 1) {
      cp5.controller("RA_switch").show();
      cp5.controller("RA_switch").setValue(1);
      tuningParam = 2;
    } else {
      cp5.controller("RA_switch").hide();
    }
  }

  if (Event.controller().name() == "RA_switch" && cp5.controller("PR_activate").value() == 1) {
    if (Event.controller().value() == 1) {
      tuningParam = 2;
    } else {
      tuningParam = 1;
      println(tuningParam);
    }
  }

  if (Event.controller().name() == "proportional") {
    if (Float.isNaN(float(Event.controller().getStringValue()))) {
      println("Error: Proportional Value is NaN (not a number)");
    } else {
      Tp = float(Event.controller().getStringValue());
    }
  }
  if (Event.controller().name() == "integral") {
    if (Float.isNaN(float(Event.controller().getStringValue()))) {
      println("Error: Integral Value is NaN (not a number)");
    } else {
      Ti = float(Event.controller().getStringValue());
    }
  }
  if (Event.controller().name() == "derivative") {
    if (Float.isNaN(float(Event.controller().getStringValue()))) {
      println("Error: Derivative Value is NaN (not a number)");
    } else {
      Td = float(Event.controller().getStringValue());
    }
  }

  // TUNING MODE_________________-------------------____________________________------------------------------
}

