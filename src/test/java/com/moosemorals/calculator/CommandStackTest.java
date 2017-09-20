package com.moosemorals.calculator;

import org.mockito.InOrder;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.testng.Assert.*;

public class CommandStackTest {

    @Test
    public void test_addOne() throws Exception {
        CommandStack stack = new CommandStack();

        Command one = mock(Command.class);

        stack.addCommand(one);

        List<Command> result = stack.dump();

        verify(one).execute();
        assertEquals(one, result.get(0));
    }


    @Test
    public void test_addTwo() throws Exception {
        CommandStack stack = new CommandStack();

        Command one = mock(Command.class);
        Command two = mock(Command.class);

        stack.addCommand(one);
        stack.addCommand(two);

        List<Command> result = stack.dump();

        verify(one).execute();
        verify(two).execute();
        assertEquals(one, result.get(0));
        assertEquals(two, result.get(1));
    }

    @Test
    public void test_undo() throws Exception {
        CommandStack stack = new CommandStack();

        Command one = mock(Command.class);

        stack.addCommand(one);
        stack.undo();

        List<Command> result = stack.dump();

        InOrder inOrder = inOrder(one);

        inOrder.verify(one).execute();
        inOrder.verify(one).undo();
        assertEquals(one, result.get(0));
    }

    @Test
    public void test_redo() throws Exception {
        CommandStack stack = new CommandStack();

        Command one = mock(Command.class);

        stack.addCommand(one);
        stack.undo();
        stack.redo();

        List<Command> result = stack.dump();

        InOrder inOrder = inOrder(one);

        inOrder.verify(one).execute();
        inOrder.verify(one).undo();
        inOrder.verify(one).execute();
        assertEquals(one, result.get(0));
    }

    @Test
    public void test_complex() throws Exception {
        CommandStack stack = new CommandStack();

        Command one = mock(Command.class);
        Command two = mock(Command.class);
        Command three = mock(Command.class);

        stack.addCommand(one);
        stack.addCommand(two);
        stack.undo();
        stack.addCommand(three);

        List<Command> result = stack.dump();

        assertEquals(one, result.get(0));
        assertEquals(three, result.get(1));
    }



}
