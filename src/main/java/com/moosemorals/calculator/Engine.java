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

    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<ListDataListener> dataListeners;

    private double currentValue = 0.0;
    private int fraction = 1;
    private State state = State.Decimal;

    public Engine() {
        stack = new Stack();
        dataListeners = new HashSet<>();
    }

    public double peek() {
        return stack.peek();
    }

    public void push(double value) {
        stack.push(value);
        notifyListeners();
    }

    public double pop() {
        double value = stack.pop();
        notifyListeners();
        return value;
    }

    public void add() {
        stack.push(stack.pop() + stack.pop());
        notifyListeners();
    }

    public void subtract() {
        double right = stack.pop();
        double left = stack.pop();
        stack.push(left - right);
        notifyListeners();
    }

    public void multiply() {
        stack.push(stack.pop() * stack.pop());
        notifyListeners();
    }

    public void divide() {
        double right = stack.pop();
        double left = stack.pop();
        stack.push(left / right);
        notifyListeners();
    }

    public void click(String cmd) {
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
                    currentValue *= 10;
                    currentValue += Double.parseDouble(cmd);
                } else if (state == State.Fraction) {
                    currentValue += Double.parseDouble(cmd) / (Math.pow(10, fraction));
                    fraction += 1;
                } else if (state == State.Display) {
                    currentValue = Double.parseDouble(cmd);
                    fraction = 1;
                    state = State.Decimal;
                }
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                if (state != State.Display) {
                    stack.push(currentValue);
                }
                switch (cmd) {
                    case "+":
                        add();
                        break;
                    case "-":
                        subtract();
                        break;
                    case "*":
                        multiply();
                        break;
                    case "/":
                        divide();
                        break;
                }
                currentValue = 0;
                state = State.Display;
                break;
            case "⏎":
                stack.push(currentValue);
                state = State.Display;
                break;
            case ".":
                if (state == State.Display) {
                    currentValue = 0;
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

    public double peek(int d) {
        return stack.peek(d);
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
        return stack.getDepth() + 1;

    }

    @Override
    public String getElementAt(int index) {
        int size = getSize() - 1;
        if (size == 0 || index == size) {
            return String.format("Current: %f", currentValue);
        } else {
            size -= 1;
            return String.format("%d: %f", index, stack.peek(size - index));
        }
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
