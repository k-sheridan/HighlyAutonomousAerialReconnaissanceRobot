//ACCELEROMETER----------------------------------------------------------------------

void ADLX_writeTo(byte address, byte val) {

  Wire.beginTransmission(ADLX345);
  Wire.write(address);
  Wire.write(val);
  Wire.endTransmission();

}

void ADLX_readFrom(byte address, int num, byte _buff[]) {

  Wire.beginTransmission(ADLX345);
  Wire.write(address);
  Wire.endTransmission();

  Wire.beginTransmission(ADLX345);
  Wire.requestFrom(ADLX345, num);

  int i = 0;
  while (Wire.available()) {
    _buff[i] = Wire.read();
    i++;
  }
  Wire.endTransmission();
}

void readAccel() {
  uint8_t const howManyBytesToRead = 6;
  ADLX_readFrom(Accel_data_x0, howManyBytesToRead, _buff);

  accel_x = (((int)_buff[1]) << 8) | _buff[0];
  accel_y = (((int)_buff[3]) << 8) | _buff[2];
  accel_z = (((int)_buff[5]) << 8) | _buff[4];

}

//GYROSCOPE--------------------------------------------------------------------

void GyroCalibrate() {

  int tmpx = 0;
  int tmpy = 0;
  int tmpz = 0;

  g_offset_x = 0;
  g_offset_y = 0;
  g_offset_z = 0;

  for (char i = 0; i < 10; i++)
  {
    delay(10);
    readGyro();
    tmpx += gyro_x;
    tmpy += gyro_y;
    tmpz += gyro_z;
  }
  g_offset_x = tmpx / 10;
  g_offset_y = tmpy / 10;
  g_offset_z = tmpz / 10;
}


void readGyro() {
  Wire.beginTransmission(ITG3205);
  Wire.write(0x1B);
  Wire.endTransmission();

  Wire.beginTransmission(ITG3205);
  Wire.requestFrom(ITG3205, 8);

  int i = 0;
  byte buff[8];
  while (Wire.available())
  {
    buff[i] = Wire.read();
    i++;
  }
  Wire.endTransmission();
  gyro_x = ((buff[4] << 8) | buff[5]) - g_offset_x;
  gyro_y = ((buff[2] << 8) | buff[3]) - g_offset_y;
  gyro_z = (((buff[6] << 8) | buff[7])* -1) - g_offset_z; // switch the direction
  gyro_temp = (buff[0] << 8) | buff[1];
}

void initGyro() {
  Wire.beginTransmission(ITG3205);
  Wire.write(0x3E);
  Wire.write(0x00);
  Wire.endTransmission();

  Wire.beginTransmission(ITG3205);
  Wire.write(0x15);
  Wire.write(0x07);
  Wire.endTransmission();

  Wire.beginTransmission(ITG3205);
  Wire.write(0x16);
  Wire.write(0x1E);
  Wire.endTransmission();

  Wire.beginTransmission(ITG3205);
  Wire.write(0x17);
  Wire.write(0x00);
  Wire.endTransmission();

}

//MAGNETOMETER-----------------------------------------------------------------------------------------

void MagnetInit() {
  Wire.beginTransmission(HMC);
  Wire.write(0x02);
  Wire.write(0x00);
  Wire.endTransmission();
}

void readMagnet() {
  Wire.beginTransmission(HMC);
  Wire.write(0x03); //select register 3, X MSB register
  Wire.endTransmission();

  Wire.requestFrom(HMC, 6);
  if (6 <= Wire.available()) {
    mag_x = Wire.read() << 8; //X msb
    mag_x |= Wire.read(); //X lsb
    mag_z = Wire.read() << 8; //Z msb
    mag_z |= Wire.read(); //Z lsb
    mag_y = Wire.read() << 8; //Y msb
    mag_y |= Wire.read(); //Y lsb
  }
}
