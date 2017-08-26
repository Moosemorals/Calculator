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

import java.util.Arrays;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public final class Stack {

    public final static int INITIAL_SIZE = 4;
    private final Logger log = LoggerFactory.getLogger(Stack.class);

    private final Object lock;
    private final LinkedList<Double> stack;
  
    public Stack() {
        lock = new Object();
        stack = new LinkedList<>();        
        reset();
    }

    public void reset() {
        synchronized (lock) {
            stack.clear();
        }
    }

    public double pop() {
        synchronized (lock) {
            if (stack.isEmpty()) {
                return Double.NaN;
            } else {
                return stack.pop();
            }
        }
    }

    public void push(double value) {
        synchronized (lock) {
            stack.push(value);
        }
    }

    public double peek() {
        synchronized (lock) {
            return stack.peek();
        }
    }

    public double peek(int d) {        
        synchronized (lock) {
            return stack.get(d);
        }
    }

    public int getDepth() {
        return stack.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < stack.size(); i += 1) {
            if (i != 0) {
                result.append(", ");
            }
            result.append(stack.get(i));
        }
        result.append("]");
        return result.toString();
    }

}
