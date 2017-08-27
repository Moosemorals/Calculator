/*
 * The MIT License
 *
 * Copyright 2015 Osric Wilkinson <osric@fluffypeople.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.moosemorals.calculator.xml;

import com.moosemorals.calculator.Button;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class ButtonsParserTest {

    @Test
    public void test_parseFile() throws Exception {
        InputStream in = new BufferedInputStream(getClass().getResourceAsStream("/testButtons.xml"));

        List<Button> buttons = new ButtonsParser().parse(in);

        assertEquals(buttons.size(), 3);

        Button firstButton = buttons.get(0);
        assertEquals(firstButton.getX(), 0);
        assertEquals(firstButton.getY(), 0);
        // These are all defaults
        assertEquals(firstButton.getLabel(), "");
        assertEquals(firstButton.getCode(), null);
        assertEquals(firstButton.getKey(), 0);
        assertEquals(firstButton.getWidth(), 1);
        assertEquals(firstButton.getHeight(), 1);

        Button secondButton = buttons.get(1);
        assertEquals(secondButton.getX(), 1);
        assertEquals(secondButton.getY(), 1);
        assertEquals(secondButton.getLabel(), "Test");
        assertEquals(secondButton.getCode(), "NOOP");
        assertEquals(secondButton.getKey(), 't');
        assertEquals(secondButton.getWidth(), 5);
        assertEquals(secondButton.getHeight(), 2);

        Button thirdButton = buttons.get(2);
        assertEquals(thirdButton.getLabel(), "\u221A");
        assertEquals(thirdButton.getKey(), '\n');

    }

}
