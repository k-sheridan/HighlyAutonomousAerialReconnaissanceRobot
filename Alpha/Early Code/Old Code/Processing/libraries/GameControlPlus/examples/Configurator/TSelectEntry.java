import g4p_controls.*;

import org.gamecontrolplus.*;

import processing.core.*;

public class TSelectEntry implements Comparable<TSelectEntry> {
  public final PApplet app;
  public final ControlIO controlIO;
  public final ControlDevice device;
  public final GLabel displayName;
  public final GButton btnGoConfig;
  public TConfigUI winCofig = null;

  public TSelectEntry(PApplet papp, ControlIO controlIO, ControlDevice dev) {
    this.app = papp;
    this.controlIO = controlIO;
    this.device = dev;
    displayName = new GLabel(papp, 36, 20, app.width-36, 20);
    displayName.setText(device.getName() + "  [" + device.getTypeName() + "]");
    displayName.setTextAlign(GAlign.LEFT, null);
    btnGoConfig = new GButton(app, 4, 24, 24, 14);
    btnGoConfig.addEventHandler(this, "configClick");
  }

  public void setIndex(int startY, int index) {
    displayName.moveTo(36, startY + index * 20);
    if (btnGoConfig != null) btnGoConfig.moveTo(4, startY + 4 + index * 20);
  }

  public void configClick(GButton source, GEvent event) {
    if (winCofig == null) {
      winCofig = new TConfigUI(app, this);
      source.setVisible(false);
    }
  }

  @Override
    public int compareTo(TSelectEntry entry) {
    return device.compareTo(entry.device);
  }
}

