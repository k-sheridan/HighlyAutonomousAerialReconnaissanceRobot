int inputVal;


void setup() {
 Serial.begin(115200); 
 pinMode(7, OUTPUT);
}

void loop() {
  if (Serial.available() > 0) {
            // read the incoming byte:
            inputVal = Serial.read();
                //potVal = (int) inputVal;
            if(inputVal == 49){
              digitalWrite(7, HIGH);
              Serial.println(1, HEX);
            }
            if(inputVal == 48){
              digitalWrite(7, LOW);
            }     
            // say what you got:
            Serial.print("I received: ");
            Serial.print("raw = ");
  Serial.println(inputVal);  // print as an ASCII-encoded decimal
  
  Serial.print("hex = ");
  Serial.println(inputVal, HEX);  // print as an ASCII-encoded hexadecimal
  
  Serial.print("oct = ");
  Serial.println(inputVal, OCT);  // print as an ASCII-encoded octal
  
  Serial.print("bin = ");
  Serial.println(inputVal, BIN);  // print as an ASCII-encoded binary

  // delay 10 milliseconds before the next reading:
  delay(0);
      }

}
