void controlP5Init() {
  cp5 = new ControlP5(this);
  cp5.enableShortcuts();
  cp5.setAutoDraw(false);

  myTextarea = cp5.addTextarea("txt")            //console
    .setPosition(width - 310, height - 220)
      .setSize(300, 210)
        .setFont(createFont("", 12))
          .setLineHeight(14)
            .setColor(color(200))
              .setColorBackground(color(0, 190))
                .setColorForeground(color(255, 190))
                  ;
  console = cp5.addConsole(myTextarea);

  //--------------------------------

  cp5.addToggle("arm")
    .setValue(0)
      .setPosition(width - 90, 10)
        .setSize(80, 20)
          .setCaptionLabel("Arm System");

  cp5.addButton("pid_apply")
    .setPosition(width - 240, height - 290)
      .setSize(80, 20)
        .setCaptionLabel("Apply PID Settings");

  //---------------------------------
  PFont pidFont = loadFont("FreeMono-24.vlw");

  cp5.addTextfield("proportional")
    .setPosition(width - 140, height - 450)
      .setSize(130, 35)
        .setFont(pidFont)
          .setFocus(false)
            .setAutoClear(true)
              .setCaptionLabel("Proportional");
  ;
  cp5.addTextfield("integral")
    .setPosition(width - 140, height - 370)
      .setSize(130, 35)
        .setFont(pidFont)
          .setFocus(false)
            .setAutoClear(true)
              .setCaptionLabel("Integral");
  ;
  cp5.addTextfield("derivative")
    .setPosition(width - 140, height - 300)
      .setSize(130, 35)
        .setFont(pidFont)
          .setFocus(false)
            .setAutoClear(true)
              .setCaptionLabel("Derivative");

  //----------------------------------

  d1 = cp5.addDropdownList("pid_list")
    .setPosition(width - 280, height - 440)
      .setSize(120, 100);

  d1.addItem("Pitch Roll Rate", 0);
  d1.addItem("Pitch Roll Attitude", 1);
  d1.addItem("Yaw Rate", 2);
  d1.addItem("Altitude PID", 3);
  d1.addItem("Position PID", 4);

  //-----------------------------------

  cp5.addSlider("pitch_lock")
    .setPosition(width - 520, height - 150)
      .setSize(150, 20)
        .setRange(-15, 15)
          .showTickMarks(true);
          
  cp5.addSlider("roll_lock")
    .setPosition(width - 520, height - 100)
      .setSize(150, 20)
        .setRange(-15, 15)
          .showTickMarks(true);
          
  cp5.addSlider("yaw_lock")
    .setPosition(width - 520, height - 50)
      .setSize(150, 20)
        .setRange(-10, 10)
          .showTickMarks(true);
  
  cp5.addSlider("alt_lock")
    .setPosition(360, 50)
      .setSize(20, 190)
        .setRange(0, 150)
          .showTickMarks(true);        
          
  motorChart = cp5.addChart("Motor Speeds")
    .setPosition(400, 45)
      .setSize(150, 210)
        .setRange(1100, 2000)
          .setView(Chart.BAR)
            .setColorActive(#FFFFFF)
              .setColorBackground(#000000)
                .setColorForeground(#DEDEDE)
                  .setColorValueLabel(#FFFFFF)
                    ;

  motorChart.addDataSet("Motor 1");
  motorChart.setColors("Motor 1", color(#FF0000));
  motorChart.setData("Motor 1", m1_speed);

  motorChart.addDataSet("Motor 2");
  motorChart.setColors("Motor 2", color(#0BFF00));
  motorChart.setData("Motor 2", m2_speed);

  motorChart.addDataSet("Motor 3");
  motorChart.setColors("Motor 3", color(#0046FF));
  motorChart.setData("Motor 3", m3_speed);

  motorChart.addDataSet("Motor 4");
  motorChart.setColors("Motor 4", color(#FAFF00));
  motorChart.setData("Motor 4", m4_speed);
}

