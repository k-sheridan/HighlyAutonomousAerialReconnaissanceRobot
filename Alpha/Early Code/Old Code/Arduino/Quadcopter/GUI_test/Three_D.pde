void quadDraw() {
  pushMatrix();
  fill(255);
  lights();
  translate(550, 240, 38);
  rotateX(radians(pitch - 10));
  rotateZ(radians(roll));
  rotateY(radians(5));
  box(150, 30, 100);
  noLights();
  popMatrix();
}

