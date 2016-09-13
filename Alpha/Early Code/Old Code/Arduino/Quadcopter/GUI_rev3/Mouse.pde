void mousePressed() {
  if (mouseX >= 1260 && mouseX <= 1350 && mouseY >= 60 && mouseY <= 150) {
    if (kill != 1) {
      kill = 1;
    } else if (kill == 1) {
      kill = 0;
    }
  }
  if(mouseX >= 1260 && mouseX <= 1350 && mouseY >= 210 && mouseY <= 300){
    if (kill != 2) {
      kill = 2;
    } else if (kill == 2) {
      kill = 0;
    }
  }
  if(mouseX >= 1260 && mouseX <= 1350 && mouseY >= 360 && mouseY <= 450){
    kill = 0;
    start = false;
  }

  if (mouseX >= 15 && mouseX <= 105 && mouseY >= 600 && mouseY <= 630) {
    readPacket();
    readPacket();
    writePacket('a');
    image(connect_down, 15, 600, 90, 30);
  }

  if (mouseX >= 15 && mouseX <= 105 && mouseY >= 640 && mouseY <= 670) {
    start = true;
    image(start_down, 15, 640, 90, 30);
  }
}

