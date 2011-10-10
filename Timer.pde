class Timer {
  int savedTime;
  int totalTime;

  Timer(int tempTotalTime) {
    totalTime = tempTotalTime;
  }

  void start() {
    savedTime = millis();
  }

  boolean isFinished() { 
    int passedTime = millis()- savedTime;
    
    println("clock:"+int(passedTime*.01));

    if (passedTime > totalTime) {
      return true;
    } 
    else {
      return false;
    }
  }
}

