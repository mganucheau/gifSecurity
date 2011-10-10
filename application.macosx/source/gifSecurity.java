import processing.core.*; 
import processing.xml.*; 

import gifAnimation.*; 
import processing.video.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class gifSecurity extends PApplet {

/*------------------------------- Animated Gif Security System
Matt Ganucheau - mganucheau@gmail.com

gifAnimation library - http://www.extrapixel.ch/processing/gifAnimation/
Timer - from Daniel Shiffman's Learning Processing (ex 10-5)
 ------------------------------------------------------------*/



Capture  cam;
GifMaker gifExport;
Gif      playingGif;
Timer    timer;
PImage   prevFrame;

float threshold = 50;  // sensitivity of motion detector 
int gifDelay    = 100; // fun to play with
int gifNumber   = 0;   // start the gif count at 0
int recording   = 4;   // recording state


public void setup() {
  size(640, 480);
  frameRate(24);
  cam = new Capture(this, 640, 480);
  timer = new Timer(2000);
  prevFrame = createImage(cam.width, cam.height, RGB);
  gifExport = new GifMaker(this, "Images/gif-"+gifNumber+".gif", 10);
  gifExport.setRepeat(0);
}

public void draw() {
  if (cam.available() == true) {
    prevFrame.copy(cam, 0, 0, cam.width, cam.height, 0, 0, cam.width, cam.height);
    prevFrame.updatePixels();
    cam.read();
    image(cam, 0, 0);
  }

  //--------------------------------------- Find Adverage Motion 
  loadPixels();
  cam.loadPixels();
  prevFrame.loadPixels();

  float totalMotion = 0;

  // Sum the brightness of each pixel
  for (int i = 0; i < cam.pixels.length; i ++ ) {
    int current = cam.pixels[i];         // get the current color
    int previous = prevFrame.pixels[i];  // get the previous color

    float r1 = red(current);     
    float g1 = green(current);
    float b1 = blue(current);
    float r2 = red(previous); 
    float g2 = green(previous);
    float b2 = blue(previous);

    float diff = dist(r1, g1, b1, r2, g2, b2);  // compare results
    totalMotion += diff;   // sum of all color differences.
  }

  float avgMotion = totalMotion / cam.pixels.length; 
  int r = PApplet.parseInt(avgMotion)*4;
  //----------------------------------------------------------

  if (r >= threshold && recording !=1 && recording !=2) {
    timer.start();
    recording = 1;
  } 

  if (recording == 1) {
    gifExport = new GifMaker(this, "Images/gif-"+gifNumber+".gif", 10);
    gifExport.setRepeat(0);
    recording = 2;
  }

  if (recording == 2) {
    if (timer.isFinished()) {
      println("gif"+gifNumber+".gif done!");
      playingGif = new Gif(this, "Images/gif-"+gifNumber+".gif");
      playingGif.loop();
      gifExport.finish();
      gifNumber = gifNumber + 1;  
      recording = 0;
    } else {
      gifExport.setDelay(100);
      gifExport.addFrame();

      smooth();
      fill(255, 0, 0);
      ellipse(width-30, 30, 20, 20);
    }
  } 

  if (recording == 0) {
    image(playingGif, 0, 0);
  }
}


class Timer {
  int savedTime;
  int totalTime;

  Timer(int tempTotalTime) {
    totalTime = tempTotalTime;
  }

  public void start() {
    savedTime = millis();
  }

  public boolean isFinished() { 
    int passedTime = millis()- savedTime;
    
    println("clock:"+PApplet.parseInt(passedTime*.01f));

    if (passedTime > totalTime) {
      return true;
    } 
    else {
      return false;
    }
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "gifSecurity" });
  }
}
