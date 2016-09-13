void cp5Init() {
  cp5 = new ControlP5(this);
  cp5.enableShortcuts();

  myTextarea = cp5.addTextarea("console")
    .setPosition(width - 500, 10)
      .setSize(500, 200)
        .setFont(createFont("", 12))
          .setLineHeight(14)
            .setColor(color(200))
              .setColorBackground(color(0, 0))
                .setColorForeground(color(255, 100));

  console = cp5.addConsole(myTextarea);

  cp5.addTextfield("dataFileText")
    .setPosition(width - 370, 250)
      .setSize(300, 30)
        .setFont(createFont("", 14))
          .setFocus(true)
            .setColorBackground(color(0))
              .setColor(color(255))
                .setCaptionLabel("Enter File Name Here");
  ;

  cp5.addButton("loadData")
    .setPosition(width - 460, 250)
      .setSize(60, 30)
        ;

  cp5.addToggle("start")
    .setPosition(width - 100, height - 150)
      .setSize(90, 30)
        .setMode(cp5.SWITCH)
        .setColorActive(color(0,255,0))
        ;
}


void controlEvent(ControlEvent event) {
  if (event.controller().name() == "dataFileText") {
    println(cp5.controller("dataFileText").getStringValue());
  }
  if (event.controller().name() == "loadData") {
    if (cp5.controller("loadData").value() == 1) {
      println("Loading Data from: " + cp5.controller("dataFileText").getStringValue());
      loadData(cp5.controller("dataFileText").getStringValue());
      println("File Loaded with " + flightData.length + " Lines of Data");
      println("First Line: " + flightData[0]);
      dataLoaded = true;
    }
  }
  if (event.controller().name() == "start") {
    if (start == false) {
      if(dataLoaded == true){
      start = true;
      println("Started");
      }
      else{
       println("Load Data First"); 
      }
    } else {
      start = false;
      println("stopped");
    }
  }
}

