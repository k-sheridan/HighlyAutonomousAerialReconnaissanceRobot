#include <LiquidCrystal.h>

LiquidCrystal lcd(12,11,5,4,3,2);

int inData[10];  // Allocate some space for the Bytes
byte inByte;   // Where to store the Bytes read
byte index = 0;   // Index into array; where to store the Bytes
int ledPin = 13;  // Set the pin to digital I/O

int _read;

void setup(){
  lcd.begin(16,2);
  Serial.begin(9600);
}

void loop()
{
  delay(250);
  lcd.print("About to read");
  _read = Serial.read();
  delay(1000);
  lcd.clear();
  lcd.print("Read value= ");
  lcd.print(_read);
  delay(1000);
  lcd.clear();
  Serial.print(49);
} 
