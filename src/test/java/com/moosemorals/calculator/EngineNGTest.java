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
package com.moosemorals.calculator;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class EngineNGTest {

    @Test
    public void test_add() {
        Engine e = new Engine();
        e.push(1);
        e.push(1);
        e.add();

        assertEquals(e.pop(), 2.0, 0.1);
    }

    @Test
    public void test_sub1() {
        Engine e = new Engine();
        e.push(1);
        e.push(1);
        e.subtract();

        assertEquals(e.pop(), 0.0, 0.1);
    }

    @Test
    public void test_sub2() {
        Engine e = new Engine();
        e.push(2);
        e.push(1);
        e.subtract();

        assertEquals(e.pop(), 1.0, 0.1);
    }

    @Test
    public void test_mut1() {
        Engine e = new Engine();
        e.push(2);
        e.push(1);
        e.multiply();

        assertEquals(e.pop(), 2.0, 0.1);
    }

    @Test
    public void test_dev1() {
        Engine e = new Engine();
        e.push(1);
        e.push(2);
        e.divide();

        assertEquals(e.pop(), 0.5, 0.1);
    }
}
