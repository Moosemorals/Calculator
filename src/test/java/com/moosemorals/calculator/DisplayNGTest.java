/*
 * The MIT License
 *
 * Copyright 2017 Osric Wilkinson (osric@fluffypeople.com).
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

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Osric Wilkinson (osric@fluffypeople.com)
 */
public class DisplayNGTest {
        
    @Test
    public void test_setValue1() {
        Display d = new Display();
        d.setValue(1);
        
        assertEquals(d.getValue(), 1.0);
    }
    
    @Test
    public void test_setValue2() {
        Display d = new Display();
        d.setValue(-1);
        
        assertEquals(d.getValue(), -1.0);
    }
    
    @Test
    public void test_setValue3() {
        Display d = new Display();
        d.setValue(10);
        
        assertEquals(d.getValue(), 10.0);
    }
    
    @Test
    public void test_setValue4() {
        Display d = new Display();
        d.setValue(0.4);
        
        assertEquals(d.getValue(), 0.4);
    }
    
    @Test
    public void test_setValue5() {
        Display d = new Display();
        d.setValue(1.4);
        
        assertEquals(d.getValue(), 1.4);
    }
    
}
