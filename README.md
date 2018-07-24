# Inertial-Measurement-Unit-Integration-With-Processing

The program is able to read raw data from the MPU6050 IMU sensor, using an Arduino Uno to calculate x,y,z (roll,pitch yaw) components of the orientation of the sensor. The calculation occurs in the Arduino IDE and is sent to Processing (via the serial) to present a visual representation of the sensor to the user.

## Getting Started

Connect the IMU sensor to power (5V and Ground), and connect the serial clock and serial data ports (SCL and SDA) to input pins A5 and A4 on the UNO, respectively. 

### Prerequisites

None Needed

## Deployment
Arduino IDE:
Compile the Arduino IDE code to the Arduino UNO. To test the operation of this module, open the serial monitor and observe the values (x,y,z) for the "accelerometer", "gyroscope" and "complementary filter". If the values shown are not "NAN", tilt the sensor approximately 90 degrees in any direction and observe the change.

Processing:
The processing code can either be run by compiling the code and pressing run, or opening the exe file located in the application folders e.g. "application.windows32"

## Authors
Alvin Useree

## Acknowledgements 
The raw data is read using open sourced code provided by arduino.cc user "Krodal"


