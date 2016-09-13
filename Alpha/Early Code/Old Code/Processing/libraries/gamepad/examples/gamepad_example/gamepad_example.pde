/*

Copyright (c) 2010-2013 Iain Lobb, Michael Heuer

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

 */

import com.iainlobb.gamepad.Gamepad;

Gamepad gamepad;

void setup()
{
  size(320, 180);
  smooth();
  noStroke();
  frameRate(60);
  background(80);
  ellipseMode(CENTER);

  // arrows & control space keys mapped by default
  gamepad = new Gamepad(this);
}

void draw()
{
  fill(80);
  rect(0, 0, width, height);

  fill(0);
  ellipse(width/3 + gamepad.x() * 25, height/2 + gamepad.y() * 25, 25, 25);

  fill(255, gamepad.fire1().isDown() ? 255 : 25);
  ellipse(2*width/3, height/2 - 25, 15, 15);

  fill(255, gamepad.fire2().isDown() ? 255 : 25); 
  ellipse(2*width/3, height/2 + 25, 15, 15);
}