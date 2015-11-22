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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Engine {

    public static final String ENTER = "⏎";
    public static final String DROP = "⊗";
    public static final String ROOT = "√";
    public static final String SWAP = "⇅";
    public static final String CLEAR = "☠";
    public static final String MINUS = "\u2796";
    public static final String PLUS = "\u2795";
    public static final String DIVIDE = "\u2797";
    public static final String MULTIPLY = "\u2716";
    public static final String SIGN_CHANGE = "\u00B1";
    public static final String POWER = "x^y";
    public static final String DUPLICAE = "dup";
    public static final String LN = "ln";

    private static final String DEFAULT_PATTERN = "#,##0.#########";
    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<EngineWatcher> engineWatchers;
    private final DecimalFormat df;
    private int fraction = 1;
    private State state = State.Decimal;

    public Engine() {
        stack = new Stack();
        stack.push(0);
        engineWatchers = new HashSet<>();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setNaN("Err");
        df = new DecimalFormat(DEFAULT_PATTERN, symbols);
    }

    public double peek() {
        return stack.peek();
    }

    public double peek(int d) {
        return stack.peek(d);
    }

    public void push(double value) {
        stack.push(value);
        notifyListeners();
    }

    public void command(String cmd) {
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
                switch (state) {
                    case Decimal:
                        left = stack.pop();
                        right = Double.parseDouble(cmd);
                        if (left >= 0) {
                            stack.push(left * 10.0 + right);
                        } else {
                            stack.push(left * 10.0 - right);
                        }
                        break;
                    case Fraction:
                        left = stack.pop();
                        right = Double.parseDouble(cmd);
                        if (left >= 0) {
                            stack.push(left + right / (Math.pow(10, fraction)));
                        } else {
                            stack.push(left - right / (Math.pow(10, fraction)));
                        }
                        fraction += 1;
                        break;
                    case Display:
                        stack.push(Double.parseDouble(cmd));
                        fraction = 1;
                        state = State.Decimal;
                        break;
                }
                break;
            case ".":
                if (state == State.Display) {
                    stack.push(0);
                    fraction = 1;
                }
                state = State.Fraction;

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
            case SIGN_CHANGE:
                stack.push(0 - stack.pop());
                break;
            case POWER:
                left = stack.pop();
                right = stack.pop();
                stack.push(Math.pow(right, left));
                state = State.Display;
                break;
            case LN:
                left = stack.pop();
                stack.push(Math.log(left));
                state = State.Display;
            case ROOT:
                stack.push(Math.sqrt(stack.pop()));
                state = State.Display;
                break;
            case ENTER:
                stack.push(0);
                fraction = 1;
                state = State.Decimal;
                break;
            case DUPLICAE:
                stack.push(stack.peek());
                state = state.Display;
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
            default:
                log.warn("Don't know about that");
                break;
        }
        notifyListeners();
    }

    public int getDepth() {
        return stack.getDepth();
    }

    public String getElementAt(int index) {
        if (index == 0 && state == State.Fraction) {
            StringBuilder pattern = new StringBuilder();
            pattern.append("#,##0.");
            for (int i = 1; i < fraction; i += 1) {
                pattern.append("0");
            }
            pattern.append("######");
            df.applyPattern(pattern.toString());
        } else {
            df.applyPattern(DEFAULT_PATTERN);
        }
        return df.format(stack.peek(index));
    }

    private void notifyListeners() {
        synchronized (engineWatchers) {
            for (EngineWatcher watcher : engineWatchers) {
                watcher.onEngineChanged();
            }
        }
    }

    public void addEngineWatcher(EngineWatcher watcher) {
        synchronized (engineWatchers) {
            engineWatchers.add(watcher);
        }
    }

    public void removeEngineWatcher(EngineWatcher watcher) {
        synchronized (engineWatchers) {
            engineWatchers.remove(watcher);
        }
    }

    private enum State {

        Decimal, Fraction, Display;
    }
}
