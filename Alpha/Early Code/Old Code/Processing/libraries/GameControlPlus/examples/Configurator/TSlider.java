import g4p_controls.*;

import org.gamecontrolplus.*;

import processing.core.PApplet;

public class TSlider extends TBaseInput {

  ControlSlider slider;
  GTextField txfMultiplier;
  GTextField txfTolerance;

  float tolerance = 0, value = 0, multiplier = 1;

  public TSlider(TConfigUI ccw, ControlSlider pbutton, float x, float y) {
    super(ccw, x, y, 1, 4);
    uiType = UI_SLIDER;
    inputTypeName = "SLIDER";
    slider = pbutton;
    float fieldHeight = ccw.input_UI_height * ccw.scale;
    txfMultiplier = new GTextField(app, x + 100, y + ccw.input_UI_height, 80, fieldHeight, G4P.SCROLLBARS_NONE);
    txfMultiplier.setFont(ccw.font);
    txfMultiplier.setText("1");
    txfMultiplier.addEventHandler(this, "multiplier_set");
    ccw.tabManager.addControl(txfMultiplier);
    txfTolerance = new GTextField(app, x + 100, y + 2 * ccw.input_UI_height, 80, fieldHeight, G4P.SCROLLBARS_NONE);
    txfTolerance.setFont(ccw.font);
    txfTolerance.setText("0");
    txfTolerance.addEventHandler(this, "tolerance_set");
    ccw.tabManager.addControl(txfTolerance);
    name = slider.getName();
    namePos = 20 + (int)(ccw.input_UI_length - 20 - app.textWidth(name)) / 2;
  }

  public float getMultiplier() {
    return multiplier;
  }

  public float getTolerance() {
    return tolerance;
  }


  public void tolerance_set(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      float previousValue = tolerance;
      try {
        tolerance = Float.parseFloat(source.getText());
        if (tolerance < 0 || tolerance >= 1.0f)
          tolerance = previousValue;
        slider.setTolerance(tolerance);
      } 
      catch (NumberFormatException e) {
        tolerance = previousValue;
      }
      source.setText("" + tolerance);
    }
  }

  public void multiplier_set(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      float previousValue = multiplier;
      try {
        multiplier = Float.parseFloat(source.getText());
        slider.setMultiplier(multiplier);
      } 
      catch (NumberFormatException e) {
        multiplier = previousValue;
      }
      source.setText("" + multiplier);
    }
  }

  @Override
    public void update() {
    value = slider.getValue();
    multiplier = slider.getMultiplier();
    tolerance = slider.getTolerance();
  }

  @Override
    public void draw() {
    app.pushMatrix();
    app.pushStyle();
    app.translate(px, py);

    drawBackground();
    app.text("Multiplier", 10, ccw.input_UI_height + ccw.fontSize);
    app.text("Tolerance", 10, 2 * ccw.input_UI_height + ccw.fontSize);
    // Slider variables
    float sLeft = 10, sRight = ccw.input_UI_length - 10;
    float sTop = 1 + 3 * ccw.input_UI_height;
    float sWidth = sRight - sLeft, sHeight = ccw.input_UI_height - 8;
    // Draw slider track
    app.noStroke();
    app.fill(255, 255, 200);
    app.rect(sLeft, sTop, sWidth, sHeight);
    // Draw tolerance
    if (tolerance > 0) {
      float tolWidth = tolerance * (sRight - sLeft);
      app.fill(255, 200, 200);
      app.rect((sRight + sLeft - tolWidth) / 2, sTop, tolWidth, sHeight);
    }
    // Draw slider value
    String vText = "" + value;
    float vWidth = app.textWidth(vText);
    app.fill(0, 0, 0, 120);
    app.text(vText, (sRight + sLeft - vWidth) / 2, sTop + ccw.fontSize);
    // Draw slider cursor
    float valueX = PApplet.map(value, -multiplier, multiplier, sLeft, sRight);
    app.stroke(SLIDER_CURSOR);
    app.strokeWeight(1);
    app.line(valueX, sTop, valueX, sTop + sHeight);
    app.fill(SLIDER_CURSOR);
    app.noStroke();
    // Top triangle
    app.beginShape();
    app.vertex(valueX, sTop + 3);
    app.vertex(valueX-3, sTop - 3);
    app.vertex(valueX+3, sTop - 3);
    app.endShape();	
    // Bottom triangle
    app.beginShape();
    app.vertex(valueX, sTop+sHeight - 3);
    app.vertex(valueX-3, sTop+sHeight + 3);
    app.vertex(valueX+3, sTop+sHeight + 3);
    app.endShape();	

    drawConnectors();
    drawControlHighlight();

    app.popStyle();
    app.popMatrix();
  }
}

