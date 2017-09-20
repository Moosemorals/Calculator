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

import com.moosemorals.calculator.Button;
import com.moosemorals.calculator.Config;
import com.moosemorals.calculator.Engine;
import com.moosemorals.calculator.Main;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Do stuff with clicks.
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class UI implements ActionListener {

    private static final String CMD_PREFIX = "BTN";
    static final String DISPLAY_PATTERN = "#,##0.######";

    private final Logger log = LoggerFactory.getLogger(UI.class);
    private final DecimalFormat df;
    private final Engine engine;
    private final Clippy clippy;
    private final Config config;
    private final Preferences prefs;

    public UI(Preferences prefs, Config config, Engine engine) {
        this.prefs = prefs;
        this.config = config;
        this.engine = engine;
        clippy = new Clippy();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setNaN("Err");
        df = new DecimalFormat(DISPLAY_PATTERN, symbols);
    }

    public void build() {
        JPanel numbers = new JPanel();
        numbers.setLayout(new GridBagLayout());

        Font buttonFont = new Font("Monospaced", Font.BOLD, 16);
        for (int i = 0; i < config.getButtonCount(); i += 1) {
            Button button = config.getButton(i);
            JButton b = new JButton();

            GridBagConstraints c = new GridBagConstraints();

            c.gridx = button.getX();
            c.gridy = button.getY();
            c.gridheight = button.getHeight();
            c.gridwidth = button.getWidth();

            b.setText(button.getLabel());
            b.setFont(buttonFont);
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setActionCommand(String.format("%s%s", CMD_PREFIX, button.getName()));
            b.addActionListener(this);
            b.setPreferredSize(new Dimension(config.getSize() * button.getWidth(), config.getSize() * button.getHeight()));
            b.setFocusable(false);
            numbers.add(b, c);
        }

        EngineDisplay display = new EngineDisplay(config, engine);
        engine.addEngineWatcher(display);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            if (e.getID() == KeyEvent.KEY_TYPED) {

                char key = e.getKeyChar();

                for (int i = 0; i < config.getButtonCount(); i += 1) {
                    Button b = config.getButton(i);
                    if (b.getKey() == key) {
                        engine.command(b.getName());
                        return false;
                    }
                }

                if (key == 'x' || key == 'X' || key == 'q' || key == 'Q') {
                    System.exit(0);
                }
            } else if (e.getID() == KeyEvent.KEY_PRESSED) {

                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                    String text = df.format(engine.getElementAt(0));
                    clippy.sendToClipboard(text);
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                    String text = clippy.getFromClipboard();
                    if (text != null) {
                        try {
                            double value = Double.parseDouble(text);
                            engine.push(value);
                        } catch (NumberFormatException ex) {
                            // ignored, but no point trying to add the number
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    engine.undo();
                } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                    engine.redo();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    engine.undo();
                }
            }
            return false;
        });

        JFrame window = new JFrame("Calculator");

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                saveBounds(e.getComponent().getBounds());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                saveBounds(e.getComponent().getBounds());
            }

            private void saveBounds(Rectangle bounds) {
                prefs.putInt(Main.KEY_FRAME_TOP, bounds.y);
                prefs.putInt(Main.KEY_FRAME_LEFT, bounds.x);
                prefs.putInt(Main.KEY_FRAME_WIDTH, bounds.width);
                prefs.putInt(Main.KEY_FRAME_HEIGHT, bounds.height);
            }
        });

        window.setBounds(new Rectangle(
                prefs.getInt(Main.KEY_FRAME_LEFT, 0),
                prefs.getInt(Main.KEY_FRAME_TOP, 0),
                prefs.getInt(Main.KEY_FRAME_WIDTH, 640),
                prefs.getInt(Main.KEY_FRAME_HEIGHT, 480)
        ));
                
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
        window.add(display);
        window.add(numbers);
        window.pack();
        window.setResizable(false);
        window.setVisible(true);
    }

    @Override

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd == null) {
            log.warn("Action with no command");
            return;
        } else if (!cmd.startsWith(CMD_PREFIX)) {
            log.warn("Action doesn't have right prefix: {}", cmd);
            return;
        }

        cmd = cmd.substring(CMD_PREFIX.length());

        engine.command(cmd);
    }
}
