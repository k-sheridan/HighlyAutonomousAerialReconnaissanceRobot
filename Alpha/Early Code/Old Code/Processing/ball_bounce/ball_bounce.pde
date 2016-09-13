int ballX = 350;
int ballY = 350;
int ballX_speed = 10;
int ballY_speed = 5;

void setup(){
  size(700, 700);
}

void draw(){
 background(0);
 ballY_speed += 2.8;
 if(ballX >= width){
   ballX_speed *= -1;
 }
 if(ballX <= 0){
   ballX_speed *= -1;
 }
 if(ballY >= height){
   ballY_speed *= -1;
 }
 if(ballY <= 0){
   ballY_speed *= -1;
 }  
 
 ballX += ballX_speed;
 ballY += ballY_speed;
 fill(255);
 ellipse(ballX,ballY,25,25);
}
