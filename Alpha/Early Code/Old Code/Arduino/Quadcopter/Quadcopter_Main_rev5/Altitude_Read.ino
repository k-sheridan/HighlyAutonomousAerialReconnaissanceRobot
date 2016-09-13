float BMP_read() {
  sensors_event_t event;
  bmp.getEvent(&event);
  
  float temperature;
  bmp.getTemperature(&temperature);
  
  // if you look up this areas SLP you can get the true altitude! :)
  float seaLevelPressure = 1012.9;
  
  return bmp.pressureToAltitude(seaLevelPressure, event.pressure, temperature);
}
