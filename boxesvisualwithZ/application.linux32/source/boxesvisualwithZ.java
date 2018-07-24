import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class boxesvisualwithZ extends PApplet {

/**
 * Show GY521 Data.
 * 
 * Reads the serial port to get x- and y- axis rotational data from an accelerometer,
 * a gyroscope, and comeplementary-filtered combination of the two, and displays the
 * orientation data as it applies to three different colored rectangles.
 * It gives the z-orientation data as given by the gyroscope, but since the accelerometer
 * can't provide z-orientation, we don't use this data.
 * 
 */
 


PImage img;

Serial  myPort;
short   portIndex = 1;
int     lf = 10;       //ASCII linefeed
String  inString;      //String for testing serial communication
int     calibrating;
 
float   dt;
float   x_gyr;        //Gyroscope data
float   y_gyr;
float   z_gyr;
float   x_acc;        //Accelerometer data
float   y_acc;
float   z_acc;
float   x_fil;        //Filtered data
float   y_fil;
float   z_fil;
float   calib_x = 0;  //calibration data
float   calib_y = 0;
float   calib_z = 0; 
float   displayed_x = 0;  //data to be displayed to the user
float   displayed_y = 0;
float   displayed_z = 0;
 
public void setup()  { 
//  size(640, 360, P3D); 
  size(1400, 800, P3D);
  noStroke();
  colorMode(RGB, 256); 
 
//  println("in setup");
  String portName = "COM5";
//  println(Serial.list());
//  println(" Connecting to -> " + Serial.list()[portIndex]);
  myPort = new Serial(this, portName, 19200);
  myPort.clear();
  myPort.bufferUntil(lf);
  img=loadImage("Medivr.jpg");
 

  
} 

public void draw_rect_rainbow() {
  scale(90);
  beginShape(QUADS);

  fill(0, 1, 1); vertex(-1,  1.5f,  0.25f);
  fill(1, 1, 1); vertex( 1,  1.5f,  0.25f);
  fill(1, 0, 1); vertex( 1, -1.5f,  0.25f);
  fill(0, 0, 1); vertex(-1, -1.5f,  0.25f);

  fill(1, 1, 1); vertex( 1,  1.5f,  0.25f);
  fill(1, 1, 0); vertex( 1,  1.5f, -0.25f);
  fill(1, 0, 0); vertex( 1, -1.5f, -0.25f);
  fill(1, 0, 1); vertex( 1, -1.5f,  0.25f);

  fill(1, 1, 0); vertex( 1,  1.5f, -0.25f);
  fill(0, 1, 0); vertex(-1,  1.5f, -0.25f);
  fill(0, 0, 0); vertex(-1, -1.5f, -0.25f);
  fill(1, 0, 0); vertex( 1, -1.5f, -0.25f);

  fill(0, 1, 0); vertex(-1,  1.5f, -0.25f);
  fill(0, 1, 1); vertex(-1,  1.5f,  0.25f);
  fill(0, 0, 1); vertex(-1, -1.5f,  0.25f);
  fill(0, 0, 0); vertex(-1, -1.5f, -0.25f);

  fill(0, 1, 0); vertex(-1,  1.5f, -0.25f);
  fill(1, 1, 0); vertex( 1,  1.5f, -0.25f);
  fill(1, 1, 1); vertex( 1,  1.5f,  0.25f);
  fill(0, 1, 1); vertex(-1,  1.5f,  0.25f);

  fill(0, 0, 0); vertex(-1, -1.5f, -0.25f);
  fill(1, 0, 0); vertex( 1, -1.5f, -0.25f);
  fill(1, 0, 1); vertex( 1, -1.5f,  0.25f);
  fill(0, 0, 1); vertex(-1, -1.5f,  0.25f);

  endShape();
  
  
}

public void draw_rect(int r, int g, int b) {
  scale(90);
  beginShape(QUADS);
  
  fill(r, g, b);
  vertex(-1,  1.5f,  0.25f);
  vertex( 1,  1.5f,  0.25f);
  vertex( 1, -1.5f,  0.25f);
  vertex(-1, -1.5f,  0.25f);

  vertex( 1,  1.5f,  0.25f);
  vertex( 1,  1.5f, -0.25f);
  vertex( 1, -1.5f, -0.25f);
  vertex( 1, -1.5f,  0.25f);

  vertex( 1,  1.5f, -0.25f);
  vertex(-1,  1.5f, -0.25f);
  vertex(-1, -1.5f, -0.25f);
  vertex( 1, -1.5f, -0.25f);

  vertex(-1,  1.5f, -0.25f);
  vertex(-1,  1.5f,  0.25f);
  vertex(-1, -1.5f,  0.25f);
  vertex(-1, -1.5f, -0.25f);

  vertex(-1,  1.5f, -0.25f);
  vertex( 1,  1.5f, -0.25f);
  vertex( 1,  1.5f,  0.25f);
  vertex(-1,  1.5f,  0.25f);

  vertex(-1, -1.5f, -0.25f);
  vertex( 1, -1.5f, -0.25f);
  vertex( 1, -1.5f,  0.25f);
  vertex(-1, -1.5f,  0.25f);

  endShape();
  
  
}

public void draw()  { 
  
  background(0);
  lights();
        
  // Tweak the view of the rectangles
  int distance = 50;
  int x_rotation = 90;
  float time = millis();  //checks to see how much time has elapsed
  
  
  
  if(time<8000)              //calibrates IMU unit
  {
  
  calib_x = x_fil;  
  calib_y = -y_fil;
  calib_z = -z_fil; 
  
  }
  
  //Show gyro data
  pushMatrix(); 
  translate(width/6, height/2, -50); 
  rotateX(radians(-x_gyr - x_rotation));
  rotateY(radians(-y_gyr));
  rotateZ(radians(z_gyr));
  draw_rect(249, 250, 50);
  
  popMatrix(); 

  //Show accel data
  pushMatrix();
  translate(5*width/6, height/2, -50);  
  rotateX(radians(-x_acc - x_rotation));
  rotateY(radians(-y_acc));
  rotateZ(radians(z_acc));
  draw_rect(93, 175, 83);  
  popMatrix();
  
  //Show combined data
  pushMatrix();
  translate(width/2, height/2, -50);
  rotateX(radians(((-x_fil - x_rotation)-calib_x)));
  rotateY(radians(-y_fil - calib_y));
  rotateZ(radians(z_fil - calib_z));
  draw_rect(56, 140, 206);
  popMatrix();
  
  displayed_x =  x_fil - calib_x;        //instead of raw filtered values being displyed
  displayed_y = -y_fil - calib_y;      //values after calibration are displayed
  displayed_z =  z_fil - calib_z;
  
  textSize(24);
  String accStr = "(" + (int) y_acc + ", " + (int) x_acc + "," + (int) z_acc + ")";
  String gyrStr = "(" + (int) y_gyr + ", " + (int) x_gyr + "," + (int) z_gyr + ")";
  String filStr = "(" + (int) displayed_y + ", " + (int) displayed_x + "," + (int) displayed_z + ")";
 
  //Gyro values
  fill(249, 250, 50);
  text("Gyroscope", (int) width/6.0f - 60, 55);
  
  text("Roll:", (int) width/6.0f - 60, 90);
  text(y_gyr, (int) (width/6.0f) +10, 90); 
  
  text("Pitch:", (int) width/6.0f - 60, 115);
  text(x_gyr, (int) (width/6.0f) +10, 115); 
  
  text("Yaw:", (int) width/6.0f - 60, 140);
  text(z_gyr, (int) (width/6.0f) +10, 140); 
  
  text(gyrStr, (int) (width/6.0f) - 40, 630);

  //complemetary filter values
  fill(56, 140, 206);
  text("Complementary Filter", (int) width/2.0f - 100, 55);
  
  text("Roll:", (int) width/2.0f - 100, 90);
  text(displayed_y, (int) (width/2.0f) - 30, 90); 
  
  text("Pitch:", (int) width/2.0f - 100, 115);
  text(displayed_x, (int) (width/2.0f) - 30, 115); 
  
  text("Yaw:", (int) width/2.0f - 100, 140);
  text(displayed_z, (int) (width/2.0f) - 30, 140);
 
  text(filStr, (int) (width/2.0f) - 40, 630); 
  
  //Accelerometer values  
  fill(83, 175, 93);
  text("Accelerometer", (int) (5.0f*width/6.0f) - 40, 55);
  
  text("Roll:", (int) (5.0f*width/6.0f) - 40, 90);
  text(y_acc, (int) (5.0f*width/6.0f)+30, 90); 
  
  text("Pitch:", (int) (5.0f*width/6.0f) - 40, 115);
  text(x_acc, (int) (5.0f*width/6.0f)+30, 115); 
  
  text("Yaw:", (int) (5.0f*width/6.0f) - 40, 140);
  text(z_acc, (int) (5.0f*width/6.0f)+30, 140);
    
  text(accStr, (int) (5.0f*width/6.0f) - 20, 630);
  image(img,565,670);
 
} 

public void serialEvent(Serial p) {

  inString = (myPort.readString());
  
  try {
    // Parse the data
    String[] dataStrings = split(inString, '#');
    for (int i = 0; i < dataStrings.length; i++) {
      String type = dataStrings[i].substring(0, 4);
      String dataval = dataStrings[i].substring(4);
    if (type.equals("DEL:")) {
        dt = PApplet.parseFloat(dataval);
        /*
        print("Dt:");
        println(dt);
        */
        
      } else if (type.equals("ACC:")) {
        String data[] = split(dataval, ',');
        x_acc = PApplet.parseFloat(data[0]);
        y_acc = PApplet.parseFloat(data[1]);
        z_acc = PApplet.parseFloat(data[2]);
        /*
        print("Acc:");
        print(x_acc);
        print(",");
        print(y_acc);
        print(",");
        println(z_acc);
        */
      } else if (type.equals("GYR:")) {
        String data[] = split(dataval, ',');
        x_gyr = PApplet.parseFloat(data[0]);
        y_gyr = PApplet.parseFloat(data[1]);
        z_gyr = PApplet.parseFloat(data[2]);
      } else if (type.equals("FIL:")) {
        String data[] = split(dataval, ',');
        x_fil = PApplet.parseFloat(data[0]);
        y_fil = PApplet.parseFloat(data[1]);
        z_fil = PApplet.parseFloat(data[2]);
      }
    }
  } catch (Exception e) {
      println("Caught Exception");
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "boxesvisualwithZ" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
