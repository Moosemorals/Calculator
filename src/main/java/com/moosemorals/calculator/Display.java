/*
 * The MIT License
 *
 * Copyright 2017 Osric Wilkinson (osric@fluffypeople.com).
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

import java.text.DecimalFormat;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson (osric@fluffypeople.com)
 */
public final class Display {

    private final static String DISPLAY_FORMAT = "#############0.################";
    private final Logger log = LoggerFactory.getLogger(Display.class);
    private final LinkedList<String> display;
    
    private final DecimalFormat df;

    public Display() {
        df = new DecimalFormat(DISPLAY_FORMAT);
        display = new LinkedList<>();
        reset();
    }

    public void push(String n) {
        if (n == null) {
            throw new IllegalArgumentException("Can't push nulls into display");
        } else if (!n.matches("^[0-9\\.]$")) {
            throw new IllegalArgumentException("Can only push one of [0-9\\.] onto display");
        }

        display.addLast(n);
    }

    public String pop() {
        return display.removeLast();
    }

    public void reset() {
        display.clear();
    }

    public double getValue() {
        return Double.parseDouble(toString());
    }

    public boolean hasValue() {
        return !display.isEmpty();
    }
    
    public void setValue(double value) {
        String val = df.format(value);
        if (val.substring(val.length() - 1).equals(".")) {
            val = val.substring(0, val.length() - 1);
        }
        log.debug("Val [{}]", val);
        display.clear();
        for (int i = 0; i < val.length(); i += 1) {            
            display.addLast(val.substring(i, i + 1));            
        }
        
    }

    @Override
    public String toString() {
        if (display.isEmpty()) {
            return "0";
        }
        StringBuilder scratch = new StringBuilder();
        display.forEach(scratch::append);
        return scratch.toString();
    }

}
