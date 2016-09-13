void displayData() {
  pushMatrix();
  lights();
  strokeWeight(0);
  fill(#B4AF04);
  translate((xPos * posMult), (relAlt * -1), (yPos * posMult));
  rotateX(radians(pitch));
  rotateY(radians(yaw * -1));
  rotateZ(radians(roll));
  box(30, 5, 30);
  popMatrix();

  //display point cloud
  color c1 = color(255, 0, 0);
  color c2 = color(0, 255, 0);
  color c3 = color(0, 0, 255);
  strokeWeight(3);
  for (int i = 0; i < pointCount; i++) {
    if (abs(Ypoint[i]) < colorDist / 2) {
      stroke(lerpColor(c3, c2, (abs(Ypoint[i]) / (colorDist / 2))));
      point(Xpoint[i], Ypoint[i], Zpoint[i]);
    } else {
      stroke(lerpColor(c2, c1, ((abs(Ypoint[i]) - (colorDist / 2))) / (colorDist / 2.0)));
      point(Xpoint[i], Ypoint[i], Zpoint[i]);
    }
  }
  //display position history
  strokeWeight(1);
  stroke(255,255,255,10);
  for (int i = 0; i < quadX.length - 1; i++) {
    line(quadX[i], quadY[i], quadZ[i], quadX[i + 1], quadY[i + 1], quadZ[i + 1]);
  }
}

