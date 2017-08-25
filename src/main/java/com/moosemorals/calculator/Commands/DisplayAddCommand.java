package com.moosemorals.calculator.Commands;

import com.moosemorals.calculator.Command;

import java.util.Deque;

public class DisplayAddCommand implements Command {

    private final Deque<String> display;
    public DisplayAddCommand(Deque<String> display) {
        this.display = display;
    }

    @Override
    public void execute() {

    }

    @Override
    public void undo() {

    }
}
