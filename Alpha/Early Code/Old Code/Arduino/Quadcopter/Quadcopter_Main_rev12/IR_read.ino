void IR_read(){
  float volts = analogRead(A1) * 0.0048828125;
  dist_1 = 65 * pow(volts, -1.10);
  volts = analogRead(A2) * 0.0048828125;
  dist_2 = 65 * pow(volts, -1.10);
  volts = analogRead(A0) * 0.0048828125;
  dist_3 = 65 * pow(volts, -1.10);
  volts = analogRead(A3) * 0.0048828125;
  dist_4 = 65 * pow(volts, -1.10);
}
