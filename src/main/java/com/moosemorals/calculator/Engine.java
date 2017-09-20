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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public final class Engine {

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

        display = new Display();
        commandStack = new CommandStack();
        engineWatchers = new HashSet<>();
        scriptEngine = setupEngine();
        scriptCache = new HashMap<>();
        stack = new Stack();
    }

    void fillCache() {
        new Thread(() -> {
            log.debug("script cache load: start");
            for (int i = 0; i < config.getButtonCount(); i += 1) {
                Button b = config.getButton(i);
                synchronized (scriptCache) {
                    if (b.hasCode() && !scriptCache.containsKey(b.getLabel())) {
                        try {
                            JSObject func = (JSObject) scriptEngine.eval(b.getCode());
                            scriptCache.put(b.getLabel(), (ScriptObjectMirror) func.call(null, stack));
                        } catch (ScriptException ex) {
                            log.error("Script cache load: Button [{}]: Code error", b.getName(), ex);
                        }
                    }
                }
            }
            log.debug("Script cache load: complete");
        }).start();
    }

    private ScriptEngine setupEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        return engine;
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

    public void redo() {
        commandStack.redo();
        notifyListeners();
    }

    private Button getButton(String cmd) {
        for (int i = 0; i < config.getButtonCount(); i += 1) {
            Button b = config.getButton(i);

            if (cmd.equals(b.getName())) {
                return b;
            }
        }
        throw new RuntimeException("Unknown command [" + cmd + "]");
    }

    public void command(final String cmd) {
        boolean handled = false;

        Button b = getButton(cmd);

        switch (cmd) {
            case "Decimal point":
                if (display.hasDecimalPoint()) {
                    handled = true;
                    break;
                }
            // Intentional drop through
            case "Number 0":
            case "Number 1":
            case "Number 2":
            case "Number 3":
            case "Number 4":
            case "Number 5":
            case "Number 6":
            case "Number 7":
            case "Number 8":
            case "Number 9":
                commandStack.addCommand(new Command() {
                    double left;

                    @Override
                    public void execute() {
                        display.push(b.getLabel());
                    }

                    @Override
                    public void undo() {
                        display.pop();
                    }
                });
                handled = true;
                break;

            case "Clear":
                commandStack.clear();
                display.reset();
                stack.reset();
                handled = true;
                break;
            default:
                if (display.hasValue()) {
                    commandStack.addCommand(enterCommand);
                    if (cmd.equals("Enter")) {
                        handled = true;
                    }
                }
                break;
        }

        if (handled) {
            notifyListeners();
            return;
        }

        log.debug("Stack before {}", stack.toString());

        if (b.hasCode()) {
            synchronized (scriptCache) {
                try {
                    if (!scriptCache.containsKey(cmd)) {
                        JSObject func = (JSObject) scriptEngine.eval(b.getCode());
                        scriptCache.put(cmd, (ScriptObjectMirror) func.call(null, stack));
                    }

                    if (stack.getDepth() >= b.getIn()) {
                        commandStack.addCommand(new JsCommand(scriptCache.get(cmd)));
                    } else {
                        log.warn("Not enough stack for {}", b.getName());
                        stack.push(Double.NaN);
                    }
                } catch (ScriptException ex) {
                    log.error("Button [{}]: Code error", b.getName(), ex);
                    stack.push(Double.NaN);
                }
            }
        } else {
            log.error("Button [{}]: No code", b.getName());
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
