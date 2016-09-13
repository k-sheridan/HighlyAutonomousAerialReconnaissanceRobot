#include <Adafruit_Sensor.h>
#include <Adafruit_BMP085_U.h>
#include <Wire.h>

Adafruit_BMP085_Unified bmp = Adafruit_BMP085_Unified(10085);

float true_altitude;
unsigned long lastMicro;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  //sensor_t sensor;
  //bmp.getSensor(&sensor);
  bmp.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  lastMicro = micros();
  true_altitude = BMP_read();
  Serial.print("alt: ");
  Serial.println(true_altitude);
  Serial.print("Microseconds: ");
  Serial.println(micros() - lastMicro);
}



float BMP_read() {
  sensors_event_t event;
  bmp.getEvent(&event);

  if (event.pressure) {
    float temperature;
    bmp.getTemperature(&temperature);

    // if you look up this areas SLP you can get the true altitude! :)
    float seaLevelPressure = 1013.25;

    return bmp.pressureToAltitude(seaLevelPressure, event.pressure, temperature);
  }
}
