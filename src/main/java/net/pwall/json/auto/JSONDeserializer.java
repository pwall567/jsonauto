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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONException;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.annotation.JSONIgnore;
import net.pwall.json.annotation.JSONName;
import net.pwall.util.ISO8601Date;

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
     * @param   <T>             the type of the result
     * @return  the object
     * @throws  JSONException   if the JSON can not be deserialized to the required type
     * @throws  NullPointerException if the resultClass parameter is {@code null}
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
     * @param   <T>             the type of the result
     * @return  the object
     * @throws  JSONException   if the JSON can not be deserialized to the required type
     * @throws  NullPointerException if the resultClass parameter is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<T> resultClass, Type[] typeArgs, JSONValue json) {

        Objects.requireNonNull(resultClass);

        // is the target an Optional (or OptionalInt etc.)?
        // (before the null check because null results in Optional.empty())

        if (resultClass.equals(Optional.class))
            return (T)deserializeOptional(typeArgs, json);
        if (resultClass.equals(OptionalInt.class))
            return (T)deserializeOptionalInt(json);
        if (resultClass.equals(OptionalLong.class))
            return (T)deserializeOptionalLong(json);
        if (resultClass.equals(OptionalDouble.class))
            return (T)deserializeOptionalDouble(json);

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

        if (json instanceof JSONString)
            return deserializeStringInternal(resultClass, json.toString());

        // is the JSON a number?

        if (json instanceof Number)
            return deserializeNumberInternal(resultClass, (Number)json);

        // is the JSON a boolean?

        if (json instanceof JSONBoolean) {

            // is the target Boolean?

            if (resultClass.equals(Boolean.class) || resultClass.equals(boolean.class))
                return (T)Boolean.valueOf(((JSONBoolean)json).booleanValue());

            throw new JSONException("Can't deserialize boolean as " + resultClass);

        }

        // is the JSON an array?

        if (json instanceof JSONArray) {

            JSONArray array = (JSONArray)json;

            // is the target an array?

            if (resultClass.isArray()) {
                Class<?> itemClass = resultClass.getComponentType();
                if (!itemClass.isPrimitive())
                    return (T)deserializeArray(itemClass, array);
                return deserializePrimitiveArray(resultClass, array);
            }

            // is the target a Set?

            if (resultClass.equals(Set.class))
                return (T)deserializeCollection((Class<?>)HashSet.class, typeArgs, array);

            // is the target a List or Collection?

            if (resultClass.equals(List.class) || resultClass.equals(Collection.class))
                return (T)deserializeCollection((Class<?>)ArrayList.class, typeArgs, array);

            // is the target any derived class from Collection?

            if (Collection.class.isAssignableFrom(resultClass))
                return (T)deserializeCollection(resultClass, typeArgs, array);

            // is the target a BitSet?

            if (resultClass.equals(BitSet.class)) {
                BitSet result = new BitSet();
                for (int i = 0, n = array.size(); i < n; i++)
                    result.set(array.getInt(i));
                return (T)result;
            }

            throw new JSONException("Can't deserialize array as " + resultClass);

        }

        // is the JSON an object?

        if (json instanceof JSONObject) {

            // is the target a Map?

            if (resultClass.equals(Map.class))
                return (T)deserializeMap(HashMap.class, typeArgs, (JSONObject)json);

            // is the target any derived class from Map?

            if (Map.class.isAssignableFrom(resultClass))
                return (T)deserializeMap(resultClass, typeArgs, (JSONObject)json);

            return deserializeObject(resultClass, (JSONObject)json);

        }

        throw new JSONException("Can't deserialize " + json.getClass());
    }

    /**
     * Deserialize a string.
     *
     * @param   resultClass     the class of the result object
     * @param   s               the string
     * @param   <T>             the type of the result
     * @return  the object
     * @throws  JSONException   if the string can not be deserialized to the required type
     * @throws  NullPointerException if the resultClass parameter is {@code null}
     */
    public static <T> T deserializeString(Class<T> resultClass, String s) {

        Objects.requireNonNull(resultClass);

        // check for null

        if (s == null)
            return null;

        return deserializeStringInternal(resultClass, s);

    }

    @SuppressWarnings("unchecked")
    private static <T, E extends Enum<E>> T deserializeStringInternal(Class<T> resultClass,
            String s) {

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
            return (T)s.toCharArray();

        // is the target class Calendar?

        if (resultClass.equals(Calendar.class)) {
            try {
                return (T)ISO8601Date.decode(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize Calendar", e);
            }
        }

        // is the target class Date?

        if (resultClass.equals(Date.class)) {
            try {
                return (T)ISO8601Date.decode(s).getTime();
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize Date", e);
            }
        }

        // is the target class Instant?

        if (resultClass.equals(Instant.class)) {
            try {
                return (T)Instant.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize Instant", e);
            }
        }

        // is the target class LocalDate?

        if (resultClass.equals(LocalDate.class)) {
            try {
                return (T)LocalDate.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize LocalDate", e);
            }
        }

        // is the target class LocalDateTime?

        if (resultClass.equals(LocalDateTime.class)) {
            try {
                return (T)LocalDateTime.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize LocalDateTime", e);
            }
        }

        // is the target class OffsetTime?

        if (resultClass.equals(OffsetTime.class)) {
            try {
                return (T)OffsetTime.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize OffsetTime", e);
            }
        }

        // is the target class OffsetDateTime?

        if (resultClass.equals(OffsetDateTime.class)) {
            try {
                return (T)OffsetDateTime.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize OffsetDateTime", e);
            }
        }

        // is the target class ZonedDateTime?

        if (resultClass.equals(ZonedDateTime.class)) {
            try {
                return (T)ZonedDateTime.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize ZonedDateTime", e);
            }
        }

        // is the target class Year?

        if (resultClass.equals(Year.class)) {
            try {
                return (T)Year.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize Year", e);
            }
        }

        // is the target class YearMonth?

        if (resultClass.equals(YearMonth.class)) {
            try {
                return (T)YearMonth.parse(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize Year", e);
            }
        }

        // is the target class UUID?

        if (resultClass.equals(UUID.class)) {
            try {
                return (T)UUID.fromString(s);
            }
            catch (Exception e) {
                throw new JSONException("Can't deserialize UUID", e);
            }
        }

        // is the target class an enum?

        if (Enum.class.isAssignableFrom(resultClass)) {
            try {
                return (T)Enum.valueOf((Class<E>)resultClass, s);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error deserializing enum");
            }
        }

        // does the target class have a public constructor that takes String?
        // (e.g. StringBuilder, Integer, ... )

        try {
            return resultClass.getConstructor(String.class).newInstance(s);
        }
        catch (Exception e) {
            throw new JSONException("Can't deserialize string as " + resultClass);
        }

    }

    /**
     * Deserialize a number.
     *
     * @param   resultClass     the class of the result object
     * @param   number          the number
     * @param   <T>             the type of the result
     * @return  the object
     * @throws  JSONException   if the number can not be deserialized to the required type
     * @throws  NullPointerException if the resultClass parameter is {@code null}
     */
    public static <T> T deserializeNumber(Class<T> resultClass, Number number) {

        Objects.requireNonNull(resultClass);

        // check for null

        if (number == null)
            return null;

        return deserializeNumberInternal(resultClass, number);

    }

    @SuppressWarnings("unchecked")
    private static <T> T deserializeNumberInternal(Class<T> resultClass, Number number) {

        // is the target class Integer?

        if (resultClass.equals(Integer.class) || resultClass.equals(int.class))
            return (T)Integer.valueOf(number.intValue());

        // is the target class Long?

        if (resultClass.equals(Long.class) || resultClass.equals(long.class))
            return (T)Long.valueOf(number.longValue());

        // is the target class Double?

        if (resultClass.equals(Double.class) || resultClass.equals(double.class))
            return (T)Double.valueOf(number.doubleValue());

        // is the target class Float?

        if (resultClass.equals(Float.class) || resultClass.equals(float.class))
            return (T)Float.valueOf(number.floatValue());

        // is the target class Short?

        if (resultClass.equals(Short.class) || resultClass.equals(short.class))
            return (T)Short.valueOf(number.shortValue());

        // is the target class Byte?

        if (resultClass.equals(Byte.class) || resultClass.equals(byte.class))
            return (T)Byte.valueOf(number.byteValue());

        throw new JSONException("Can't deserialize number as " + resultClass);

    }

    /**
     * Deserialize an object.
     *
     * @param   resultClass     the class of the result object
     * @param   object          the JSON representation of the object
     * @param   <T>             the type of the result
     * @return  the object
     * @throws  NullPointerException if the resultClass parameter is {@code null}
     */
    public static <T> T deserializeObject(Class<T> resultClass, JSONObject object) {
        try {
            Constructor<T> constructor = resultClass.getConstructor();
            constructor.setAccessible(true);
            T result = constructor.newInstance();
            for (Map.Entry<String, JSONValue> entry : object.entrySet()) {
                String name = entry.getKey();
                // TODO use setter method if available?
                Field field = findField(resultClass, name);
                if (field == null)
                    throw new JSONException("Can't find field for " + name);
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
            throw new JSONException("Can't deserialize object as " + resultClass, e);
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

    /**
     * Deserialize a {@link Map}.  This method only works for a {@code Map<?, ?>} where the key
     * type can be constructed from {@link String} and the value type is itself deserializable.
     *
     * @param   mapClass        the class of the map
     * @param   typeArgs        the actual types for the generic class
     * @param   object          a {@link JSONObject} to be deserialized into a {@link Map}
     * @param   <K>             the type of the key
     * @param   <V>             the type of the value
     * @return  the {@link Map}
     * @throws  JSONException if the type arguments are incorrect, if the map class can't be
     *          instantiated, or if the deserialization of the items throws an exception
     * @throws  NullPointerException if the mapClass or object parameter is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> deserializeMap(Class<?> mapClass, Type[] typeArgs,
            JSONObject object) {
        if (typeArgs == null || typeArgs.length != 2)
            throw new JSONException("Missing or incorrect type arguments for Map");
        Class<K> keyClass = (Class<K>)getGenericClass(typeArgs[0]);
        Type valueType = typeArgs[1];
        Class<V> valueClass = (Class<V>)getGenericClass(valueType);
        Type[] valueTypeArgs = getGenericTypeArgs(valueType);
        try {
            Map<K, V> result = (Map<K, V>)mapClass.newInstance();
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
     * @param   <T>             the type of the result
     * @return  the {@link Collection}
     * @throws  JSONException if the type arguments are incorrect, if the collection class can't
     *          be instantiated, or if the deserialization of the items throws an exception
     * @throws  NullPointerException if the collectionClass or array parameter is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserializeCollection(Class<?> collectionClass,
            Type[] typeArgs, JSONArray array) {
        if (typeArgs == null || typeArgs.length != 1)
            throw new JSONException("Missing or incorrect type arguments for Collection");
        Type itemType = typeArgs[0];
        Class<T> itemClass = (Class<T>)getGenericClass(itemType);
        Type[] itemTypeArgs = getGenericTypeArgs(itemType);
        try {
            Collection<T> result = (Collection<T>)collectionClass.newInstance();
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
     * @param   <T>             the type of the result
     * @return  the result array
     * @throws  NullPointerException if either parameter is {@code null}
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
     * @param   <T>             the type of the result array item
     * @return  the result array
     * @throws  NullPointerException if either parameter is {@code null}
     */
    public static <T> T[] deserializeArray(Class<T> itemClass, JSONArray array) {
        int n = array.size();
        @SuppressWarnings("unchecked")
        T[] result = (T[])Array.newInstance(itemClass, n);
        for (int i = 0; i < n; i++)
            result[i] = deserialize(itemClass, array.get(i));
        return result;
    }

    /**
     * Deserialize an {@link Optional}.
     *
     * @param   typeArgs    the actual types for the generic class
     * @param   json        the JSON for the target object
     * @return  the result {@link Optional}
     * @throws  JSONException if the type arguments are incorrect or if the deserialization of
     *          the target object throws an exception
     */
    public static Optional<?> deserializeOptional(Type[] typeArgs, JSONValue json) {
        if (typeArgs == null || typeArgs.length != 1)
            throw new JSONException("Missing or incorrect type arguments for Optional");
        Type targetType = typeArgs[0];
        Class<?> targetClass = getGenericClass(targetType);
        Type[] targetTypeArgs = getGenericTypeArgs(targetType);
        Object value = deserialize(targetClass, targetTypeArgs, json);
        return value != null ? Optional.of(value) : Optional.empty();
    }

    /**
     * Deserialize an {@link OptionalInt}.
     *
     * @param   json        the JSON for the target value
     * @return  the result {@link OptionalInt}
     * @throws  JSONException if the deserialization of the target value throws an exception
     */
    public static OptionalInt deserializeOptionalInt(JSONValue json) {
        Integer value = deserialize(Integer.class, null, json);
        return value != null ? OptionalInt.of(value) : OptionalInt.empty();
    }

    /**
     * Deserialize an {@link OptionalLong}.
     *
     * @param   json        the JSON for the target value
     * @return  the result {@link OptionalLong}
     * @throws  JSONException if the deserialization of the target value throws an exception
     */
    public static OptionalLong deserializeOptionalLong(JSONValue json) {
        Long value = deserialize(Long.class, null, json);
        return value != null ? OptionalLong.of(value) : OptionalLong.empty();
    }

    /**
     * Deserialize an {@link OptionalDouble}.
     *
     * @param   json        the JSON for the target value
     * @return  the result {@link OptionalDouble}
     * @throws  JSONException if the deserialization of the target value throws an exception
     */
    public static OptionalDouble deserializeOptionalDouble(JSONValue json) {
        Double value = deserialize(Double.class, null, json);
        return value != null ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    private static Class<?> getGenericClass(Type type) {
        if (type instanceof Class)
            return (Class<?>)type;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            return (Class<?>)pt.getRawType();
        }
        throw new JSONException("Can't determine target class for parameterized type");
    }

    private static Type[] getGenericTypeArgs(Type type) {
        if (type instanceof Class)
            return null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            return pt.getActualTypeArguments();
        }
        throw new JSONException("Can't determine target type args for parameterized type");
    }

}
