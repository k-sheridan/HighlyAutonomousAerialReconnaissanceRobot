int val;
char ID;

void setup() {
  Serial.begin(115200);
  pinMode(3, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(9, OUTPUT);
  analogWrite(3, 140);
  analogWrite(9, 140);
  analogWrite(10, 140);
  analogWrite(11, 140);
}

void loop() {
if(Serial.available() > 0)
{
  ID = Serial.read();
  if(ID == '/')
  {
    val = Serial.parseInt();
    analogWrite(3, val);
    analogWrite(9, val);
    analogWrite(10, val);
    analogWrite(11, val);
    Serial.read();
    Serial.print("All motors are at: ");
    Serial.println(val);
  }
  if(ID == 'a')
  {
    val = Serial.parseInt();
    analogWrite(3, val);
    Serial.read();
    Serial.print("MOTOR: A is ");
    Serial.println(val);  
  }
  if(ID == 'b')
  {
    val = Serial.parseInt();
    analogWrite(9, val);
    Serial.read();
    Serial.print("MOTOR: B is ");
    Serial.println(val);
  }
  if(ID == 'c')
  {
    val = Serial.parseInt();
    analogWrite(10, val);
    Serial.read();
    Serial.print("MOTOR: C is ");
    Serial.println(val);
  }
  if(ID == 'd')
  {
    val = Serial.parseInt();
    analogWrite(11, val);
    Serial.read();
    Serial.print("MOTOR: D is ");
    Serial.println(val);
  }
}
}
