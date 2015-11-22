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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
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

    private final Logger log = LoggerFactory.getLogger(UI.class);
    private final Engine engine;
    private final Clippy clippy;
    private final Config config;

    public UI(Config config, Engine engine) {
        this.config = config;
        this.engine = engine;
        clippy = new Clippy();
    }

    public void build() throws IOException {
        JPanel numbers = new JPanel();
        numbers.setLayout(new GridLayout(0, config.getCols()));

        //       Font font = new Font("Monospaced", Font.PLAIN, 12);
        for (int i = 0; i < config.getButtonCount(); i += 1) {
            JButton button = new JButton();
            String label = config.getButton(i).getLabel();
            button.setText(label);
            button.setActionCommand(String.format("%s%s", CMD_PREFIX, label));
            button.addActionListener(this);
            button.setPreferredSize(new Dimension(config.getSize(), config.getSize()));
            button.setFocusable(false);
            numbers.add(button);
        }

        EngineDisplay display = new EngineDisplay(config, engine);
        //      display.setFont(font);
        engine.addEngineWatcher(display);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {

                    char key = e.getKeyChar();

                    for (int i = 0; i < config.getButtonCount(); i += 1) {
                        Button b = config.getButton(i);
                        if (b.getKey() == key) {
                            engine.command(b.getLabel());
                            return false;
                        }
                    }

                    if (key == 'x' || key == 'X' || key == 'q' || key == 'Q') {
                        System.exit(0);
                    }
                } else if (e.getID() == KeyEvent.KEY_PRESSED) {

                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                        String text = engine.getElementAt(0);
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
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        log.debug("Trying to delete, eh?");
                    }
                }
                return false;
            }
        });

        JFrame main = new JFrame("Calculator");

        //    main.setSize(480, 900);
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.getContentPane().setLayout(new BoxLayout(main.getContentPane(), BoxLayout.Y_AXIS));
        main.add(display);
        main.add(numbers);
        main.pack();
        main.setResizable(false);
        main.setVisible(true);
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
