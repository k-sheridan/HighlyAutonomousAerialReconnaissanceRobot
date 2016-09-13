void drawGrid() {
  stroke(255);
  strokeWeight(1);
  for (int i = -1000; i <= 1000; i += 100) {
    line(i, 0, 1000, i, 0, -1000);
  }
  for (int i = -1000; i <= 1000; i += 100) {
    line(1000, 0, i, -1000, 0, i);
  }
}

