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
package com.moosemorals.calculator.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Button {

    private final Logger log = LoggerFactory.getLogger(Button.class);

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String name;
    private final String label;
    private final String code;
    private final char key;

    private Button(int x, int y, int width, int height, String name, String label, String code, char key) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.label = label;
        this.code = code;
        this.key = key;
    }

    /**
     * X location in button grid. Zero is left edge.
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Y location in button grid. Zero is top edge
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * Relative width
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Relative height.
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Human readable name for the button.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Label to draw on the button.
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Code that runs when the button is clicked.
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Keyboard key to operate the button. Zero means no keystroke.
     *
     * @return
     */
    public char getKey() {
        return key;
    }

    public static class Builder {

        private int x;
        private int y;
        private int width = 1;
        private int height = 1;
        private String name = "";
        private String label = "";
        private String code = null;
        private char key = 0;

        public Builder() {
            super();
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setKey(char key) {
            this.key = key;
            return this;
        }

        public Button build() {
            return new Button(x, y, width, height, name, label, code, key);
        }
    }

}
