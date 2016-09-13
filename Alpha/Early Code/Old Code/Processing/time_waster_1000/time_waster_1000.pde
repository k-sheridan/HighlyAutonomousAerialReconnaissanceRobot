
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;
import processing.opengl.*;

ControlIO control;
ControlDevice gpad;

float rX, rY, lX, lY;
boolean b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12;

int characterX = 0;
int characterY = 0;

int last_characterX = 0;
int last_characterY = 0;

int characterX_speed = 0;
int characterY_speed = 0;

int ai_num = 0;
int aiX = 100;
int aiY = 100;
int cap = 10;

boolean mouse = false;

void setup(){
 size(1280, 720, OPENGL);
 frame.setResizable(true);
 //control = ControlIO.getInstance(this);
 //gpad = control.getMatchedDevice("fs_config"); 
}

void draw(){
 background(255, 0, 255); 
 mouseClick();
 characterCompute();
 aiCompute();
}

void characterCompute(){
 pushMatrix();
 characterX = mouseX;
 characterY = mouseY;
 fill(255);
 translate(characterX, characterY, 0);
 rotateX(characterX * 0.01);
 rotateY(characterY * 0.01);
 box(100, 50, 100);
 popMatrix();
 
 characterX_speed = characterX - last_characterX;
 characterY_speed = characterY - last_characterY;
 last_characterX = characterX;
 last_characterY = characterY;
}

void aiCompute(){
  int posLockX;
  int posLockY;
  int translateX;
  int translateY;
  
  posLockX = characterX + (characterX_speed * 5);
  posLockY = characterY + (characterY_speed * 5);
  translateX = characterX - aiX;
  translateY = characterY - aiY;
  
  aiX += translateX * 0.03;
  aiY += translateY * 0.03;
  fill(0);
  ellipse(aiX, aiY, 50, 50);
}

void mouseClick(){
 if(mousePressed == true && mouse == false){
  mouse = true;
 }
 else{
  mouse = false;
 } 
}

void getUserInput(){
 rX = gpad.getSlider("rX").getValue();
 rY = gpad.getSlider("rY").getValue();
 lX = gpad.getSlider("lX").getValue();
 lY = gpad.getSlider("lY").getValue();
 //b1 = gpad.getButton("b1").pressed();
 //b2 = gpad.getButton("b2").pressed();
 //b3 = gpad.getButton("b3").pressed();
 //b4 = gpad.getButton("b4").pressed();
 println(rX + " " + rY + " " + lX + " " + lY + " " + b1);
}
