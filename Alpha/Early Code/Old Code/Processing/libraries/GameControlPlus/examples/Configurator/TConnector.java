import processing.core.*;

public class TConnector implements PConstants, TConstants {

  private final PApplet app;
  final TBase owner;
  TConnector conTo = null;

  public final int conNo;
  boolean isOver = false;

  float size, hsize;
  // INPUT or DESC
  final int type;
  // Absolute position on screen
  final float px, py;

  public TConnector(PApplet papp, TBase owner, int conNo, float x, float y, float size) {
    app = papp;
    this.owner = owner;
    this.conNo = conNo;
    type = (owner instanceof TDescriptor) ? DESC : INPUT;
    px = x;
    py = y;
    this.size = size;
    hsize = size/2;
  }

  public void draw(float deltaY) {
    app.pushMatrix();
    //		app.pushStyle();
    app.translate(0, deltaY);
    if (type == INPUT)
      app.scale(-1, 1);
    app.fill(isOver ? HIGHLIGHT : CONNECTOR);
    app.noStroke();
    app.rect(0, 0, hsize, size);
    app.stroke(isOver ? HIGHLIGHT : BORDER);
    app.strokeWeight(isOver ? 3 : 1);
    app.arc(hsize, hsize, size, size, PI+HALF_PI, TWO_PI+HALF_PI, OPEN);
    app.line(0, 0, hsize, 0);
    app.line(0, 0, 0, size);
    app.line(0, size, hsize, size);
    //		app.popStyle();
    app.popMatrix();
  }

  public boolean isOver(TConfigUI ccw, float mx, float my) {
    isOver = (Math.abs(mx-px) <= hsize && Math.abs(my-py) <= hsize);
    if (isOver) 
      ccw.current = this;
    return isOver;
  }
}

