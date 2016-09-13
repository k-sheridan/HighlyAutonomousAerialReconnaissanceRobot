void drawGUI(){
  fill(0,0,0,100);
  rect(width - 290, height - 460, 290, 230);
  fill(255);
  
  overlayText();
  
  //odomGraph.updateValues(0, 0);
  rg.updateValues(rateX, rateY, 0, 0, SQUAL);
  
  motorChart.unshift("Motor 1", m1_speed);
  motorChart.unshift("Motor 2", m2_speed);
  motorChart.unshift("Motor 3", m3_speed);
  motorChart.unshift("Motor 4", m4_speed);
  
  cp5.draw();
}
