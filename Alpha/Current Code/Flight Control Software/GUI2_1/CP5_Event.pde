public void controlEvent(ControlEvent Event) {
  String ENAME = Event.name();

  if (ENAME == "arm") {
    float armVal = Event.value();
    println(Event.value());
    if (armVal == 1) {
      println("System Armed");
      start = true;
    } else {
      println("System DISArmed");
      start = false;
    }
  }
  
  else if(ENAME == "proportional"){
    float tfloat = float(Event.getStringValue());
    tp = tfloat;
  }
  
  else if(ENAME == "integral"){
    float tfloat = float(Event.getStringValue());
    ti = tfloat;
  }
  
  else if(ENAME == "derivative"){
    float tfloat = float(Event.getStringValue());
    td = tfloat;
  }
  
  else if(ENAME == "pid_apply"){
    zeroRateX = rateX; // get rid of
    zeroRateY = rateY;    
  }
  
  else if(ENAME == "pitch_lock"){
    pitch_lock = round(Event.value());  
  }
  
  else if(ENAME == "roll_lock"){
    roll_lock = round(Event.value()); 
  }
  
  else if(ENAME == "yaw_lock"){
    yaw_lock = round(Event.value()); 
  }
  
  else if(ENAME == "alt_lock"){
    alt_lock = round(Event.value()); 
  }
  
  else {
    println("UNKNOWN: " + Event);
  }
}

