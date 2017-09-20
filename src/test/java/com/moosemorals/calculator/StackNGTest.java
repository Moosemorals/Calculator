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
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class StackNGTest {

    public StackNGTest() {
    }

    @Test
    public void test_push1() {
        Stack s = new Stack();
        s.push(1);
        assertEquals(s.pop(), 1, 0.1);
    }

    @Test
    public void test_push2() {
        Stack s = new Stack();
        s.push(1);
        s.push(2);
        assertEquals(s.pop(), 2, 0.1);
        assertEquals(s.pop(), 1, 0.1);
    }

    @Test
    public void test_peek1() {
        Stack s = new Stack();
        s.push(1);
        assertEquals(s.peek(), 1, 0.1);
        assertEquals(s.peek(), 1, 0.1);
        assertEquals(s.peek(), 1, 0.1);
    }

    @Test
    public void test_peek2() {
        Stack s = new Stack();
        s.push(1);
        s.push(2);
        s.push(3);
        assertEquals(s.peek(0), 3, 0.1);
        assertEquals(s.peek(1), 2, 0.1);
        assertEquals(s.peek(2), 1, 0.1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void test_peak3() {
        Stack s = new Stack();
        assertEquals(s.peek(0), 0);
    }

    @Test
    public void test_emptyPop() {
        Stack s = new Stack();
        assertTrue(Double.isNaN(s.pop()));
    }

    @Test
    public void test_extend() {
        Stack s = new Stack();
        for (int i = 0; i < Stack.INITIAL_SIZE * 2; i += 1) {
            s.push(i);
        }
        assertEquals(s.pop(), Stack.INITIAL_SIZE * 2 - 1, 0.1);
    }

    @Test
    public void test_getDepth1() {
        Stack s = new Stack();
        assertEquals(s.getDepth(), 0);
    }

    @Test
    public void test_getDepth2() {
        Stack s = new Stack();
        s.push(1);
        assertEquals(s.getDepth(), 1);
    }

}
