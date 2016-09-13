float get4ByteFloat(byte[] data, int offset) { 
  String hexint=hex(data[offset+3])+hex(data[offset+2])+hex(data[offset+1])+hex(data[offset]); 
  return Float.intBitsToFloat(unhex(hexint));
}

void float2bytes(float myFloat) {
  int bits = Float.floatToIntBits(myFloat);
  FB[0] = (byte)(bits & 0xff);
  FB[1] = (byte)((bits >> 8) & 0xff);
  FB[2] = (byte)((bits >> 16) & 0xff);
  FB[3] = (byte)((bits >> 24) & 0xff);
}
