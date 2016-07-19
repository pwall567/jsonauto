/*
 * @(#) TestTwoWay.java
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

import java.util.Date;

import org.junit.Test;

/**
 * Test cases to test two-way serialization and deserialization.  Serialize an object,
 * deserialize it again and check that the result is identical to the original.
 */
public class TestTwoWay {

    // checklist:
    // int
    // long
    // boolean
    // short
    // char
    // double

    @Test
    public void testInt() {
        int a = 1;
        int b = JSONDeserializer.deserialize(Integer.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = 5327;
        b = JSONDeserializer.deserialize(Integer.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = -32777;
        b = JSONDeserializer.deserialize(Integer.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = 0;
        b = JSONDeserializer.deserialize(Integer.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testLong() {
        long a = 1;
        long b = JSONDeserializer.deserialize(Long.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = 5327L;
        b = JSONDeserializer.deserialize(Long.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = -327779875432000L;
        b = JSONDeserializer.deserialize(Long.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = 0;
        b = JSONDeserializer.deserialize(Long.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testBoolean() {
        boolean a = true;
        boolean b = JSONDeserializer.deserialize(Boolean.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = false;
        b = JSONDeserializer.deserialize(Boolean.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testShort() {
        short a = 1234;
        short b = JSONDeserializer.deserialize(Short.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = -5789;
        b = JSONDeserializer.deserialize(Short.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testChar() {
        char a = 'a';
        char b = JSONDeserializer.deserialize(Character.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = '\0';
        b = JSONDeserializer.deserialize(Character.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = '\u2020';
        b = JSONDeserializer.deserialize(Character.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testDouble() {
        double a = 1234.0;
        double b = JSONDeserializer.deserialize(Double.class, JSONSerializer.serialize(a));
        assertEquals(a, b, 1e-64);
        a = -2e50;
        b = JSONDeserializer.deserialize(Double.class, JSONSerializer.serialize(a));
        assertEquals(a, b, 1e-64);
        a = 0.0;
        b = JSONDeserializer.deserialize(Double.class, JSONSerializer.serialize(a));
        assertEquals(a, b, 1e-64);
    }

    @Test
    public void testEnum() {
        DummyEnum a = DummyEnum.ALPHA;
        DummyEnum b = JSONDeserializer.deserialize(DummyEnum.class,
                JSONSerializer.serialize(a));
        assertEquals(a, b);
        a = DummyEnum.GAMMA;
        b = JSONDeserializer.deserialize(DummyEnum.class,
                JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testDate() {
        Date a = new Date();
        Date b = JSONDeserializer.deserialize(Date.class, JSONSerializer.serialize(a));
        assertEquals(a, b);
    }

}
