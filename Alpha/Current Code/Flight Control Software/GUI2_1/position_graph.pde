class posGraph {
  //vars
  int x, y, dimX, dimY;
  int odomX, odomY;
  PImage grid, grid_cropped;
  //functions
  posGraph (int _x, int _y, int _dimX, int _dimY) {
    x = _x;
    y = _y;
    dimX = _dimX;
    dimY = _dimY;
  }

  void updateValues(float _odomX, float _odomY) {
    //setup data
    grid = loadImage("grid.png");
    odomX = int(_odomX * 100);
    odomY = int(_odomY * 100);
    //draw graphics
    fill(0);
    strokeWeight(0);
    //--------------
    textAlign(LEFT);
    text("SLAM X:", x, y - 5);
    text("SLAM Y:", x + 100, y - 5);
    //-------------
    rect(x, y, dimX, dimY);
    stroke(255);
    //crop image
    int relX = round(map((-1 * odomX) - 30, -1325, 1325, 0, 1920));
    int relY = round(map((odomY) - 45, -750, 750, 0, 1080));
    grid_cropped = grid.get(relX - (dimX / 2), relY - (dimY /2), dimX, dimY);
    image(grid_cropped, x, y, dimX, dimY);
    
    fill(255, 0, 0);
    ellipse(x + (dimX / 2), y + (dimY / 2), 20, 20);
  }
}

