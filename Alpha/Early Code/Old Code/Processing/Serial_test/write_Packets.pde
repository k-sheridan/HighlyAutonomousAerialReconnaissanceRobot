void writePacket(char CMD, int VAR) {
  int _remainder;
  //BYTES TO FIX
  int fix1;
  int fix2;
  int fix3;
  byte ITEM_1;
  byte ITEM_2;
  int CHKSUM;

  switch(CMD) {
  case lcd_print:
    break;

  case int_print:
    break;

  case int_print_long:
    ITEM_1 = (byte)(VAR >> 8);
    ITEM_2 = (byte)(VAR & 0xFF);
    //FIX BYTES------------------------
    fix1 = CMD;
    fix2 = ITEM_1;
    fix3 = ITEM_2;
    if (fix1 < 0) {
      fix1 = 256 + fix1;
    }
    if (fix2 < 0) {
      fix2 = 256 + fix2;
    }
    if (fix3 < 0) {
      fix3 = 256 + fix3;
    }
    //CHECK SUM----------------------------
    CHKSUM = fix1 + fix2 + fix3;
    _remainder = CHKSUM / 23;
    _remainder = CHKSUM - (_remainder * 23);
    CHKSUM = _remainder;
    port.write(CMD);
    port.write(ITEM_1);
    port.write(ITEM_2);
    port.write(CHKSUM);
    port.write('\n');  
    break;

  default:
    println("CASE NULL");
    break;
  }
}










