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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Config {

    private final Logger log = LoggerFactory.getLogger(Config.class);
    private final List<Button> buttons;
    private final int cols;
    private final int rows;
    private final int size;

    private Config(int cols, int rows, int size, List<Button> buttons) {
        this.cols = cols;
        this.rows = rows;
        this.size = size;        
        this.buttons = buttons;
    }

    public int getCols() {
        return cols;
    }
    
    public int getRows() {
        return rows;
    }

    public int getSize() {
        return size;
    }

    public int getButtonCount() {
        return buttons.size();
    }

    public Button getButton(int index) {
        return buttons.get(index);
    }

    public static class Builder {

        private int cols = 0;
        private int rows = 0;
        private int size = 48;
        private final List<Button> buttons = new ArrayList<>();

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder addButtons(List<Button> buttons) {
            this.buttons.addAll(buttons);
            return this;
        }

        public Config build() {

            cols = 0;
            rows = 0;

            buttons.forEach((Button b) -> {
                if (b.getX() + b.getWidth() > cols) {
                    cols = b.getX() + b.getWidth();
                }
                if (b.getY() + b.getHeight() > rows) {
                    rows = b.getY() + b.getHeight();
                }
            });

            // Sort buttons so that the button in the top-left comes first,
            // then top-right, then bottom-left, finaly bottom right.
            Collections.sort(buttons, (Button left, Button right) -> {
                if (left.getY() == right.getY()) {
                    return (left.getX() - right.getX());
                } else {
                    return left.getY() - right.getY();
                }
            });

            return new Config(cols, rows, size, buttons);
        }

    }

}
