/*
 * @(#) JSONSerializer.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONException;
import net.pwall.json.JSONFloat;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONNumberValue;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;

/**
 * JSON auto-serialization class.
 *
 * @author Peter Wall
 */
public class JSONSerializer {

    /**
     * Private constructor.  A question for the future - do I want to allow options to be set on
     * an individual instance of this class to customise the serialization?
     */
    private JSONSerializer() {
    }

    /**
     * Create a JSON representation of any given object.
     *
     * @param   object  the object
     * @return  the JSON for that object
     */
    public static JSONValue serialize(Object object) {

        // is it null?

        if (object == null)
            return null;

        // is it a CharSequence (e.g. String)?

        if (object instanceof CharSequence)
            return new JSONString((CharSequence)object);

        // is it a Number (Integer, Double etc.)?

        if (object instanceof Number)
            return serializeNumber((Number)object);

        // is it a Boolean?

        if (object instanceof Boolean)
            return JSONBoolean.valueOf((Boolean)object);

        // is it a Character?

        if (object instanceof Character) {
            StringBuilder sb = new StringBuilder();
            sb.append(object);
            return new JSONString(sb);
        }

        // is it an array of char?

        if (object instanceof char[])
            return new JSONString(new String((char[])object));

        // is it an Object array?

        if (object instanceof Object[]) {
            JSONArray jsonArray = new JSONArray();
            for (Object item : (Object[])object)
                jsonArray.add(serialize(item));
            return jsonArray;
        }

        // is it an array of primitive type? (other than char)

        Class<?> objectClass = object.getClass();
        if (objectClass.isArray())
            return serializeArray(object);

        // does it have a "toJSON()" method?
        // questions:
        // - should we require that the method be public?
        // - should we look for method in include superclass?

        try {
            Method toJSON = objectClass.getDeclaredMethod("toJSON");
            toJSON.setAccessible(true);
            if (JSONValue.class.isAssignableFrom(toJSON.getReturnType()))
                return (JSONValue)toJSON.invoke(object);
        }
        catch (NoSuchMethodException e) {
            // ignore - normal case
        }
        catch (Exception e) {
            throw new JSONException("Custom serialization failed for " + objectClass.getName(),
                    e);
        }

        // is it an enum?

        if (object instanceof Enum)
            return new JSONString(((Enum<?>)object).name());

        // is it a List?

        if (object instanceof List)
            return serializeList((List<?>)object);

        // TODO - add List, Set, Map(?), Date, Calendar, BitSet, UUID, java.time classes, ...

        // serialize it as an Object

        JSONObject jsonObject = new JSONObject();
        addFieldsToJSONObject(jsonObject, objectClass, object);
        return jsonObject;

    }

    /**
     * Serialize the various {@link Number} classes.
     *
     * @param   number  the {@link Number} object
     * @return  the JSON for that object
     */
    public static JSONNumberValue serializeNumber(Number number) {

        // is it a Long?

        if (number instanceof Long)
            return JSONLong.valueOf((Long)number);

        // is it a Double?

        if (number instanceof Double)
            return JSONDouble.valueOf((Double)number);

        // is it a Float?

        if (number instanceof Float)
            return JSONFloat.valueOf((Float)number);

        // treat it as an Integer

        return JSONInteger.valueOf(number.intValue());
    }

    /**
     * Serialize an array of primitive type (except for {@code char[]} which serializes as a
     * string).
     *
     * @param   array   the array
     * @return  the JSON for that array
     * @throws  JSONException if the array can't be serialized
     */
    public static JSONArray serializeArray(Object array) {

        JSONArray jsonArray = new JSONArray();

        if (array instanceof int[]) {
            for (int item : (int[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof long[]) {
            for (long item : (long[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof boolean[]) {
            for (boolean item : (boolean[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof double[]) {
            for (double item : (double[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof float[]) {
            for (float item : (float[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof short[]) {
            for (short item : (short[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        if (array instanceof byte[]) {
            for (byte item : (byte[])array)
                jsonArray.addValue(item);
            return jsonArray;
        }

        Class<?> arrayClass = array.getClass();
        throw new JSONException(!arrayClass.isArray() ? "Not an array" :
                "Can't serialize array of " + arrayClass.getComponentType());
    }

    /**
     * Serialize a {@link List}.
     *
     * @param   list    the {@link List}
     * @return  the JSON for that {@link List}
     */
    public static JSONArray serializeList(List<?> list) {
        JSONArray jsonArray = JSONArray.create();
        for (Object item : list)
            jsonArray.add(serialize(item));
        return jsonArray;
    }

    /**
     * Serialize an object.  This is a convenience method that bypasses some of the built-in
     * type checks, for cases where the object is known to require field-by-field serialization.
     *
     * @param   object  the object
     * @return  the JSON for that object
     */
    public static JSONObject serializeObject(Object object) {
        if (object == null)
            return null;
        JSONObject jsonObject = new JSONObject();
        addFieldsToJSONObject(jsonObject, object.getClass(), object);
        return jsonObject;
    }

    /**
     * Add the individual serializations of the fields of an {@link Object} to a
     * {@link JSONObject}.  This method first calls itself recursively to get the fields of the
     * superclass (if any), then iterates through the declared fields of the class.
     *
     * @param   jsonObject      the destination {@link JSONObject}
     * @param   objectClass     the {@link Class} object for the source
     * @param   object          the source object
     * @throws  JSONException on any errors accessing the fields
     */
    private static void addFieldsToJSONObject(JSONObject jsonObject, Class<?> objectClass,
            Object object) {

        // first deal with fields of superclass

        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class))
            addFieldsToJSONObject(jsonObject, superClass, object);

        // now, for each field in this class

        for (Field field : objectClass.getDeclaredFields()) {
            String fieldName = field.getName();
            field.setAccessible(true);

            // ignore fields marked as static or transient

            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                try {
                    Object value = field.get(object);
                    if (value != null)
                        jsonObject.put(fieldName, serialize(value));
                }
                catch (JSONException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new JSONException("Error serializing " + objectClass + '.' +
                            fieldName);
                }
            }

            // TODO - handle annotations to modify name used in JSON, to ignore fields
            // or to output "null" if the field is null

        }

    }

}
