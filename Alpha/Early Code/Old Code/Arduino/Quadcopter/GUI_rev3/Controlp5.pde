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
        .setCaptionLabel("      Pitch")
          .setColorBackground(#797C00)
            .setColorForeground(#A7AA00)
              .setColorActive(#F5FA00)
                ;
  cp5.addToggle("Yaw_activate")
    .setPosition(250, 230)
      .setSize(50, 20)
        .setCaptionLabel("        Yaw")
          .setColorBackground(#797C00)
            .setColorForeground(#A7AA00)
              .setColorActive(#F5FA00)
                ;
  cp5.addToggle("altHold_activate")
    .setPosition(250, 270)
      .setSize(50, 20)
        .setCaptionLabel("Altitude Hold")
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

  /* DATA LOGGING */

  cp5.addToggle("dataRecord")
    .setPosition(120, 510)
      .setSize(80, 20)
        .setCaptionLabel("Record Data (off/on)")
          .setMode(ControlP5.SWITCH)
            .setColorBackground(#504F4F)
              .setColorForeground(#FFFFFF)
                .setColorActive(#FF0303)
                  .setValue(1)
                    ;
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
    case 3:
      println(" to Yaw RATE PID");
      break;
    case 4: 
      println(" to Altitude Hold PID");
      break;
    case 5: 
      println(" to Altitude Hold PID");
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
      cp5.controller("Yaw_activate").setValue(0);
      cp5.controller("altHold_activate").setValue(0);
      tuningParam = 2;
    } else {
      cp5.controller("RA_switch").hide();
    }
    println(tuningParam);
  }

  if (Event.controller().name() == "RA_switch" && cp5.controller("PR_activate").value() == 1) {
    if (Event.controller().value() == 1) {
      tuningParam = 2;
    } else {
      tuningParam = 1;
    }
    println(tuningParam);
  }

  if (Event.controller().name() == "Yaw_activate") {
    if (Event.controller().value() == 1) {
      tuningParam = 3;
      cp5.controller("RA_switch").hide();
      cp5.controller("PR_activate").setValue(0);
      cp5.controller("altHold_activate").setValue(0);
    }
  }

  if (Event.controller().name() == "altHold_activate") {
    if (Event.controller().value() == 1) {
      tuningParam = 4;
      cp5.controller("RA_switch").hide();
      cp5.controller("PR_activate").setValue(0);
      cp5.controller("Yaw_activate").setValue(0);
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

  //_________________-------------------____________________________------------------------------
  /* Data Logging */
  
  if(Event.controller().name() == "dataRecord"){
    if(cp5.controller("dataRecord").value() == 1){
      if(logMode != 0){
        println("File has been saved");
        dataLog.flush();
        dataLog.close();
      }
      logMode = 0;
    }
    else{
      logMode = 1;
      println("File has been created, and data logging has begun!");
      dataLog = createWriter("data_sets/FVMap-" + hour() + "-" + minute() + ".csv");
      dataLog.println("Complete data set for Project: Unknown refer to GUI for structure"); 
    }
  }
  
}

