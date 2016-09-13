public interface TConstants {

  final float INPUT_UI_HEIGHT	= 24;
  final float DESC_UI_HEIGHT	= 30;
  final float ELEMENT_UI_GAP	= 4;
  final float INPUT_UI_LENGTH	= 220;
  final float DESC_UI_LENGTH	= 300;
  final float TEXTFIELD_GAP	= 4;
  final float INICATOR_D		= 12;

  final float CONNECTOR_SIZE_R	= 10; // radius
  final float CONNECTOR_SIZE_R2	= CONNECTOR_SIZE_R * CONNECTOR_SIZE_R ; // radius squared
  final float CONNECTOR_SIZE_D	= 2 * CONNECTOR_SIZE_R ; // radius
  // Connector type
  final int INPUT 			= 0x01;
  final int DESC 				= 0x02;

  final float FONT_SIZE 		= 12;

  final int PANEL_WIDTH		= 320;
  final int PANEL_HEIGHT		= 330;

  // UI element type IDs
  final int UI_BUTTON			= 0x01;
  final int UI_COOLIEHAT		= 0x02;
  final int UI_SLIDER			= 0x03;
  final int UI_DESCRIPTOR		= 0x04;


  final int BACKGROUND			= 0xFFF0FFF0;
  final int PANEL					= 0xFF208020;
  final int BORDER 				= 0xFF4040A0;
  final int CONNECTION			= 0xFF8080A0;
  final int HIGHLIGHT 			= 0xFFFF40FF;
  final int NAME_AREA 			= 0xFFC8C8FF;
  final int CONNECTOR				= 0xFFFFC0FF;
  final int TEXTFILL				= 0xFF000080;
  final int PRESSED				= 0xFFFF3030;
  final int RELEASED				= 0xFF802020;
  final int INDICATOR				= 0xFFE0E0FF;
  final int SLIDER_CURSOR			= 0xFFFF4040;


  int NOT_OVER					= 0x0000;
  // Add connector number
  int OVER_CONNECTOR				= 0x0100;

  String SEPARATOR = "\t";
}

