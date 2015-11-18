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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class EngineDisplay extends JComponent implements ListDataListener {

    private static final int BORDER = 5;

    private final Logger log = LoggerFactory.getLogger(EngineDisplay.class);
    private final Engine engine;
    private final DecimalFormat df;

    public EngineDisplay(Engine engine) {
        this.engine = engine;
        df = new DecimalFormat("#,##0.0#######");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(240, 18 * 6);
    }

    @Override
    protected void paintComponent(Graphics g) {

        FontMetrics fm = g.getFontMetrics();

        if (isOpaque()) { //paint background
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        for (int i = 0; i < engine.getDepth(); i += 1) {
            String text = df.format(engine.peek(i));

            int x = getWidth() - fm.stringWidth(text) - BORDER;
            int y = getHeight() - (i * fm.getHeight()) - BORDER;

            g.drawString(text, x, y);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        repaint();
    }

}