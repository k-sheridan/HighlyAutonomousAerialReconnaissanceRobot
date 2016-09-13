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

import java.util.Arrays;
import java.util.List;

/**
 * Gamepad multi input.
 *
 * @author  Iain Lobb
 * @author  Michael Heuer (java port)
 */
public final class GamepadMultiInput
{
    private boolean isDown;
    private boolean isPressed;
    private boolean isReleased;
    private int downTicks = -1;
    private int upTicks = -1;
    private final boolean isOr;
    private final List<GamepadInput> inputs;

    GamepadMultiInput(final GamepadInput... inputs)
    {
        this(false, inputs);
    }

    // unfortunately vararg must be last parameter
    GamepadMultiInput(final boolean isOr, final GamepadInput... inputs)
    {
        this.isOr = isOr;
        this.inputs = asList(inputs);
    }

    public boolean isDown() { return isDown; }
    public boolean isPressed() { return isPressed; }
    public boolean isReleased() { return isReleased; }
    public int downTicks() { return downTicks; }
    public int upTicks() { return upTicks; }

    public void update()
    {
        if (isOr)
        {
            isDown = false;
            for (GamepadInput input : inputs)
            {
                if (input.isDown())
                {
                    isDown = true;
                    break;
                }
            }
        }
        else
        {
            isDown = true;
            for (GamepadInput input : inputs)
            {
                if (!input.isDown())
                {
                    isDown = false;
                    break;
                }
            }
        }

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

    private static <T> List<T> asList(final T... elements)
    {
        return Arrays.asList(elements);
    }
}
