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

import java.util.ArrayList;
import java.util.List;

/**
 * Gamepad input.
 *
 * @author  Iain Lobb
 * @author  Michael Heuer (java port)
 */
public final class GamepadInput
{
    private boolean isDown;
    private boolean isPressed;
    private boolean isReleased;
    private int downTicks = -1;
    private int upTicks = -1;
    private final List<Integer> mappedKeys;

    GamepadInput()
    {
        this(-1);
    }

    GamepadInput(final int keyCode)
    {
        mappedKeys = new ArrayList<Integer>();
        if (keyCode >= 0)
        {
            mappedKeys.add(Integer.valueOf(keyCode));
        }
    }

    public boolean isDown() { return isDown; }
    public boolean isPressed() { return isPressed; }
    public boolean isReleased() { return isReleased; }
    public int downTicks() { return downTicks; }
    public int upTicks() { return upTicks; }

    public void mapKey(final int keyCode, final boolean replaceAll)
    {
        if (replaceAll)
        {
            mappedKeys.clear();
        }
        mappedKeys.add(Integer.valueOf(keyCode));
    }

    public void unmapKey(final int keyCode)
    {
        mappedKeys.remove(Integer.valueOf(keyCode));
    }

    public void update()
    {
        if (isDown)
        {
            isPressed = (downTicks == -1);
            isReleased = false;
            downTicks++;
            upTicks = -1;
        }
        else
        {
            isReleased = (upTicks == -1);
            isPressed = false;
            upTicks++;
            downTicks = -1;
        }
    }

    public void keyDown(final int keyCode)
    {
        if (mappedKeys.contains(keyCode))
        {
            isDown = true;
        }
    }

    public void keyUp(final int keyCode)
    {
        if (mappedKeys.contains(keyCode))
        {
            isDown = false;
        }
    }
}