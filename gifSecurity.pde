/*------------------------------- Animated Gif Security System 0.1
 Matt Ganucheau - mganucheau@gmail.com
 
 gifAnimation library - http://www.extrapixel.ch/processing/gifAnimation/
 Timer - from Daniel Shiffman's Learning Processing (ex 10-5)
 ------------------------------------------------------------*/
import gifAnimation.*;
import processing.video.*;

Capture  cam;
GifMaker gifExport;
Gif      playingGif;
Timer    timer;
PImage   prevFrame;

float threshold = 70;    // sensitivity of motion detector 0/high - 100/low
int timerAmount = 2000;  // Amount of time to record
int gifDelay    = 100;   // fun to play with
int gifNumber   = 0;     // start the gif count at 0
int recording   = 4;     // recording state

String date() {
  Calendar now = Calendar.getInstance();
  return String.format("Images/%1$tm:%1$td:%1$ty/", now);
}


void setup() {
  size(640, 480);
  frameRate(24);
  cam = new Capture(this, 640, 480);
  timer = new Timer(timerAmount);
  prevFrame = createImage(cam.width, cam.height, RGB);
  
  gifExport = new GifMaker(this, date()+"gif "+gifNumber+".gif", 10);
  gifExport.setRepeat(0);
}

void draw() {
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
    color current = cam.pixels[i];         // get the current color
    color previous = prevFrame.pixels[i];  // get the previous color

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
  int r = int(avgMotion)*4;
  //----------------------------------------------------------

  if (r >= threshold && recording !=1 && recording !=2) {
    timer.start();
    recording = 1;
  } 

  if (recording == 1) {
    gifExport = new GifMaker(this, date()+"gif "+gifNumber+".gif", 10);
    gifExport.setRepeat(0);
    recording = 2;
  }

  if (recording == 2) {
    if (timer.isFinished()) {
      println("gif"+gifNumber+".gif done!");
      playingGif = new Gif(this, date()+"gif "+gifNumber+".gif");
      playingGif.loop();
      gifExport.finish();
      gifNumber = gifNumber + 1;  
      recording = 0;
      delay(500);  // Idea to help the buffer clear out
    } 
    else {
      gifExport.setDelay(100);
      gifExport.addFrame();

      smooth();
      noStroke();
      fill(255, 0, 0);
      ellipse(width-30, 30, 10, 10);
    }
  } 

  if (recording == 0) {
    image(playingGif, 0, 0);
  }
}
