int dist_1;
int dist_2;

void setup() {
  Serial.begin(9600);
}

void loop() {
  float volts = analogRead(A1) * 0.0048828125;
  dist_1 = 65 * pow(volts, -1.10);
  volts = analogRead(A2) * 0.0048828125;
  dist_2 = 65 * pow(volts, -1.10);
  //Serial.print(dist_2);
  Serial.print(" ");
  Serial.println(dist_1);
  delay(50);
}


