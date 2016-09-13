void serialReceive() {
  if (Serial.available() > 0) {
    if (start == false) {
      if (Serial.read() == 's') {
//        Serial.println();
//        Serial.println();
//        Serial.println();
//        Serial.print("Starting in: 3");
//        delay(500);
//        Serial.print(" - ");
//        delay(500);
//        Serial.print("2");
//        delay(500);
//        Serial.print(" - ");
//        delay(500);
//        Serial.print("1");
//        delay(500);
//        Serial.print(" - ");
//        delay(500);
        Serial.print("Start");
        delay(500);
        start = true;
      }
    }
    if (start == true) {
      int _read;
      _read = Serial.read();
      if (_read == '/') {
       
        analogWrite(motor1, 140);
        analogWrite(motor2, 140);
        analogWrite(motor3, 140);
        analogWrite(motor4, 140);
        start = false;
        Serial.read();
        Serial.println("motors at 140");
      }
      if (_read == 'm') {
        motor_speed = Serial.parseInt();
        Serial.read();
      }
      if(_read == 'p'){
        p_mult = (Serial.parseInt() / 1000);
        Serial.read();
        Serial.println(p_mult);
      }
      if(_read == 'd'){
        d_mult = (Serial.parseInt() / 1000);
        Serial.read();
        Serial.println(d_mult);
      }
      if(_read == 'q'){
       pitch_lock = Serial.parseInt();
       Serial.read();
      }
    }
  }
}
