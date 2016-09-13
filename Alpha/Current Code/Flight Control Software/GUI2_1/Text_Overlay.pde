void overlayText() {
  textAlign(RIGHT);
  text("P:", width - 130, height - 390);
  text("I:", width - 130, height - 310);
  text("D:", width - 130, height - 240);
  textAlign(LEFT);
  text(tp, width - 125, height - 390);
  text(ti, width - 125, height - 310);
  text(td, width - 125, height - 240);
  
  textAlign(RIGHT);
  text("Pitch: ", width - 500, height - 200);
  text("Roll: ", width - 500, height - 190);
  text("Yaw: ", width - 500, height - 180);
  text("Relative Alt: ", width - 370, height - 200);
  textAlign(LEFT);
  text(pitch, width - 500, height - 200);
  text(roll, width - 500, height - 190);
  text(yaw, width - 500, height - 180);
  text(relalt, width - 370, height - 200);
}

