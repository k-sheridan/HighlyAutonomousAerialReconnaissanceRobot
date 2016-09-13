void writePacket() {
  String dataOut = ""; // initialize array
  //convert and load data
  dataOut += str(alt_lock);
  dataOut += ','; 
  dataOut += str(start);
  
  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  //send data
  //println(dataOut);
  currentClient.write(dataOut);  
}

