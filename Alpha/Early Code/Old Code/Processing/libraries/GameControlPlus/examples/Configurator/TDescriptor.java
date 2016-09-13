import g4p_controls.*;

public class TDescriptor extends TBase {

  GTextField txfKey;
  GTextField txfDescription;

  TBaseInput source = null;
  int conID = 0;

  public TDescriptor(TConfigUI ccw, float x, float y) {
    this(ccw, "", "", x, y);
  }

  public TDescriptor(TConfigUI ccw, String key, String desc, float x, float y) {
    super(ccw, x, y, 1);
    for (int cn = 0; cn < connectors.length; cn++)
      connectors[cn]= new TConnector(app, this, cn, 
      px + ccw.desc_UI_length + ccw.connector_size_r, // x
      py + (cn + 0/5f) * ccw.desc_UI_height + ccw.connector_size_r, //y
      ccw.connector_size_d); // size

    uiType = UI_DESCRIPTOR;
    UI_HEIGHT = ccw.desc_UI_height;
    fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;
    final float HGAP = 6;
    float klen = 0.3f * (ccw.desc_UI_length - 4 * HGAP);
    float dlen = 0.7f * (ccw.desc_UI_length - 4 * HGAP);
    float fieldHeight = ccw.desc_UI_height - 2 * ccw.textfield_gap;
    txfKey = new GTextField(app, x + HGAP, y + ccw.textfield_gap, klen, fieldHeight, G4P.SCROLLBARS_NONE);
    txfKey.setFont(ccw.font);
    txfKey.setLocalColorScheme(GCScheme.GOLD_SCHEME);
    txfKey.setDefaultText("Key name");
    txfKey.setText(key);
    txfKey.addEventHandler(this, "key_set");
    ccw.tabManager.addControl(txfKey);

    txfDescription = new GTextField(app, x + 3 * HGAP + klen, y + HGAP/2, dlen, fieldHeight, G4P.SCROLLBARS_NONE);
    txfDescription.setFont(ccw.font);
    txfDescription.setLocalColorScheme(GCScheme.GREEN_SCHEME);
    txfDescription.setDefaultText("Description");
    txfDescription.setText(desc);
    txfDescription.addEventHandler(this, "description_set");
    ccw.tabManager.addControl(txfDescription);
  }

  public void key_set(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      if (source.getText().indexOf(SEPARATOR) >= 0)
        source.setText(source.getText().replaceAll(SEPARATOR, " "));
    }
  }

  public void description_set(GTextField source, GEvent event) {
    if (event == GEvent.ENTERED || event == GEvent.LOST_FOCUS) {
      if (source.getText().indexOf(SEPARATOR) >= 0)
        source.setText(source.getText().replaceAll(SEPARATOR, " "));
    }
  }

  public void draw() {
    app.pushMatrix();
    //		app.pushStyle();
    app.translate(px, py);
    // Descriptor area
    app.noStroke();
    app.fill(NAME_AREA);
    app.rect(0, 0, ccw.desc_UI_length, UI_HEIGHT);

    // Highlight
    app.stroke(isOver ? HIGHLIGHT : BORDER);
    app.strokeWeight(isOver ? 4 : 1);
    app.noFill();
    app.rect(0, 0, ccw.desc_UI_length, UI_HEIGHT);

    // Connectors 
    app.pushMatrix();
    app.translate(ccw.desc_UI_length, 0);
    drawConnectors();
    app.popMatrix();

    //		app.popStyle();
    app.popMatrix();
  }

  protected void drawConnectors() {
    for (int cn = 0; cn < connectors.length; cn++)
      connectors[cn].draw(cn * ccw.desc_UI_height);
  }

  public String getName() {
    return txfKey.getText();
  }
}

