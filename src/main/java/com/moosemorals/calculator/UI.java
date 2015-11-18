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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

    private static final String[] BUTTONS
            = {
                "7", "8", "9", "+",
                "4", "5", "6", "-",
                "1", "2", "3", "*",
                ".", "0", Engine.ENTER, "/"
            };

    private final Logger log = LoggerFactory.getLogger(UI.class);
    private final Engine engine;

    public UI(Engine engine) {
        this.engine = engine;
    }

    public void build() {
        JPanel numbers = new JPanel();
        numbers.setLayout(new GridLayout(0, 4));

        for (int i = 0; i < BUTTONS.length; i += 1) {
            JButton button = new JButton();
            button.setText(BUTTONS[i]);
            button.setActionCommand(String.format("%s%s", CMD_PREFIX, BUTTONS[i]));
            button.addActionListener(this);
            numbers.add(button);
        }

        EngineDisplay display = new EngineDisplay(engine);
        engine.addListDataListener(display);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    String key = new String(new char[]{e.getKeyChar()});

                    // This won't catch the funky Engine.ENTER button
                    for (String cmd : BUTTONS) {
                        if (cmd.equals(key)) {
                            engine.click(cmd);
                            return false;
                        }
                    }
                    if (key.equals("\n")) {
                        engine.click(Engine.ENTER);
                    }
                }

                return false;
            }
        });

        JFrame main = new JFrame("Calculator");

        //    main.setSize(480, 900);
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.getContentPane().setLayout(new BoxLayout(main.getContentPane(), BoxLayout.X_AXIS));
        main.add(display, BorderLayout.CENTER);
        main.add(numbers, BorderLayout.SOUTH);
        main.pack();
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

        engine.click(cmd);
    }
}
