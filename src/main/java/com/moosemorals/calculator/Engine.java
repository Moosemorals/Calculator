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

import java.util.HashSet;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Engine implements ListModel<String> {

    public static final String ENTER = "⏎";
    public static final String DROP = "⊗";
    public static final String ROOT = "√";
    public static final String SWAP = "⇅";
    public static final String CLEAR = "☠";
    public static final String MINUS = "\u2796";
    public static final String PLUS = "\u2795";
    public static final String DIVIDE = "\u2797";
    public static final String MULTIPLY = "\u2716";

    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<ListDataListener> dataListeners;

    private int fraction = 1;
    private State state = State.Decimal;

    public Engine() {
        stack = new Stack();
        stack.push(0);
        dataListeners = new HashSet<>();
    }

    public double peek() {
        return stack.peek();
    }

    public double peek(int d) {
        return stack.peek(d);
    }

    public void click(String cmd) {
        double left, right;
        switch (cmd) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                if (state == State.Decimal) {
                    stack.push(stack.pop() * 10.0 + Double.parseDouble(cmd));
                } else if (state == State.Fraction) {
                    stack.push(stack.pop() + Double.parseDouble(cmd) / (Math.pow(10, fraction)));
                    fraction += 1;
                } else if (state == State.Display) {

                    stack.push(Double.parseDouble(cmd));
                    fraction = 1;
                    state = State.Decimal;
                }
                break;
            case PLUS:
                stack.push(stack.pop() + stack.pop());
                state = State.Display;
                break;
            case MINUS:
                right = stack.pop();
                left = stack.pop();
                stack.push(left - right);

                state = State.Display;
                break;
            case MULTIPLY:
                stack.push(stack.pop() * stack.pop());
                state = State.Display;
                break;
            case DIVIDE:
                right = stack.pop();
                left = stack.pop();
                stack.push(left / right);
                state = State.Display;
                break;
            case ROOT:
                stack.push(Math.sqrt(stack.pop()));
                state = State.Display;
                break;
            case ENTER:
                stack.push(0);
                state = State.Decimal;
                break;
            case SWAP:
                left = stack.pop();
                right = stack.pop();
                stack.push(left);
                stack.push(right);
                state = State.Display;
                break;
            case DROP:
                stack.pop();
                state = State.Display;
                break;
            case CLEAR:
                while (stack.getDepth() > 0) {
                    stack.pop();
                }
                state = State.Display;
                break;
            case ".":
                if (state == State.Display) {
                    fraction = 1;
                }
                state = State.Fraction;

                break;
            default:
                log.warn("Don't know about that");
                break;
        }
        notifyListeners();
    }

    public int getDepth() {
        return stack.getDepth();
    }

    private void notifyListeners() {
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, stack.getDepth());
        synchronized (dataListeners) {
            for (ListDataListener l : dataListeners) {
                l.contentsChanged(e);
            }
        }
    }

    @Override
    public int getSize() {
        return stack.getDepth();

    }

    @Override
    public String getElementAt(int index) {
        return String.format("%f", stack.peek((stack.getDepth() - 1) - index));
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        synchronized (dataListeners) {
            dataListeners.add(l);
        }
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        synchronized (dataListeners) {
            dataListeners.remove(l);
        }
    }

    private enum State {

        Decimal, Fraction, Display;
    }
}
