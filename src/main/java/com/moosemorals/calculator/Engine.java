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

import com.moosemorals.calculator.Commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Engine {

    private static final String ENTER = "⏎";
    private static final String DROP = "⊗";
    private static final String ROOT = "√";
    private static final String SWAP = "⇅";
    private static final String CLEAR = "☠";
    private static final String MINUS = "\u2796";
    private static final String PLUS = "\u2795";
    private static final String DIVIDE = "\u2797";
    private static final String MULTIPLY = "\u2716";
    private static final String SIGN_CHANGE = "\u00B1";
    private static final String POWER = "x^y";
    private static final String DUPLICAE = "dup";
    private static final String LN = "ln";

    private static final String DEFAULT_PATTERN = "#,##0.#########";
    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<EngineWatcher> engineWatchers;
    private final DecimalFormat df;
    private final CommandStack commandStack;
    private final LinkedList<String> display;
    private int fraction = 1;

    Engine() {
        display = new LinkedList<>();
        commandStack = new CommandStack();
        stack = new Stack();
        stack.push(0.0);
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

    public void push(final double value) {
        commandStack.addCommand(new Command() {
            @Override
            public void execute() {
                stack.push(value);
            }

            @Override
            public void undo() {
                stack.pop();
            }
        });

        notifyListeners();
    }

    public void undo() {
        commandStack.undo();
        notifyListeners();
    }

    public void command(final String cmd) {
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
            case ".":
                commandStack.addCommand(new Command() {
                    @Override
                    public void execute() {
                        display.push(cmd);
                    }

                    @Override
                    public void undo() {
                        display.pop();
                    }
                });
                break;
            case PLUS:
                commandStack.addCommand(new AddCommand(stack));
                break;
            case MINUS:
                commandStack.addCommand(new SubtractCommand(stack));
                break;
            case MULTIPLY:
                commandStack.addCommand(new MultiplyCommand(stack));

                break;
            case DIVIDE:
                commandStack.addCommand(new DivideCommand(stack));

                break;
            case SIGN_CHANGE:
                commandStack.addCommand(new SignChangeCommand(stack));
                break;
            case POWER:
                commandStack.addCommand(new PowerCommand(stack));

                break;
            case LN:
                commandStack.addCommand(new LnCommand(stack));

            case ROOT:
                commandStack.addCommand(new RootCommand(stack));

                break;
            case ENTER:
                commandStack.addCommand(new Command() {
                    @Override
                    public void execute() {
                        StringBuilder scratch = new StringBuilder();

                        display.forEach(scratch::append);
                        stack.push(Double.parseDouble(scratch.toString()));
                    }

                    @Override
                    public void undo() {
                        stack.pop();
                    }
                });

                break;
            case DUPLICAE:
                commandStack.addCommand(new DuplicateCommand(stack));

                break;
            case SWAP:
                commandStack.addCommand(new SwapCommand(stack));

                break;
            case DROP:
                commandStack.addCommand(new DropCommand(stack));

                break;
            case CLEAR:
                commandStack.clear();
                while (stack.getDepth() > 0) {
                    stack.pop();
                }

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

}
