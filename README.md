A basic Java RPN calculator.

# Plan

I want to be able to easily customise the calculator, without needing to recompile.
To this end, I want to write a fairly simple programming language, so that I 
can define the actions of keys in a config file.

# Progress

Basic calculator works.

Configurable buttons work. See [src/main/resources/config.xml](config.xml)
for a longer example, but buttons can be defined something like:

    <button x="2" y="4">
        <name>Sign change</name>
        <label>±</label>
        <key>s</key>
    </button>

and the button will be drawn at `(2, 4)`, the "s" key will trigger the action,
the label will show "±". 

The next step is to add `<code>` tags to the definition, so that

    <code>0 -</code>

works.

I've got the rough shape of a solution in my head (based on a [virtual stack
computer](http://users.ece.cmu.edu/~koopman/stack_computers/sec3_2.html) and 
programming in Forth) but there are still bits I'm working out.

Ah, well. It's a hobby, I guess.

