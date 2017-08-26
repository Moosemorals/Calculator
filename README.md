A basic Java RPN desktop calculator.

# Usage
## Fetch

    git clone https://github.com/Moosemorals/Calculator.git && cd Calculator
## Build:

    mvn clean package
## Run

    java -jar target/Calculator-current.jar

# Features

  * RPN. I find it easier to think in RPN than infix, especially for
    complex calculations

  * Programmable. I don't mean that you can write macros for the 
    calculator, I mean that the buttons run JavaScript code
    against the stack.


# Buttons

Buttons run user defined JavaScript running on Java's Nashorn engine.

This means that buttons can (if you really want) perform actions well
outside the scope of basic maths. Be careful using buttons from other
people, they can download and install evil code. 

See the [example config][config] for an extended example, but buttons
are defined like:

    <button>
        <name>Add</name>
        <label>+</label>
        <key>+</key>
        <script>
            function (stack) {
                var left, right;
                return {
                    execute : function () {
                        left = stack.pop();
                        right = stack.pop();
                        stack.push(left + right);                    
                    },
                    undo : function () {
                        stack.pop();
                        stack.push(right);
                        stack.push(left);
                    },
                };
            }
        </script>
    </button>

Buttons should be a function that takes a single parameter (the current stack)
and returns a "Command" object that will be used to manipulate the stack. 

Using the "Command Pattern" means that the calculator has an undo stack that
can take you through calculations. (At some point, I'll add 'redo' in as well,
it won't be a big change).


[config]: src/main/resources/config.xml
