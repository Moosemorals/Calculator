/*
 * The MIT License
 *
 * Copyright 2017 Osric Wilkinson <osric@fluffypeople.com>.
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

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
 class CommandStack {

    private final Logger log = LoggerFactory.getLogger(CommandStack.class);
    private final List<Command> stack;
    private int current = 0;

     CommandStack() {
        stack = new LinkedList<>();
    }

     void addCommand(Command x) {
        x.execute();
        while (stack.size() > current) {
            stack.remove(stack.size() - 1);
        }
        stack.add(current++, x);
    }

     void undo() {
        if (current >= 0) {
            stack.get(--current).undo();
        }
    }

    void redo() {
         if (current < stack.size()) {
             stack.get(current++).execute();
         }
    }

    void clear() {
         stack.clear();
    }

    protected List<Command> dump() {
         return Collections.unmodifiableList(stack);
    }
}
