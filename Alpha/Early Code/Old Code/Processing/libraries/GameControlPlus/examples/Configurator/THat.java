import g4p_controls.*;

import org.gamecontrolplus.*;



public class THat extends TBaseInput {

  ControlHat button;
  GTextField txfMultiplier;

  float x, y, value, multiplier = 1;

  final boolean[] indicators = new boolean[4];
  final String[] itext = { 
    "left", "right", "up", "down"
  };


  public THat(TConfigUI ccw, ControlHat pbutton, float x, float y) {
    super(ccw, x, y, 1, 5);
    uiType = UI_COOLIEHAT;
    inputTypeName = "HAT";
    button = pbutton;
    float fieldHeight = ccw.input_UI_height * ccw.scale;
    txfMultiplier = new GTextField(app, x + 130, y + 2 + 2 * ccw.input_UI_height, 80, fieldHeight, G4P.SCROLLBARS_NONE);
    txfMultiplier.setFont(ccw.font);
    txfMultiplier.setText("1");
    txfMultiplier.addEventHandler(this, "multiplier_set");
    ccw.tabManager.addControl(txfMultiplier);
    name = button.getName();
    namePos = 20 + (int)(ccw.input_UI_length - 20 - app.textWidth(name)) / 2;
  }

  public void multiplier_set(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      float m = multiplier;
      try {
        multiplier = Float.parseFloat(source.getText());
        button.setMultiplier(multiplier);
      } 
      catch (NumberFormatException e) {
        multiplier = m;
      }
      source.setText("" + multiplier);
    }
  }
  public float getMultiplier() {
    return multiplier;
  }

  @Override
    public void update() {
    value = button.getValue();
    x = button.getX();
    y = button.getY();
    indicators[0] = button.left();
    indicators[1] = button.right();
    indicators[2] = button.up();
    indicators[3] = button.down();
    multiplier = button.getMultiplier();
  }

  public void draw() {
    app.pushMatrix();
    //		app.pushStyle();
    app.translate(px, py);

    drawBackground();
    // Direction indicators
    app.noStroke();
    app.fill(INDICATOR);
    app.rect(2, ccw.input_UI_height, 60, ccw.input_UI_height * 4 - 3);
    app.stroke(BORDER);
    app.strokeWeight(1);
    for (int i = 0; i < 4; i++) {
      float iy = (i + 1) * ccw.input_UI_height;
      app.fill(indicators[i] ? PRESSED : RELEASED);
      app.ellipse(ccw.input_UI_height / 2, iy + ccw.input_UI_height / 2, ccw.indicator_d, ccw.indicator_d);
      app.fill(TEXTFILL);
      app.text(itext[i], 24, iy + fontBaseLine);
      switch(i) {
      case 0:
        app.text("Hat Value:   " + value, 72, iy + fontBaseLine);
        break;
      case 1:
        app.text("Multiplier:", 72, iy + fontBaseLine);
        break;
      case 2:
        app.text("X:   " + x, 72, iy + fontBaseLine);
        break;
      case 3:
        app.text("Y:   " + y, 72, iy + fontBaseLine);
        break;
      }
    }
    // Pressed ?
    app.stroke(BORDER);
    app.strokeWeight(1);
    app.fill(value != 0 ? PRESSED : RELEASED);
    app.ellipse(ccw.input_UI_height/2, ccw.input_UI_height/2, ccw.indicator_d, ccw.indicator_d);

    drawConnectors();
    drawControlHighlight();
    //		app.popStyle();
    app.popMatrix();
  }
}

