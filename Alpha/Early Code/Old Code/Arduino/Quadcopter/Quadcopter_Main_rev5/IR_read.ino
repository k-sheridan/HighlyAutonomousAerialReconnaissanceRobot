void IR_read(){
  float volts = analogRead(A11) * 0.0048828125;
  dist_1 = 65 * pow(volts, -1.10);
  volts = analogRead(A10) * 0.0048828125;
  dist_2 = 65 * pow(volts, -1.10);
  volts = analogRead(A9) * 0.0048828125;
  dist_3 = 65 * pow(volts, -1.10);
  volts = analogRead(A8) * 0.0048828125;
  dist_4 = 65 * pow(volts, -1.10);
}
