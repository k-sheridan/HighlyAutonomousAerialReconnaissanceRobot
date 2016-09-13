void gui() {
  hint(DISABLE_DEPTH_TEST);
  cam.beginHUD();
  strokeWeight(0);
  fill(0,0,0,75);
  rect(width - 510, 0, 550, height);
  cp5.draw();
  drawText();
  cam.endHUD();
  hint(ENABLE_DEPTH_TEST);
}
