void textBoxUpdate() {
  textSize(12);
  textAlign(LEFT);
  
  for (int i = 0; i < 17; i++) {
    text(dText[i], 703, (670 + (i * -11.3)));
  }
}

void textBoxAdd(String newText0, String newText1, String newText2){
  for(int i = 15; i >= 0; i--){
    dText[i + 1] = dText[i];
  }
  
  dText[0] = newText0 + " " + newText1 + " " + newText2;
}

