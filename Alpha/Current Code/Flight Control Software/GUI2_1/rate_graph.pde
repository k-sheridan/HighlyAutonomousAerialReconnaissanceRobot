class rateGraph{
    float rateX;
    float rateY;
    
    private int x;
    private int y;
    private int dimX;
    private int dimY;
    
    rateGraph(int _x, int _y, int _dimX, int _dimY){
      x = _x;
      y = _y;
      dimX = _dimX;
      dimY = _dimY;
    }
    
    boolean updateValues(float _rateX, float _rateY, float _tRateX, float _tRateY, int _SQUAL){
      rateX = _rateX;
      rateY = _rateY;
      
      fill(0);
      rect(x, y, dimX, dimY);
      textAlign(RIGHT);
      text("RATE X:", x + 40, y - 10);
      text("RATE Y:", x + 140, y - 10);
      text("SQUAL:", x + 40, y - 20);
      textAlign(LEFT);
      text(_SQUAL, x + 40, y - 20);
      text(rateX, x + 40, y - 10);
      text(rateY, x + 140, y - 10);
      textSize(14);
      
      
      strokeWeight(2);
      line(x, y + (dimY / 2), x + dimX, y + (dimY / 2));
      line(x + (dimX / 2), y, x + (dimX / 2), y + dimY);
      strokeWeight(0);
      
      fill(255, 0, 0);
      ellipse(x + (dimX / 2 + (-1 * _tRateX * (dimX / 4))),y + (dimY / 2 + (-1 * _tRateY * (dimY / 4))), 20, 20);
      
      fill(0, 0, 255);
      ellipse(x + (dimX / 2 + (-1 * rateX * (dimX / 4))),y + (dimY / 2 + (-1 * rateY * (dimY / 4))), 10, 10);
      
      return true;
    }
}
