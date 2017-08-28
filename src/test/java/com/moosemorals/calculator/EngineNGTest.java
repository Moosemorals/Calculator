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

import com.moosemorals.calculator.xml.ConfigFileParser;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class EngineNGTest {

    private static final double FUDGE = 0.0001;

    private Config config;
    
    @BeforeClass
    public void setup() throws Exception {        
        config = new ConfigFileParser().parse(getClass().getResourceAsStream("/config.xml"));
    }
    
    @Test
    public void test_click1() {
        Engine e = new Engine(config);
        e.command("Number 1");
        assertEquals(e.peek(), 1.0, FUDGE);
    }

    @Test
    public void test_click2() {
        Engine e = new Engine(config);
        e.command("Number 1");
        e.command("Decimal point");
        e.command("Number 2");
        assertEquals(e.peek(), 1.2, FUDGE);
        assertEquals(e.getDepth(), 0);
    }


}
