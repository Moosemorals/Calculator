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

import com.moosemorals.calculator.ui.UI;
import com.moosemorals.calculator.xml.ConfigFileParser;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start the GUI
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class Main {

    public static final String KEY_FRAME_WIDTH = "frame_width";
    public static final String KEY_FRAME_LEFT = "frame_left";
    public static final String KEY_FRAME_TOP = "frame_top";
    public static final String KEY_FRAME_KNOWN = "frame_bounds";
    public static final String KEY_FRAME_HEIGHT = "frame_height";

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Config config;

    public static void main(String[] args) throws IOException {

        Preferences prefs = Preferences.userNodeForPackage(Main.class);

        try {
            config = new ConfigFileParser().parse(Main.class.getResourceAsStream("/config.xml"));
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Can't read config.xml: " + ex.getMessage(), ex);
        }

        Engine engine = new Engine(config);
        engine.fillCache();
        final UI ui = new UI(prefs, config, engine);

        SwingUtilities.invokeLater(() -> {
            try {
                ui.build();
            } catch (IOException ex) {
                throw new RuntimeException("Can't build ui: " + ex.getMessage(), ex);
            }
        });

    }

}
