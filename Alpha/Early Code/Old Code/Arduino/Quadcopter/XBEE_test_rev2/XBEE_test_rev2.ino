int inputVal;


void setup() {
 Serial.begin(115200); 
 pinMode(7, OUTPUT);
}

void loop() {
  if (Serial.available() > 0) {
            inputVal = Serial.read();
            Serial.println(inputVal);
      }
  Serial.println("hello");
  delay(100);

}
