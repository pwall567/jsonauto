/*
 * @(#) DeserializeStringTest.java
 *
 * jsonauto JSON Auto-serialization Library
 * Copyright (c) 2016 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.auto;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test the {@link JSONDeserializer#deserializeString(Class, String)} function.
 */
public class DeserializeStringTest {

    @Test
    public void testString() {
        String result = "abcdef";
        assertEquals(result, JSONDeserializer.deserializeString(String.class, "abcdef"));
        result = "";
        assertEquals(result, JSONDeserializer.deserializeString(String.class, ""));
    }

    @Test
    public void testInteger() {
        Integer result = Integer.valueOf(27);
        assertEquals(result, JSONDeserializer.deserializeString(Integer.class, "27"));
        result = Integer.valueOf(0);
        assertEquals(result, JSONDeserializer.deserializeString(Integer.class, "0"));
        result = Integer.valueOf(-1);
        assertEquals(result, JSONDeserializer.deserializeString(Integer.class, "-1"));
    }

}
