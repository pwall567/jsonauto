/*
 * @(#) JSONSerializerTest.java
 *
 * jsonauto JSON Auto-serialization Library
 * Copyright (c) 2015 Peter Wall
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
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

import static org.junit.Assert.*;

/**
 * Test class for {@link JSONSerializer}.
 *
 * @author Peter Wall
 */
public class JSONSerializerTest {

    @Test
    public void testNull() {
        assertEquals(null, JSONSerializer.serialize(null));
    }

    @Test
    public void testStrings() {
        String str = "test1";
        JSONString jsonString = new JSONString("test1");
        assertEquals(jsonString, JSONSerializer.serialize(str));

        StringBuilder sb = new StringBuilder();
        sb.append('a').append('b').append('c');
        jsonString = new JSONString("abc");
        assertEquals(jsonString, JSONSerializer.serialize(sb));

        StringBuffer sbuf = new StringBuffer();
        sbuf.append(123);
        jsonString = new JSONString("123");
        assertEquals(jsonString, JSONSerializer.serialize(sbuf));
    }

    @Test
    public void testBoolean() {
        boolean bool1 = true;
        JSONBoolean jsonBoolean = JSONBoolean.TRUE;
        assertEquals(jsonBoolean, JSONSerializer.serialize(bool1));

        bool1 = false;
        jsonBoolean = JSONBoolean.FALSE;
        assertEquals(jsonBoolean, JSONSerializer.serialize(bool1));

        Boolean bool2 = Boolean.FALSE;
        jsonBoolean = JSONBoolean.FALSE;
        assertEquals(jsonBoolean, JSONSerializer.serialize(bool2));
    }

    @Test
    public void testInteger() {
        int int1 = 123;
        JSONInteger jsonInteger = JSONInteger.valueOf(int1);
        assertEquals(jsonInteger, JSONSerializer.serialize(int1));

        int1 = -567;
        jsonInteger = JSONInteger.valueOf(int1);
        assertEquals(jsonInteger, JSONSerializer.serialize(int1));

        int1 = 0;
        jsonInteger = JSONInteger.valueOf(int1);
        assertEquals(jsonInteger, JSONSerializer.serialize(int1));

        int1 = Integer.MAX_VALUE;
        jsonInteger = JSONInteger.valueOf(int1);
        assertEquals(jsonInteger, JSONSerializer.serialize(int1));

        int1 = Integer.MIN_VALUE;
        jsonInteger = JSONInteger.valueOf(int1);
        assertEquals(jsonInteger, JSONSerializer.serialize(int1));

        Integer int2 = 8888;
        jsonInteger = JSONInteger.valueOf(int2);
        assertEquals(jsonInteger, JSONSerializer.serialize(int2));
    }

    @Test
    public void testLong() {
        long long1 = 57L;
        JSONLong jsonLong = JSONLong.valueOf(long1);
        assertEquals(jsonLong, JSONSerializer.serialize(long1));

        long1 = -112233445566778899L;
        jsonLong = JSONLong.valueOf(long1);
        assertEquals(jsonLong, JSONSerializer.serialize(long1));

        long1 = 0L;
        jsonLong = JSONLong.valueOf(long1);
        assertEquals(jsonLong, JSONSerializer.serialize(long1));

        long1 = Long.MAX_VALUE;
        jsonLong = JSONLong.valueOf(long1);
        assertEquals(jsonLong, JSONSerializer.serialize(long1));

        long1 = Long.MIN_VALUE;
        jsonLong = JSONLong.valueOf(long1);
        assertEquals(jsonLong, JSONSerializer.serialize(long1));

        Long long2 = 1234567812345678L;
        jsonLong = JSONLong.valueOf(long2);
        assertEquals(jsonLong, JSONSerializer.serialize(long2));
    }

    @Test
    public void testShort() {
        short short1 = 2345;
        JSONInteger jsonInteger = JSONInteger.valueOf(short1);
        assertEquals(jsonInteger, JSONSerializer.serialize(short1));

        short1 = (short)-54321;
        jsonInteger = JSONInteger.valueOf(short1);
        assertEquals(jsonInteger, JSONSerializer.serialize(short1));

        short1 = 0;
        jsonInteger = JSONInteger.valueOf(short1);
        assertEquals(jsonInteger, JSONSerializer.serialize(short1));

        short1 = Short.MAX_VALUE;
        jsonInteger = JSONInteger.valueOf(short1);
        assertEquals(jsonInteger, JSONSerializer.serialize(short1));

        short1 = Short.MIN_VALUE;
        jsonInteger = JSONInteger.valueOf(short1);
        assertEquals(jsonInteger, JSONSerializer.serialize(short1));

        Short short2 = 597;
        jsonInteger = JSONInteger.valueOf(short2);
        assertEquals(jsonInteger, JSONSerializer.serialize(short2));
    }

    @Test
    public void testByte() {
        byte byte1 = 123;
        JSONInteger jsonInteger = JSONInteger.valueOf(byte1);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte1));

        byte1 = -88;
        jsonInteger = JSONInteger.valueOf(byte1);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte1));

        byte1 = 0;
        jsonInteger = JSONInteger.valueOf(byte1);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte1));

        byte1 = Byte.MAX_VALUE;
        jsonInteger = JSONInteger.valueOf(byte1);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte1));

        byte1 = Byte.MIN_VALUE;
        jsonInteger = JSONInteger.valueOf(byte1);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte1));

        Byte byte2 = 0x1F;
        jsonInteger = JSONInteger.valueOf(byte2);
        assertEquals(jsonInteger, JSONSerializer.serialize(byte2));
    }

    @Test
    public void testDouble() {
        double double1 = 1234.567;
        JSONDouble jsonDouble = JSONDouble.valueOf(double1);
        assertEquals(jsonDouble, JSONSerializer.serialize(double1));

        double1 = -876.2222E16;
        jsonDouble = JSONDouble.valueOf(double1);
        assertEquals(jsonDouble, JSONSerializer.serialize(double1));

        double1 = 0.0;
        jsonDouble = JSONDouble.valueOf(double1);
        assertEquals(jsonDouble, JSONSerializer.serialize(double1));
    }

    @Test
    public void testFloat() {
        float float1 = 1234.567F;
        JSONFloat jsonFloat = JSONFloat.valueOf(float1);
        assertEquals(jsonFloat, JSONSerializer.serialize(float1));

        float1 = -1234E-40F;
        jsonFloat = JSONFloat.valueOf(float1);
        assertEquals(jsonFloat, JSONSerializer.serialize(float1));

        float1 = 0.0F;
        jsonFloat = JSONFloat.valueOf(float1);
        assertEquals(jsonFloat, JSONSerializer.serialize(float1));
    }

    @Test
    public void testChar() {
        char char1 = 'q';
        JSONString jsonString = new JSONString("q");
        assertEquals(jsonString, JSONSerializer.serialize(char1));

        char1 = '\u1234';
        jsonString = new JSONString("\u1234");
        assertEquals(jsonString, JSONSerializer.serialize(char1));
    }

    @Test
    public void testArrayString() {
        String[] array1 = { "one", "two", "three", "four" };
        JSONArray jsonArray = JSONArray.create().addValue("one").addValue("two").
                addValue("three").addValue("four");
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1[2] = null;
        jsonArray = JSONArray.create().addValue("one").addValue("two").addNull().
                addValue("four");
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new String[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new String[1];
        array1[0] = "a very much longer string just for testing purposes";
        jsonArray = JSONArray.create().addValue(array1[0]);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayInt() {
        int[] array1 = { 1, 2, 3 };
        JSONArray jsonArray = JSONArray.create().addValue(1).addValue(2).addValue(3);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new int[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayIntObject() {
        Integer[] array1 = { 1, 2, 3, null, -5 };
        JSONArray jsonArray = JSONArray.create().addValue(1).addValue(2).addValue(3).addNull().
                addValue(-5);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new Integer[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayLong() {
        long[] array1 = { 1L, 2L, 112233445566778899L, 0L };
        JSONArray jsonArray = JSONArray.create().addValue(1L).addValue(2L).
                addValue(112233445566778899L).addValue(0L);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new long[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayBoolean() {
        boolean[] array1 = { true, false, true };
        JSONArray jsonArray = JSONArray.create().addValue(true).addValue(false).addValue(true);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new boolean[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayShort() {
        short[] array1 = { 123, -456, 0 };
        JSONArray jsonArray = JSONArray.create().addValue(123).addValue(-456).addValue(0);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new short[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayDouble() {
        double[] array1 = { 123E32, -456.789, 0.00005 };
        JSONArray jsonArray = JSONArray.create().addValue(123E32).addValue(-456.789).
                addValue(0.00005);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new double[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayFloat() {
        float[] array1 = { 123E32F, -456.789F, 0.00005F };
        JSONArray jsonArray = JSONArray.create().addValue(123E32F).addValue(-456.789F).
                addValue(0.00005F);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new float[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayByte() {
        byte[] array1 = { 123, -27, 0, 0x1F, 0x2F };
        JSONArray jsonArray = JSONArray.create().addValue(123).addValue(-27).addValue(0).
                addValue(0x1F).addValue(0x2F);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new byte[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayChar() {
        String string1 = "test string!";
        JSONString jsonString = new JSONString(string1);
        assertEquals(jsonString, JSONSerializer.serialize(string1.toCharArray()));

        jsonString = new JSONString("");
        assertEquals(jsonString, JSONSerializer.serialize(new char[0]));
    }

    @Test
    public void testEnum() {
        DummyEnum enum1 = DummyEnum.ALPHA;
        JSONString jsonString = new JSONString("ALPHA");
        assertEquals(jsonString, JSONSerializer.serialize(enum1));

        enum1 = DummyEnum.BETA;
        jsonString = new JSONString("BETA");
        assertEquals(jsonString, JSONSerializer.serialize(enum1));
    }

    @Test
    public void testListString() {
        List<String> list1 = new ArrayList<>();
        list1.add("entry1");
        list1.add("entry2");
        JSONArray jsonArray = JSONArray.create().addValue("entry1").addValue("entry2");
        assertEquals(jsonArray, JSONSerializer.serialize(list1));
    }

    @Test
    public void testListInteger() {
        List<Integer> list1 = new ArrayList<>();
        list1.add(25);
        list1.add(-345);
        JSONArray jsonArray = JSONArray.create().addValue(25).addValue(-345);
        assertEquals(jsonArray, JSONSerializer.serialize(list1));
    }

    @Test
    public void testSetString() {
        Set<String> set1 = new HashSet<>();
        set1.add("entry1");
        set1.add("entry2");
        set1.add("entry3");
        set1.add("entry4");
        JSONArray jsonArray = JSONArray.create().addValue("entry1").addValue("entry2").
                addValue("entry3").addValue("entry4");
        assertTrue(arraySameContents(jsonArray, (JSONArray)JSONSerializer.serialize(set1)));
    }

    @Test
    public void testSetInteger() {
        Set<Integer> set1 = new HashSet<>();
        set1.add(15);
        set1.add(63);
        set1.add(32767);
        JSONArray jsonArray = JSONArray.create().addValue(15).addValue(63).addValue(32767);
        assertTrue(arraySameContents(jsonArray, (JSONArray)JSONSerializer.serialize(set1)));
    }

    @Test
    public void testObject() {
        DummyObject object1 = new DummyObject();
        object1.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1");
        assertEquals(jsonObject, JSONSerializer.serialize(object1));

        object1 = new DummyObject();
        jsonObject = JSONObject.create();
        assertEquals(jsonObject, JSONSerializer.serialize(object1));
    }

    @Test
    public void testObject2() {
        DummyObject2 object2 = new DummyObject2();
        object2.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1").
                putValue("int1", 0);
        assertEquals(jsonObject, JSONSerializer.serialize(object2));

        object2 = new DummyObject2();
        jsonObject = JSONObject.create().putValue("int1", 0);
        assertEquals(jsonObject, JSONSerializer.serialize(object2));
    }

    @Test
    public void testObject3() {
        DummyObject3 object3 = new DummyObject3();
        object3.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1");
        assertEquals(jsonObject, JSONSerializer.serialize(object3));

        object3.setInteger1(57);
        jsonObject.putValue("integer1", 57);
        assertEquals(jsonObject, JSONSerializer.serialize(object3));

        DummyObject object1 = new DummyObject();
        object1.setString1("value2");
        object3.setDummy1(object1);
        jsonObject.put("dummy1", JSONObject.create().putValue("string1", "value2"));
        assertEquals(jsonObject, JSONSerializer.serialize(object3));

        object3.setInteger1(null);
        jsonObject.remove("integer1");
        assertEquals(jsonObject, JSONSerializer.serialize(object3));

        int[] array1 = new int[] { 1, 1, 2, 3, 5, 8, 13 };
        object3.setArray1(array1);
        jsonObject.put("array1", JSONArray.create().addValue(1).addValue(1).addValue(2).
                addValue(3).addValue(5).addValue(8).addValue(13));
        assertEquals(jsonObject, JSONSerializer.serialize(object3));

        object3 = new DummyObject3();
        jsonObject = JSONObject.create();
        assertEquals(jsonObject, JSONSerializer.serialize(object3));
    }

    @Test
    public void testObject4() {
        DummyObject4 object4 = new DummyObject4();
        object4.setString1("value1");
        object4.setInt1(27);
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1").
                putValue("int1", 27);
        assertEquals(jsonObject, JSONSerializer.serialize(object4));
    }

    @Test
    public void testObject5() {
        DummyObject5 object5 = new DummyObject5();
        object5.setInt1(27);
        JSONObject jsonObject = JSONObject.create().putValue("dec", "27").putValue("hex", "1B");
        assertEquals(jsonObject, JSONSerializer.serialize(object5));
    }

    @Test
    public void testListObject5() {
        List<DummyObject5> list1 = new ArrayList<>();
        DummyObject5 object5 = new DummyObject5();
        object5.setInt1(27);
        list1.add(object5);
        DummyObject5 object5b = new DummyObject5();
        object5b.setInt1(45);
        list1.add(object5b);
        JSONArray jsonArray = JSONArray.create();
        jsonArray.add(JSONObject.create().putValue("dec", "27").putValue("hex", "1B"));
        jsonArray.add(JSONObject.create().putValue("dec", "45").putValue("hex", "2D"));
        assertEquals(jsonArray, JSONSerializer.serialize(list1));
    }

    @Test
    public void testSetObject5() {
        Set<DummyObject5> set1 = new HashSet<>();
        DummyObject5 object5 = new DummyObject5();
        object5.setInt1(27);
        set1.add(object5);
        DummyObject5 object5b = new DummyObject5();
        object5b.setInt1(45);
        set1.add(object5b);
        DummyObject5 object5c = new DummyObject5();
        object5c.setInt1(127);
        set1.add(object5c);
        JSONArray jsonArray = JSONArray.create();
        jsonArray.add(JSONObject.create().putValue("dec", "27").putValue("hex", "1B"));
        jsonArray.add(JSONObject.create().putValue("dec", "45").putValue("hex", "2D"));
        jsonArray.add(JSONObject.create().putValue("dec", "127").putValue("hex", "7F"));
        assertTrue(arraySameContents(jsonArray, (JSONArray)JSONSerializer.serialize(set1)));
    }

    @Test
    public void testSerializeObject() {
        DummyObject object1 = new DummyObject();
        object1.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1");
        assertEquals(jsonObject, JSONSerializer.serializeObject(object1));
    }

    /**
     * Test that two {@link JSONArray}s have the same contents, regardless of order (used for
     * checking serialization of {@link Set}.
     *
     * @param   array1  the first {@link JSONArray}
     * @param   array2  the second {@link JSONArray}
     * @return  {@code true} if the arrays have the same contents, {@code false} otherwise
     */
    private static boolean arraySameContents(JSONArray array1, JSONArray array2) {
        int len = array1.size();
        if (len != array2.size())
            return false;
        BitSet bitSet = new BitSet();
        for (JSONValue value : array1) {
            int i = array2.indexOf(value);
            if (i < 0)
                return false;
            if (bitSet.get(i))
                return false;
            bitSet.set(i);
        }
        if (bitSet.nextClearBit(0) != len)
            return false;
        return true;
    }

}
