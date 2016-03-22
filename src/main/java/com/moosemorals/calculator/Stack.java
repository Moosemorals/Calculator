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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Stack {

    public final static int INITIAL_SIZE = 4;
    private final Logger log = LoggerFactory.getLogger(Stack.class);

    protected final Object lock;
    protected double[] backingArray;
    protected int depth;

    public Stack() {
        lock = new Object();
        backingArray = new double[INITIAL_SIZE];
        for (int i = 0; i < INITIAL_SIZE; i += 1) {
            backingArray[i] = Double.NaN;
        }
        depth = 0;
    }

    public double pop() {
        synchronized (lock) {
            if (depth >= 1) {
                return backingArray[--depth];
            } else {
                return Double.NaN;
            }
        }
    }

    public void push(double value) {
        synchronized (lock) {
            if (depth == backingArray.length - 1) {
                double[] temp = new double[backingArray.length * 2];
                for (int i = 0; i < temp.length; i += 1) {
                    temp[i] = Double.NaN;
                }
                System.arraycopy(backingArray, 0, temp, 0, backingArray.length);
                backingArray = temp;
            }
            backingArray[depth++] = value;
        }
    }

    public double peek() {
        synchronized (lock) {
            return backingArray[depth - 1];
        }
    }

    public double peek(int d) {
        if (d < 0) {
            throw new IndexOutOfBoundsException("Trying to peak at negative stack.");
        }
        if (d >= depth) {
            throw new IndexOutOfBoundsException("Trying to peak past end of stack: Wanted " + d + ", can have: " + depth);
        }
        synchronized (lock) {
            return backingArray[(depth - 1) - d];
        }
    }

    public int getDepth() {
        return depth;
    }

}
