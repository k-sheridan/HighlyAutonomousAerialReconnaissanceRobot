void processGraph(int xPos, int yPos, float data1, float data2, float data3) {
  fill(255);
  rect(xPos, yPos, 390, 200);
  textAlign(LEFT);
  text("0", (xPos + 393), (yPos + 101));
  text("-15", (xPos + 393), (yPos + 131));
  text("15", (xPos + 393), (yPos + 71));
  /* Generate lines */
  stroke(200);
  strokeWeight(1);
  line(xPos, (yPos + 100), (xPos + 390), (yPos + 100));
  strokeWeight(1);
  line(xPos, (yPos + 130), (xPos + 390), (yPos + 130));
  line(xPos, (yPos + 70), (xPos + 390), (yPos + 70));
  stroke(255, 0, 0);
  strokeWeight(1);
  for (int i = 0; i < 39; i++) {
    line((xPos + (i * 10)), (yPos + 100 + (d1[i] * 2)), (xPos + ((i + 1) * 10)), (yPos + 100 + ((d1[i + 1]) * 2)));
  }
  /* Shift data to the left */
  for (int i = 0; i < 39; i++) {
    d1[i] = d1[i + 1];
  }
  d1[39] = data1;


  stroke(0, 255, 0);
  strokeWeight(1);
  for (int i = 0; i < 39; i++) {
    line((xPos + (i * 10)), (yPos + 100 + (d2[i] * 2)), (xPos + ((i + 1) * 10)), (yPos + 100 + ((d2[i + 1]) * 2)));
  }
  /* Shift data to the left */
  for (int i = 0; i < 39; i++) {
    d2[i] = d2[i + 1];
  }
  d2[39] = data2;


  stroke(0, 0, 255);
  strokeWeight(1);
  for (int i = 0; i < 39; i++) {
    line((xPos + (i * 10)), (yPos + 100 + (d3[i] * 2)), (xPos + ((i + 1) * 10)), (yPos + 100 + ((d3[i + 1]) * 2)));
  }
  /* Shift data to the left */
  for (int i = 0; i < 39; i++) {
    d3[i] = d3[i + 1];
  }
  d3[39] = (int)data3;

  stroke(0);
}

