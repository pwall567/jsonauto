/*
 * @(#) JSONDeserializerTest.java
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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONFloat;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;

import org.junit.Test;

/**
 * Test class for {@link JSONDeserializer}.
 *
 * @author Peter Wall
 */
public class JSONDeserializerTest {

    @Test
    public void testNull() {
        assertEquals(null, JSONDeserializer.deserialize(String.class, null));
    }

    @Test
    public void testString() {
        JSONString json = new JSONString("abc");
        assertEquals("abc", JSONDeserializer.deserialize(String.class, json));
    }

    @Test
    public void testStringBuilder() {
        JSONString json = new JSONString("abc");
        StringBuilder sb = JSONDeserializer.deserialize(StringBuilder.class, json);
        assertEquals("abc", sb.toString());
    }

    @Test
    public void testStringBuffer() {
        JSONString json = new JSONString("abc");
        StringBuffer sb = JSONDeserializer.deserialize(StringBuffer.class, json);
        assertEquals("abc", sb.toString());
    }

    @Test
    public void testInteger() {
        JSONInteger json = new JSONInteger(35);
        assertEquals(Integer.valueOf(35), JSONDeserializer.deserialize(Integer.class, json));
        json = new JSONInteger(-543);
        assertEquals(Integer.valueOf(-543), JSONDeserializer.deserialize(Integer.class, json));
    }

    @Test
    public void testLong() {
        JSONLong json = new JSONLong(5372964L);
        assertEquals(Long.valueOf(5372964L), JSONDeserializer.deserialize(Long.class, json));
    }

    @Test
    public void testDouble() {
        JSONDouble json = new JSONDouble(123.456);
        assertEquals(Double.valueOf(123.456), JSONDeserializer.deserialize(Double.class, json));
    }

    @Test
    public void testFloat() {
        JSONFloat json = new JSONFloat(0.456F);
        assertEquals(Float.valueOf(0.456F), JSONDeserializer.deserialize(Float.class, json));
    }

    @Test
    public void testShort() {
        JSONInteger json = new JSONInteger(87);
        assertEquals(Short.valueOf((short)87), JSONDeserializer.deserialize(Short.class, json));
    }

    @Test
    public void testByte() {
        JSONInteger json = new JSONInteger(123);
        assertEquals(Byte.valueOf((byte)123), JSONDeserializer.deserialize(Byte.class, json));
    }

    @Test
    public void testBoolean() {
        JSONBoolean json = JSONBoolean.FALSE;
        assertEquals(Boolean.FALSE, JSONDeserializer.deserialize(Boolean.class, json));

        json = JSONBoolean.TRUE;
        assertEquals(Boolean.TRUE, JSONDeserializer.deserialize(Boolean.class, json));
    }

    @Test
    public void testCharacter() {
        JSONString json = new JSONString("Q");
        assertEquals(Character.valueOf('Q'),
                JSONDeserializer.deserialize(Character.class, json));
    }

    @Test
    public void testList() {
        JSONArray json = JSONArray.create().addValue("ABC").addValue("def").addValue("!");
        List<String> expected = new ArrayList<>();
        expected.add("ABC");
        expected.add("def");
        expected.add("!");
        Type[] types = new Type[] { String.class };
        assertEquals(expected, JSONDeserializer.deserialize(List.class, types, json));
    }

    @Test
    public void testArrayList() {
        JSONArray json = JSONArray.create().addValue("ABC").addValue("def").addValue("!");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("ABC");
        expected.add("def");
        expected.add("!");
        Type[] types = new Type[] { String.class };
        assertEquals(expected, JSONDeserializer.deserialize(ArrayList.class, types, json));
    }

    private List<List<String>> listListString;

    @Test
    public void testListList() throws Exception {
        JSONArray json1 = JSONArray.create().addValue("ABC").addValue("def").addValue("!");
        JSONArray json2 = JSONArray.create().addValue("xyz").addValue("1234");
        JSONArray json = JSONArray.create().addJSON(json1).addJSON(json2);
        listListString = new ArrayList<>();
        List<String> listString = new ArrayList<>();
        listString.add("ABC");
        listString.add("def");
        listString.add("!");
        listListString.add(listString);
        listString = new ArrayList<>();
        listString.add("xyz");
        listString.add("1234");
        listListString.add(listString);
        Field expectedField = getClass().getDeclaredField("listListString");
        Class<?> expectedClass = expectedField.getType();
        Type genericType = expectedField.getGenericType();
        Type[] typeArgs = genericType instanceof ParameterizedType ?
                ((ParameterizedType)genericType).getActualTypeArguments() : null;
        assertEquals(listListString,
                JSONDeserializer.deserialize(expectedClass, typeArgs, json));
    }

    @Test
    public void testSet() {
        JSONArray json = JSONArray.create().addValue("ABC").addValue("def").addValue("!");
        Set<String> expected = new HashSet<>();
        expected.add("ABC");
        expected.add("def");
        expected.add("!");
        Type[] types = new Type[] { String.class };
        assertEquals(expected, JSONDeserializer.deserialize(Set.class, types, json));
    }

    @Test
    public void testHashSet() {
        JSONArray json = JSONArray.create().addValue("ABC").addValue("def").addValue("!");
        HashSet<String> expected = new HashSet<>();
        expected.add("ABC");
        expected.add("def");
        expected.add("!");
        Type[] types = new Type[] { String.class };
        assertEquals(expected, JSONDeserializer.deserialize(HashSet.class, types, json));
    }

    @Test
    public void testMapStringString() {
        JSONObject json = JSONObject.create().putValue("key1", "abcdef");
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "abcdef");
        Type[] types = new Type[] { String.class, String.class };
        assertEquals(expected, JSONDeserializer.deserialize(HashMap.class, types, json));
    }

    @Test
    public void testArrayInt() {
        JSONArray json = JSONArray.create().addValue(523).addValue(-1).addValue(39222);
        int[] expected = new int[] { 523, -1, 39222 };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayLong() {
        JSONArray json = JSONArray.create().addValue(77713334523L).addValue(-1548269337749L);
        long[] expected = new long[] { 77713334523L, -1548269337749L };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayDouble() {
        JSONArray json = JSONArray.create().addValue(0.0).addValue(-23.567).addValue(1e-20);
        double[] expected = new double[] { 0.0, -23.567, 1e-20 };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayFloat() {
        JSONArray json = JSONArray.create().addValue(25F).addValue(-1.006F).addValue(1e-6F);
        float[] expected = new float[] { 25F, -1.006F, 1e-6F };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayShort() {
        JSONArray json = JSONArray.create().addValue(123).addValue(-456).addValue(789);
        short[] expected = new short[] { 123, -456, 789 };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayByte() {
        JSONArray json = JSONArray.create().addValue(123).addValue(-25).addValue(0);
        byte[] expected = new byte[] { 123, -25, 0 };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayChar() {
        JSONString json = new JSONString("Abc");
        char[] expected = new char[] { 'A', 'b', 'c' };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayBoolean() {
        JSONArray json = JSONArray.create().addValue(true).addValue(false);
        boolean[] expected = new boolean[] { true, false };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayInteger() {
        JSONArray json = JSONArray.create().addValue(523).addValue(-1).addValue(39222);
        Integer[] expected = new Integer[] { 523, -1, 39222 };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testArrayLONG() {
        JSONArray json = JSONArray.create().addValue(77713334523L).addValue(-1548269337749L);
        Long[] expected = new Long[] { 77713334523L, -1548269337749L };
        assertTrue(Arrays.equals(expected,
                JSONDeserializer.deserialize(expected.getClass(), json)));
    }

    @Test
    public void testFromJSON() {
        JSONObject json = JSONObject.create().putValue("dec", "17").putValue("hex", "11");
        DummyObject5 expected = new DummyObject5();
        expected.setInt1(17);
        assertEquals(expected, JSONDeserializer.deserialize(DummyObject5.class, json));
    }

    @Test
    public void testIncludedJSON() {
        JSONValue jsonValue = new JSONDouble(0.1);
        assertEquals(jsonValue, JSONDeserializer.deserialize(JSONValue.class, jsonValue));
    }

    @Test
    public void testEnum() {
        JSONString json = new JSONString("ALPHA");
        DummyEnum expected = DummyEnum.ALPHA;
        assertEquals(expected, JSONDeserializer.deserialize(DummyEnum.class, json));
        json = new JSONString("BETA");
        expected = DummyEnum.BETA;
        assertEquals(expected, JSONDeserializer.deserialize(DummyEnum.class, json));
    }

    @Test
    public void testObject() {
        JSONObject json = JSONObject.create().putValue("string1", "abc");
        DummyObject expected = new DummyObject();
        expected.setString1("abc");
        assertEquals(expected, JSONDeserializer.deserialize(DummyObject.class, json));
    }

    @Test
    public void testObject2() {
        JSONObject json = JSONObject.create().putValue("string1", "abc").putValue("int1", 27);
        DummyObject2 expected = new DummyObject2();
        expected.setString1("abc");
        expected.setInt1(27);
        assertEquals(expected, JSONDeserializer.deserialize(DummyObject2.class, json));
    }

}
