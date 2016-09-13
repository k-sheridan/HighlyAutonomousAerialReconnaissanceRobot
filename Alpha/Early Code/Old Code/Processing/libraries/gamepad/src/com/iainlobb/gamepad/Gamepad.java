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
package com.iainlobb.gamepad;

import processing.event.KeyEvent;

import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;

/**
 * Gamepad.
 *
 * @author  Iain Lobb
 * @author  Michael Heuer (java port)
 */
public final class Gamepad
{
    private final GamepadInput up;
    private final GamepadInput down;
    private final GamepadInput left;
    private final GamepadInput right;
    private final GamepadInput fire1;
    private final GamepadInput fire2;
    private final List<GamepadInput> inputs;

    private final GamepadMultiInput upLeft;
    private final GamepadMultiInput downLeft;
    private final GamepadMultiInput upRight;
    private final GamepadMultiInput downRight;
    private final GamepadMultiInput anyDirection;
    private final List<GamepadMultiInput> multiInputs;

    private float x;
    private float y;
    private float targetX;
    private float targetY;
    private float angle;
    private float rotation;
    private float magnitude;
    private final boolean isCircle;
    private final float ease;


    public Gamepad(final PApplet applet)
    {
        this(applet, false, 0.2f);
    }

    public Gamepad(final PApplet applet, final boolean isCircle, final float ease)
    {
        up = new GamepadInput();
        down = new GamepadInput();
        left = new GamepadInput();
        right = new GamepadInput();
        fire1 = new GamepadInput();
        fire2 = new GamepadInput();
        inputs = asList(up, down, left, right, fire1, fire2);

        upLeft = new GamepadMultiInput(up, left);
        upRight = new GamepadMultiInput(up, right);
        downLeft = new GamepadMultiInput(down, left);
        downRight = new GamepadMultiInput(down, right);
        anyDirection = new GamepadMultiInput(true, up, down, left, right);
        multiInputs = asList(upLeft, upRight, downLeft, downRight, anyDirection);

        useArrows();
        useControlSpace();

        this.isCircle = isCircle;
        this.ease = ease;

        applet.registerMethod("post", this);
        applet.registerMethod("keyEvent", this);
    }


    // called by processing after draw()
    public void post()
    {
        update();
    }

    // called by processing on key event
    public void keyEvent(final KeyEvent keyEvent)
    {
        switch (keyEvent.getAction())
        {
        case KeyEvent.PRESS:
            keyDown(keyEvent.getKeyCode());
            break;
        case KeyEvent.RELEASE:
            keyUp(keyEvent.getKeyCode());
            break;
        default:
            break;
        }
    }

    public void mapDirection(final int up, final int down, final int left, final int right, final boolean replaceExisting)
    {
        this.up.mapKey(up, replaceExisting);
        this.down.mapKey(down, replaceExisting);
        this.left.mapKey(left, replaceExisting);
        this.right.mapKey(right, replaceExisting);
    }

    public void useArrows()
    {
        useArrows(false);
    }

    public void useArrows(final boolean replaceExisting)
    {
        mapDirection(java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_RIGHT, replaceExisting);
    }

    public void useWASD()
    {
        useWASD(false);
    }

    public void useWASD(final boolean replaceExisting)
    {
        mapDirection(java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_D, replaceExisting);
    }

    public void useIJKL()
    {
        useIJKL(false);
    }

    public void useIJKL(final boolean replaceExisting)
    {
        mapDirection(java.awt.event.KeyEvent.VK_I, java.awt.event.KeyEvent.VK_K, java.awt.event.KeyEvent.VK_J, java.awt.event.KeyEvent.VK_L, replaceExisting);
    }

    public void useZQSD()
    {
        useZQSD(false);
    }

    public void useZQSD(final boolean replaceExisting)
    {
        mapDirection(java.awt.event.KeyEvent.VK_Z, java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_Q, java.awt.event.KeyEvent.VK_D, replaceExisting);
    }

    public void mapFireButtons(final int fire1, final int fire2, final boolean replaceExisting)
    {
        this.fire1.mapKey(fire1, replaceExisting);
        this.fire2.mapKey(fire2, replaceExisting);
    }

    public void useChevrons()
    {
        useChevrons(false);
    }

    public void useChevrons(final boolean replaceExisting)
    {
        mapFireButtons(java.awt.event.KeyEvent.VK_LESS, java.awt.event.KeyEvent.VK_GREATER, replaceExisting);
    }

    public void useGH()
    {
        useGH(false);
    }

    public void useGH(final boolean replaceExisting)
    {
        mapFireButtons(java.awt.event.KeyEvent.VK_G, java.awt.event.KeyEvent.VK_H, replaceExisting);
    }

    public void useZX()
    {
        useZX(false);
    }

    public void useZX(final boolean replaceExisting)
    {
        mapFireButtons(java.awt.event.KeyEvent.VK_Z, java.awt.event.KeyEvent.VK_X, replaceExisting);
    }

    public void useYX()
    {
        useYX(false);
    }

    public void useYX(final boolean replaceExisting)
    {
        mapFireButtons(java.awt.event.KeyEvent.VK_Y, java.awt.event.KeyEvent.VK_X, replaceExisting);
    }

    public void useControlSpace()
    {
        useControlSpace(false);
    }

    public void useControlSpace(final boolean replaceExisting)
    {
        mapFireButtons(java.awt.event.KeyEvent.VK_CONTROL, java.awt.event.KeyEvent.VK_SPACE, replaceExisting);
    }

    public void update() // step()?
    {
        x += (targetX - x) * ease;
        y += (targetY - y) * ease;

        magnitude = (float) Math.sqrt(x * x + y * y);
        angle = (float) Math.atan2(x, y);
        rotation = angle * 57.29577951308232f;

        for (GamepadInput input : inputs)
        {
            input.update();
        }
    }

    public float x() { return x; }
    public float y() { return y; }
    public float angle() { return angle; }
    public float magnitude() { return magnitude; }
    public float rotation() { return rotation; }
    public GamepadInput up() { return up; }
    public GamepadInput down() { return down; }
    public GamepadInput left() { return left; }
    public GamepadInput right() { return right; }
    public GamepadInput fire1() { return fire1; }
    public GamepadInput fire2() { return fire2; }
    public GamepadMultiInput upLeft() { return upLeft; }
    public GamepadMultiInput downLeft() { return downLeft; }
    public GamepadMultiInput upRight() { return upRight; }
    public GamepadMultiInput downRight() { return downRight; }
    public GamepadMultiInput anyDirection() { return anyDirection; }

    private void keyUp(final int keyCode) // keyReleased?
    {
        for (GamepadInput input : inputs)
        {
            input.keyUp(keyCode);
        }
        updateState();
    }

    private void keyDown(final int keyCode) // keyPressed?
    {
        for (GamepadInput input : inputs)
        {
            input.keyDown(keyCode);
        }
        updateState();
    }

    //protected void updateState()
    private void updateState()
    {
        for (GamepadMultiInput multiInput : multiInputs)
        {
            multiInput.update();
        }
        if (up.isDown())
        {
            targetY = -1;
        }
        else if (down.isDown())
        {
            targetY = 1;
        }
        else
        {
            targetY = 0;
        }

        if (left.isDown())
        {
            targetX = -1;
        }
        else if (right.isDown())
        {
            targetX = 1;
        }
        else
        {
            targetX = 0;
        }

        float targetAngle = (float) Math.atan2(targetX, targetY);
        //rotation = angle * 57.29577951308232f;

        if (isCircle && anyDirection.isDown())
        {
            targetX = (float) Math.sin(targetAngle);
            targetY = (float) Math.cos(targetAngle);
        }
    }

    private static <T> List<T> asList(final T... elements)
    {
        return Arrays.asList(elements);
    }
}