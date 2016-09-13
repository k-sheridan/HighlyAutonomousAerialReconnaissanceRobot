void readPacket() {

  for (int i = 0; i < BUFFER_LIMIT; i++) {
    dataPacket.data[i] = Serial.read();
  }

  // now it's time to compute the byte array

  switch (dataPacket.data[CMD]) {
    case lcd_write:
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print(dataPacket.data[ITEM_1]);
      break;

    case int_print:
      //Serial.println(dataPacket.data[ITEM_1]);
      break;

    case int_print_long:
      int _longInt;
      int x;
      int remainder;
      _longInt |= dataPacket.data[ITEM_1];
      _longInt = _longInt << 8; // moves the high byte over to the left
      _longInt |= dataPacket.data[ITEM_2];
      checkSum = dataPacket.data[CHKSUM];

      // CHECK SUM
      x = dataPacket.data[CMD] + dataPacket.data[ITEM_1] + dataPacket.data[ITEM_2];
      remainder = x / 23; // divisor
      remainder = x - (remainder * 23);
      // ---------------------------

      if (checkSum == remainder) {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Value:");
        lcd.setCursor(8, 0);
        lcd.print(_longInt);
        lcd.setCursor(0, 1);
        lcd.print("Match Sum:");
        lcd.setCursor(12, 1);
        lcd.print(Serial.available());
      }
      else if(checkSum != remainder){
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("PACKET LOSS");
        lcd.setCursor(0, 1);
        lcd.print(remainder);
        lcd.setCursor(4, 1);
        lcd.print(checkSum);
        lcd.setCursor(10, 1);
        lcd.print(_longInt);
        packetLoss = 1;
      }
      break;

    default:
      correctPacket = false;
      packetLoss = 1;
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("oops!");
      break;
  }
}












