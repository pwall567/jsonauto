/*
 * @(#) JSONDeserializer.java
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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONException;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.annotation.JSONIgnore;
import net.pwall.json.annotation.JSONName;

/**
 * JSON auto-deserialization class.
 *
 * @author Peter Wall
 */
public class JSONDeserializer {

    /**
     * Deserialize an object from its JSON representation.
     *
     * @param   resultClass     the class of the result object
     * @param   json            the JSON representation of the object
     * @return  the object
     * @throws  JSONException   if the JSON can not be deserialized to the required type
     */
    public static <T> T deserialize(Class<T> resultClass, JSONValue json) {
        return deserialize(resultClass, null, json);
    }

    /**
     * Deserialize an object from its JSON representation.
     *
     * @param   resultClass     the class of the result object
     * @param   typeArgs        the actual types for a generic class, or {@code null}
     * @param   json            the JSON representation of the object
     * @return  the object
     * @throws  JSONException   if the JSON can not be deserialized to the required type
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<T> resultClass, Type[] typeArgs, JSONValue json) {

        Objects.requireNonNull(resultClass);

        // check for null

        if (json == null)
            return null;

        // check for JSONValue

        if (JSONValue.class.isAssignableFrom(resultClass) &&
                resultClass.isAssignableFrom(json.getClass()))
            return (T)json;

        // does the target class have a "fromJSON()" method?
        // questions:
        // - should we require that the method be public?
        // - should we look for method in include superclass?

        try {
            Method fromJSON = resultClass.getDeclaredMethod("fromJSON", JSONValue.class);
            fromJSON.setAccessible(true);
            if (Modifier.isStatic(fromJSON.getModifiers()) &&
                    resultClass.isAssignableFrom(fromJSON.getReturnType())) {
                return (T)fromJSON.invoke(null, json);
            }
        }
        catch (NoSuchMethodException e) {
            // ignore - this just means the result class doesn't have a fromJSON method
        }
        catch (Exception e) {
            throw new JSONException("Custom deserialization failed for " + resultClass, e);
        }

        // is the JSON a string?

        if (json instanceof JSONString) {

            String s = json.toString();

            // is the target class String?

            if (resultClass.equals(String.class))
                return (T)s;

            // is the target class Character?

            if (resultClass.equals(Character.class)) {
                if (s.length() != 1)
                    throw new JSONException("Character must be string of length 1");
                return (T)Character.valueOf(s.charAt(0));
            }

            // is the target class array of char?

            if (resultClass.isArray() && resultClass.getComponentType().equals(char.class))
                return (T)json.toString().toCharArray();

            // is the target class an enum?

            if (Enum.class.isAssignableFrom(resultClass)) {
                try {
                    Method valuesMethod = resultClass.getMethod("values");
                    Enum<?>[] values = (Enum<?>[])valuesMethod.invoke(null);
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].name().equals(s))
                            return (T)values[i];
                    }
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Error deserializing enum");
                }
            }

            // does the target class have a constructor that takes String?
            // (e.g. StringBuilder, Integer, ... )

            try {
                Constructor<T> constructor = resultClass.getConstructor(String.class);
                return constructor.newInstance(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize string as " + resultClass);
            }

        }

        // is the JSON a number?

        if (json instanceof Number) {

            // is the target class Integer?

            if (resultClass.equals(Integer.class) || resultClass.equals(int.class))
                return (T)Integer.valueOf(((Number)json).intValue());

            // is the target class Long?

            if (resultClass.equals(Long.class) || resultClass.equals(long.class))
                return (T)Long.valueOf(((Number)json).longValue());

            // is the target class Double?

            if (resultClass.equals(Double.class) || resultClass.equals(double.class))
                return (T)Double.valueOf(((Number)json).doubleValue());

            // is the target class Float?

            if (resultClass.equals(Float.class) || resultClass.equals(float.class))
                return (T)Float.valueOf(((Number)json).floatValue());

            // is the target class Short?

            if (resultClass.equals(Short.class) || resultClass.equals(short.class))
                return (T)Short.valueOf(((Number)json).shortValue());

            // is the target class Byte?

            if (resultClass.equals(Byte.class) || resultClass.equals(byte.class))
                return (T)Byte.valueOf(((Number)json).byteValue());

            throw new JSONException("Can't deserialize number as " + resultClass);

        }

        // is the JSON a boolean?

        if (json instanceof JSONBoolean) {

            // is the target Boolean?

            if (resultClass.equals(Boolean.class) || resultClass.equals(boolean.class))
                return (T)Boolean.valueOf(((JSONBoolean)json).booleanValue());

            throw new JSONException("Can't deserialize boolean as " + resultClass);

        }

        // is the JSON an array?

        if (json instanceof JSONArray) {

            // is the target an array?

            if (resultClass.isArray()) {
                Class<?> itemClass = resultClass.getComponentType();
                if (!itemClass.isPrimitive())
                    return (T)deserializeArray(itemClass, (JSONArray)json);
                return deserializePrimitiveArray(resultClass, (JSONArray)json);
            }

            // is the target a Set?

            if (resultClass.equals(Set.class))
                return (T)deserializeCollection((Class<? extends Collection<?>>)HashSet.class,
                        typeArgs, (JSONArray)json);

            // is the target a List or Collection?

            if (resultClass.equals(List.class) || resultClass.equals(Collection.class))
                return (T)deserializeCollection((Class<? extends Collection<?>>)ArrayList.class,
                        typeArgs, (JSONArray)json);

            // is the target any derived class from Collection?

            if (Collection.class.isAssignableFrom(resultClass))
                return (T)deserializeCollection((Class<? extends Collection<?>>)resultClass,
                        typeArgs, (JSONArray)json);

            throw new JSONException("Can't deserialize array as " + resultClass);

        }

        // is the JSON an object?

        if (json instanceof JSONObject) {

            // is the target a Map?

            if (resultClass.equals(Map.class))
                return (T)deserializeMap(HashMap.class, typeArgs, (JSONObject)json);

            // is the target any derived class from Map?

            if (Map.class.isAssignableFrom(resultClass))
                return (T)deserializeMap((Class<? extends Map<?, ?>>)resultClass, typeArgs,
                        (JSONObject)json);

            return deserializeObject(resultClass, (JSONObject)json);

        }

        throw new JSONException("Can't deserialize " + json.getClass());
    }

    public static <T> T deserializeObject(Class<T> resultClass, JSONObject object) {
        try {
            Constructor<T> constructor = resultClass.getConstructor();
            constructor.setAccessible(true);
            T result = constructor.newInstance();
            for (Map.Entry<String, JSONValue> entry : object.entrySet()) {
                Field field = findField(resultClass, entry.getKey());
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) ||
                        field.isAnnotationPresent(JSONIgnore.class))
                    throw new JSONException("Can't access field " + field);
                Type genericType = field.getGenericType();
                Type[] typeArgs = genericType instanceof ParameterizedType ?
                        ((ParameterizedType)genericType).getActualTypeArguments() : null;
                field.setAccessible(true);
                field.set(result, deserialize(field.getType(), typeArgs, entry.getValue()));
            }
            return result;
        }
        catch (JSONException e) {
            throw e;
        }
        catch (Exception e) {
            throw new JSONException("Can't deserialize object as " + resultClass);
        }
    }

    private static Field findField(Class<?> resultClass, String name) {
        Field[] fields = resultClass.getDeclaredFields();
        for (Field field : fields) {
            JSONName nameAnnotation = field.getAnnotation(JSONName.class);
            if (nameAnnotation != null && nameAnnotation.value().equals(name))
                return field;
        }
        for (Field field : fields) {
            if (field.getName().equals(name))
                return field;
        }
        Class<?> superClass = resultClass.getSuperclass();
        return superClass == null ? null : findField(superClass, name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeString(Class<T> resultClass, String s) {

        Objects.requireNonNull(resultClass);

        // check for null

        if (s == null)
            return null;

        // is it a String?

        if (resultClass.equals(String.class))
            return (T)s;

        // is it a Character?

        if (resultClass.equals(Character.class)) {
            if (s.length() != 1)
                throw new JSONException("Character must be string of length 1");
            return (T)Character.valueOf(s.charAt(0));
        }

        // any others?

        // is there a constructor that takes String? (includes StringBuilder, Integer etc.)

        try {
            Constructor<T> constructor = resultClass.getConstructor(String.class);
            return constructor.newInstance(s);
        }
        catch (Exception e) {
            throw new JSONException("Can't deserialize string as " + resultClass);
        }

    }

    /**
     * Deserialize a {@link Map}.  This method only works for a {@code Map<?, ?>} where the key
     * type can be constructed from {@link String} and the value type is itself deserializable.
     *
     * @param   mapClass        the class of the map
     * @param   typeArgs        the actual types for the generic class
     * @param   object          a {@link JSONObject} to be deserialized into a {@link Map}
     * @return  the {@link Map}
     * @throws  JSONException if the type arguments are incorrect, if the map class can't be
     *          instantiated, or if the deserialization of the items throws an exception
     */
    @SuppressWarnings("unchecked")
    public static <M extends Map<K, V>, K, V> Map<K, V> deserializeMap(Class<M> mapClass,
            Type[] typeArgs, JSONObject object) {
        if (typeArgs == null || typeArgs.length != 2)
            throw new JSONException("Missing or incorrect type arguments for Map");
        Class<K> keyClass = null;
        Type type = typeArgs[0];
        if (type instanceof Class)
            keyClass = (Class<K>)type;
        else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            keyClass = (Class<K>)pt.getRawType();
        }
        Class<V> valueClass = null;
        Type[] valueTypeArgs = null;
        type = typeArgs[0];
        if (type instanceof Class)
            valueClass = (Class<V>)type;
        else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            valueClass = (Class<V>)pt.getRawType();
            valueTypeArgs = pt.getActualTypeArguments();
        }
        try {
            Map<K, V> result = mapClass.newInstance();
            for (Map.Entry<String, JSONValue> entry : object.entrySet())
                result.put(deserializeString(keyClass, entry.getKey()),
                        deserialize(valueClass, valueTypeArgs, entry.getValue()));
            return result;
        }
        catch (JSONException je) {
            throw je;
        }
        catch (Exception e) {
            throw new JSONException("Can't instantiate " + mapClass, e);
        }
    }

    /**
     * Deserialize a {@link Collection}.  This method only works for a {@code Collection<?>}
     * where the item type is itself deserializable.
     *
     * @param   collectionClass the class of the collection
     * @param   typeArgs        the actual types for the generic class
     * @param   array           a {@link JSONArray} to be deserialized into a {@link Collection}
     * @return  the {@link Collection}
     * @throws  JSONException if the type arguments are incorrect, if the collection class can't
     *          be instantiated, or if the deserialization of the items throws an exception
     */
    @SuppressWarnings("unchecked")
    public static <C extends Collection<T>, T> Collection<T> deserializeCollection(
            Class<C> collectionClass, Type[] typeArgs, JSONArray array) {
        if (typeArgs == null || typeArgs.length != 1)
            throw new JSONException("Missing or incorrect type arguments for Collection");
        Class<T> itemClass = null;
        Type[] itemTypeArgs = null;
        Type type = typeArgs[0];
        if (type instanceof Class)
            itemClass = (Class<T>)type;
        else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            itemClass = (Class<T>)pt.getRawType();
            itemTypeArgs = pt.getActualTypeArguments();
        }
        try {
            Collection<T> result = collectionClass.newInstance();
            for (JSONValue value : array)
                result.add(deserialize(itemClass, itemTypeArgs, value));
            return result;
        }
        catch (JSONException je) {
            throw je;
        }
        catch (Exception e) {
            throw new JSONException("Can't instantiate " + collectionClass, e);
        }
    }

    /**
     * Deserialize an array of primitive type.
     *
     * @param   arrayClass      the class of the array
     * @param   array           a {@link JSONArray} to be deserialized into an array
     * @return  the result array
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializePrimitiveArray(Class<T> arrayClass, JSONArray array) {
        int n = array.size();
        Class<?> componentType = arrayClass.getComponentType();
        if (componentType.equals(int.class)) {
            int[] result = new int[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(int.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(long.class)) {
            long[] result = new long[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(long.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(double.class)) {
            double[] result = new double[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(double.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(float.class)) {
            float[] result = new float[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(float.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(short.class)) {
            short[] result = new short[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(short.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(byte.class)) {
            byte[] result = new byte[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(byte.class, array.get(i));
            return (T)result;
        }
        if (componentType.equals(boolean.class)) {
            boolean[] result = new boolean[n];
            for (int i = 0; i < n; i++)
                result[i] = deserialize(boolean.class, array.get(i));
            return (T)result;
        }
        throw new JSONException("Can't deserialize array of " + componentType);
    }

    /**
     * Deserialize an array.
     *
     * @param   itemClass       the class of the array item
     * @param   array           a {@link JSONArray} to be deserialized into an array
     * @return  the result array
     */
    public static <T> T[] deserializeArray(Class<T> itemClass, JSONArray array) {
        int n = array.size();
        @SuppressWarnings("unchecked")
        T[] result = (T[])Array.newInstance(itemClass, n);
        for (int i = 0; i < n; i++)
            result[i] = deserialize(itemClass, array.get(i));
        return result;
    }

}
