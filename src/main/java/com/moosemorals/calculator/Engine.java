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

import com.moosemorals.calculator.ui.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Engine {

    private static final String ENTER = "⏎";
    private static final String CLEAR = "☠";
    
    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<EngineWatcher> engineWatchers;
    
    private final CommandStack commandStack;
    private final LinkedList<String> display;
    private final Config config;
    private final ScriptEngine scriptEngine;
    private final Map<String, ScriptObjectMirror> scriptCache;

    Engine(Config config) {
        this.config = config;

        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptCache = new HashMap<>();
        display = new LinkedList<>();
        commandStack = new CommandStack();
        stack = new Stack();
        stack.push(0.0);
        engineWatchers = new HashSet<>();
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
        for (int i = 0; i < config.getButtonCount(); i += 1) {
            Button b = config.getButton(i);

            if (cmd.equals(b.getLabel())) {
                String code = b.getCode();
                if (code != null) {
                    try {
                        JSObject func = (JSObject) scriptEngine.eval(b.getCode());

                        commandStack.addCommand(new JsCommand((ScriptObjectMirror) func.call(null, stack)));
                        notifyListeners();
                        return;
                    } catch (ScriptException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

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
                    double left;
                    @Override
                    public void execute() {
                        display.addLast(cmd);
                        left = stack.pop();
                        stack.push(getDisplayValue());
                    }

                    @Override
                    public void undo() {
                        display.removeLast();
                        stack.pop();
                        stack.push(left);
                    }
                });
                break;

            case ENTER:
                commandStack.addCommand(new Command() {
                    double left;
                    @Override
                    public void execute() {                                                
                        stack.push(getDisplayValue());
                        display.clear();
                    }

                    @Override
                    public void undo() {
                        stack.pop();
                    }
                });

                break;
            case CLEAR:
                commandStack.clear();
                display.clear();
                stack.reset();
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

    public double getElementAt(int index) {

        return stack.peek(index);
    }

    private void notifyListeners() {
        synchronized (engineWatchers) {
            for (EngineWatcher watcher : engineWatchers) {
                watcher.onEngineChanged();
            }
        }
    }

    private double getDisplayValue() {
        StringBuilder scratch = new StringBuilder();        
        display.forEach(scratch::append);
        return Double.parseDouble(scratch.toString());
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
