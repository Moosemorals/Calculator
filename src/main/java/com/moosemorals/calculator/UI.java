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
import java.awt.Font;
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

    private static final String[] LABLES = {
        Engine.ROOT, "7", "8", "9", Engine.PLUS,
        Engine.SWAP, "4", "5", "6", Engine.MINUS,
        Engine.DROP, "1", "2", "3", Engine.MULTIPLY,
        Engine.CLEAR, ".", "0", Engine.ENTER, Engine.DIVIDE
    };

    private static final char[] KEYS = {
        0, '7', '8', '9', '+',
        0, '4', '5', '6', '-',
        0, '1', '2', '3', '*',
        0, '.', '0', '\n', '/'
    };

    private final Logger log = LoggerFactory.getLogger(UI.class);
    private final Engine engine;

    public UI(Engine engine) {
        this.engine = engine;
    }

    public void build() {
        JPanel numbers = new JPanel();
        numbers.setLayout(new GridLayout(0, 5));

        Font font = new Font("Monospaced", Font.PLAIN, 16);

        for (int i = 0; i < LABLES.length; i += 1) {
            JButton button = new JButton();
            button.setText(LABLES[i]);
            button.setActionCommand(String.format("%s%s", CMD_PREFIX, LABLES[i]));
            button.addActionListener(this);
            button.setFont(font);
            button.setPreferredSize(new Dimension(48, 48));
            button.setFocusable(false);
            numbers.add(button);
        }

        EngineDisplay display = new EngineDisplay(engine);
        display.setFont(font);
        engine.addListDataListener(display);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    char key = e.getKeyChar();

                    for (int i = 0; i < KEYS.length; i += 1) {
                        if (KEYS[i] == key) {
                            engine.command(LABLES[i]);
                            return false;
                        }
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
