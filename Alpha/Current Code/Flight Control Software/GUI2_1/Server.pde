void serverPoll() {
  currentClient = gs_server.available(); // get the next client
  if(currentClient != null){
    // read packet and convert data to variables;
    readPacket();
    // write pertinent data to the current client
    if(packetReceived == true){
    writePacket(); 
    packetReceived = false;
    }
  }
}

void disconnectEvent(Client theClient){
  connected = false;
  println(theClient.ip() + " disconnect message: " + (char)theClient.read() );
}

void serverEvent(Server theServer, Client theClient){
  println(theClient.ip() + " connected");
}


