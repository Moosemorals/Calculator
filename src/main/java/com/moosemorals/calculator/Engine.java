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
    private static final String CLEAR = "☠";

    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Stack stack;
    private final Set<EngineWatcher> engineWatchers;

    private final CommandStack commandStack;
    private final Display display;
    private final Config config;
    private final ScriptEngine scriptEngine;
    private final Map<String, ScriptObjectMirror> scriptCache;

    private final Command enterCommand = new Command() {
        double left;

        @Override
        public void execute() {
            stack.push(display.getValue());
            display.reset();
        }

        @Override
        public void undo() {
            display.setValue(stack.pop());
        }
    };

    Engine(Config config) {
        this.config = config;

        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptCache = new HashMap<>();
        display = new Display();
        commandStack = new CommandStack();
        stack = new Stack();

        engineWatchers = new HashSet<>();
    }

    public double peek() {
        if (display.hasValue()) {
            return display.getValue();
        } else {
            return stack.peek();
        }
    }

    public double peek(int d) {
        if (display.hasValue()) {
            if (d == 0) {
                return display.getValue();
            } else {
                return stack.peek(d - 1);
            }
        } else {
           return stack.peek(d);
        }
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
        boolean handled = false;
        switch (cmd) {
            case ".":
                if (display.hasDecimalPoint()) {
                    handled = true;
                    break;
                }
                // Intentional drop through
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
                commandStack.addCommand(new Command() {
                    double left;

                    @Override
                    public void execute() {
                        display.push(cmd);
                    }

                    @Override
                    public void undo() {
                        display.pop();
                    }
                });
                handled = true;
                break;

            case CLEAR:
                commandStack.clear();
                display.reset();
                stack.reset();
                handled = true;
                break;
            default:
                if (display.hasValue()) {
                    commandStack.addCommand(enterCommand);
                }
                break;
        }

        if (handled) {
            notifyListeners();
            return;
        }

        log.debug("Stack before {}", stack.toString());
        for (int i = 0; i < config.getButtonCount(); i += 1) {
            Button b = config.getButton(i);

            if (cmd.equals(b.getLabel())) {
                String code = b.getCode();
                if (code != null) {                    
                    try {
                        if (!scriptCache.containsKey(cmd)) {
                            JSObject func = (JSObject) scriptEngine.eval(b.getCode());
                            scriptCache.put(cmd, (ScriptObjectMirror) func.call(null, stack));
                        }
                        
                        log.debug("Depth {}, need {}", stack.getDepth(), b.getIn());
                        if (stack.getDepth() >= b.getIn()) {
                            commandStack.addCommand(new JsCommand(scriptCache.get(cmd)));
                        } else {
                            log.warn("Not enough stack for {}", b.getLabel());
                            stack.push(Double.NaN);
                        }
                        
                        break;
                    } catch (ScriptException ex) {
                        log.error("Button [{}]: Code error", b.getLabel(), ex);
                        stack.push(Double.NaN);
                    }
                } else {
                    log.error("Button [{}]: No code", b.getLabel());
                }
            }
        }
        log.debug("Stack after {}", stack.toString());

        notifyListeners();
    }

    public boolean hasDisplayValue() {
        return display.hasValue();
    }

    public String getDisplayString() {
        return display.toString();
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
