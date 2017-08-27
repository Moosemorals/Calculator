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

import com.moosemorals.calculator.Config;
import com.moosemorals.calculator.Engine;
import com.moosemorals.calculator.EngineWatcher;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class EngineDisplay extends JComponent implements EngineWatcher {

    private static final int BORDER = 5;
    private static final int FONT_SIZE = 24;

    private final Logger log = LoggerFactory.getLogger(EngineDisplay.class);
    private final DecimalFormat df;
    private final Engine engine;
    private final Config config;

    public EngineDisplay(Config config, Engine engine) {
        this.engine = engine;
        this.config = config;
        this.setFont(new Font("Monospaced", Font.BOLD, FONT_SIZE));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setNaN("Err");
        df = new DecimalFormat(UI.DISPLAY_PATTERN, symbols);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(config.getCols() * config.getSize(), FONT_SIZE * 6);
    }

    private void displayLine(Graphics g, FontMetrics fm, int index, String text) {
        int x = getWidth() - fm.stringWidth(text) - BORDER;
        int y = getHeight() - (index * fm.getHeight()) - BORDER;

        g.drawString(text, x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {

        FontMetrics fm = g.getFontMetrics();

        if (isOpaque()) { //paint background
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        boolean hasDisplay = engine.hasDisplayValue() || engine.getDepth() == 0;
        
        if (hasDisplay) {
            displayLine(g, fm, 0, engine.getDisplayString());
        }

        for (int i = 0; i < engine.getDepth(); i += 1) {
            String text = df.format(engine.getElementAt(i));
            displayLine(g, fm, hasDisplay ? i + 1 : i, text);
        }
    }

    @Override
    public void onEngineChanged() {
        repaint();
    }

}
