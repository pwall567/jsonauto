/*
 * @(#) JSONSerializerTest.java
 */
package net.pwall.json.auto;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONFloat;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Default comment for {@code JSONSerializerTest}.
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
    public void testArrayInt() { // TODO - needs more, and different types
        int[] array1 = { 1, 2, 3 };
        JSONArray jsonArray = JSONArray.create().addValue(1).addValue(2).addValue(3);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new int[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
    }

    @Test
    public void testArrayLong() { // TODO - needs more, and different types
        long[] array1 = { 1L, 2L, 112233445566778899L };
        JSONArray jsonArray = JSONArray.create().addValue(1L).addValue(2L).
                addValue(112233445566778899L);
        assertEquals(jsonArray, JSONSerializer.serialize(array1));

        array1 = new long[0];
        jsonArray = JSONArray.create();
        assertEquals(jsonArray, JSONSerializer.serialize(array1));
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
    public void testObject() {
        DummyObject object1 = new DummyObject();
        object1.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1");
        assertEquals(jsonObject, JSONSerializer.serialize(object1));

        object1 = new DummyObject();
        jsonObject = JSONObject.create();
        assertEquals(jsonObject, JSONSerializer.serialize(object1));

        // TODO - try different kinds of fields: int, Integer, nested objects etc.
    }

    @Test
    public void testSerializeObject() {
        DummyObject object1 = new DummyObject();
        object1.setString1("value1");
        JSONObject jsonObject = JSONObject.create().putValue("string1", "value1");
        assertEquals(jsonObject, JSONSerializer.serializeObject(object1));
    }

}
