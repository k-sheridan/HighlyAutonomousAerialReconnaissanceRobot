#include <LiquidCrystal.h>


//SEND PACKET----------------------------------------
enum SEND_COMMANDS {
  potWrite = 'a'
};

//RECIEVE PACKET--------------------------------------------
enum RECEIVE_COMMANDS {       //a,b,c commands in that order(use these characters!)
  lcd_write = 'a',
  int_print,
  int_print_long
};

enum REC_PACKET_DETAILS {
  CMD = 0,
  ITEM_1,
  ITEM_2,
  CHKSUM,
  LINE_END
};
byte packetLoss = 0;
int checkSum;
int const BUFFER_LIMIT = 5;

struct {
  byte data[BUFFER_LIMIT];
} dataPacket;

boolean correctPacket = false;
boolean readComplete = false;
//---------------------------------------------

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

int potVal = 0;

void setup() {
  Serial.begin(9600);
  lcd.begin(16, 2);
  lcd.print("INIT");
}

void loop() {
  potVal++;
  if (Serial.available() >= 5 && Serial.read() == '\n') {
    readPacket();
    writePacket(potWrite, potVal);
  }
}

