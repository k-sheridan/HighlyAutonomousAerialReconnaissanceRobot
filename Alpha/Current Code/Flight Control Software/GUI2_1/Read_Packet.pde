void readPacket() {
  byte[] type = currentClient.readBytesUntil(10);
  
  if (type != null) {
    switch((char)type[0]) {
    case 'a':
      String message = currentClient.readString();
      println(message);
      if(message != null){
      String stringVals[] = split(message, ",");
      if(message.length() > 45 && stringVals.length > 10){
      pitch = Float.parseFloat(stringVals[0]);
      roll = Float.parseFloat(stringVals[1]);
      yaw = Float.parseFloat(stringVals[2]);
      rateX = Float.parseFloat(stringVals[3]);
      rateY = Float.parseFloat(stringVals[4]);
      SQUAL = Integer.parseInt(stringVals[5]);
      relalt = Float.parseFloat(stringVals[6]);
      m1_speed = Float.parseFloat(stringVals[7]);
      m2_speed = Float.parseFloat(stringVals[8]);
      m3_speed = Float.parseFloat(stringVals[9]);
      m4_speed = Float.parseFloat(stringVals[10]);
//      slamX = Float.parseFloat(stringVals[7]);
//      slamY = Float.parseFloat(stringVals[8]);
//      slamW = Float.parseFloat(stringVals[9]);
      }
      else{
       println("message too small"); 
      }
      }
      packetReceived = true;
      //println(message);
      break;

    default:
      println("unrecognized type");
      currentClient.clear();
      packetReceived = true;
    }
  }
}

