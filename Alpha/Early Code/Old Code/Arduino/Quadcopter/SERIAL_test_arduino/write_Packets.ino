void writePacket(char CMD, int var){
  int CHKSUM;
  byte ITEM_1;
  byte ITEM_2;
  int _remainder;
  
  switch(CMD){
    case potWrite:
    ITEM_1 = var >> 8;
    ITEM_2 = var & 0xFF;
    
    //check sum------------------------
    
    CHKSUM = CMD + ITEM_1 + ITEM_2;
    _remainder = CHKSUM / 23;
    _remainder = CHKSUM - (_remainder * 23);
    CHKSUM = _remainder;
    
    //-----------------------------------
    Serial.write(CMD);
    Serial.write(ITEM_1);
    Serial.write(ITEM_2);
    Serial.write(CHKSUM);
    Serial.write('\n');    
    break;
    
    default:
    break;
  }
}
