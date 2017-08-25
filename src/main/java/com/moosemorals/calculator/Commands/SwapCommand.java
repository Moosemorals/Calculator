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
package com.moosemorals.calculator.Commands;

import com.moosemorals.calculator.Command;
import com.moosemorals.calculator.Stack;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class SwapCommand implements Command {

    final Stack stack;
    private double left;
    private double right;

    public SwapCommand(Stack stack) {
        this.stack = stack;
    }

    @Override
    public void execute() {
        left = stack.pop();
        right = stack.pop();
        stack.push(left * right);
    }

    @Override
    public void undo() {
        stack.pop();
        stack.push(right);
        stack.push(left);
    }
}