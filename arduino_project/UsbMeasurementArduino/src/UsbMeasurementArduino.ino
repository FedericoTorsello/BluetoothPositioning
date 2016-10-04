#include <NewPing.h>

const unsigned int TRIGGER_PIN = 8;  // Arduino pin tied to trigger pin on the ultrasonic sensor.
const unsigned int ECHO_PIN  = 7;  // Arduino pin tied to echo pin on the ultrasonic sensor.
const unsigned int MAX_DISTANCE  = 500; // Maximum distance we want to ping for (in centimeters).
// Maximum sensor distance is rated at 400-500cm.

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.

void setup() {
  Serial.begin(115200); // Open serial monitor at 115200 baud to see ping results.
}

void loop() {

  unsigned int ping_median = sonar.ping_median();

  unsigned int in_cm = sonar.convert_cm(ping_median);

  // Send ping, get distance in cm and print result (0 = outside set distance range)
  Serial.println(in_cm);
}
