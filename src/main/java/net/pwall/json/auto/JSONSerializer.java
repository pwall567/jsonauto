/*
 * @(#) JSONSerializer.java
 *
 * jsonauto JSON Auto-serialization Library
 * Copyright (c) 2015, 2016, 2017 Peter Wall
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

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
import net.pwall.json.JSONZero;
import net.pwall.json.annotation.JSONAlways;
import net.pwall.json.annotation.JSONIgnore;
import net.pwall.json.annotation.JSONName;
import net.pwall.util.Strings;

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

        // is it already a JSONValue?

        if (object instanceof JSONValue)
            return (JSONValue)object;

        // is it a CharSequence (e.g. String)?

        if (object instanceof CharSequence)
            return new JSONString((CharSequence)object);

        // is it an Integer?

        if (object instanceof Integer || object instanceof Short || object instanceof Byte)
            return JSONInteger.valueOf(((Number)object).intValue());

        // is it a Long?

        if (object instanceof Long)
            return JSONLong.valueOf(((Long)object).longValue());

        // is it a Double?

        if (object instanceof Double)
            return JSONDouble.valueOf(((Double)object).doubleValue());

        // is it a Float?

        if (object instanceof Float)
            return JSONFloat.valueOf(((Float)object).floatValue());

        // is it a Boolean?

        if (object instanceof Boolean)
            return JSONBoolean.valueOf((Boolean)object);

        // is it a Character?

        if (object instanceof Character) {
            StringBuilder sb = new StringBuilder(1);
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

        // is it a Collection?

        if (object instanceof Collection)
            return serializeCollection((Collection<?>)object);

        // is it a Map?

        if (object instanceof Map)
            return serializeMap((Map<?, ?>)object);

        // is it a Calendar?

        if (object instanceof Calendar)
            return serializeCalendar((Calendar)object);

        // is it a Date?

        if (object instanceof Date)
            return serializeDate((Date)object);

        // is it an Instant?

        if (object instanceof Instant)
            return serializeInstant((Instant)object);

        // is it a LocalDate?

        if (object instanceof LocalDate)
            return serializeLocalDate((LocalDate)object);

        // is it a LocalDateTime?

        if (object instanceof LocalDateTime)
            return serializeLocalDateTime((LocalDateTime)object);

        // is it an OffsetTime?

        if (object instanceof OffsetTime)
            return serializeOffsetTime((OffsetTime)object);

        // is it an OffsetDateTime?

        if (object instanceof OffsetDateTime)
            return serializeOffsetDateTime((OffsetDateTime)object);

        // is it a ZonedDateTime?

        if (object instanceof ZonedDateTime)
            return serializeZonedDateTime((ZonedDateTime)object);

        // is it a Year?

        if (object instanceof Year)
            return serializeYear((Year)object);

        // is it a YearMonth?

        if (object instanceof YearMonth)
            return serializeYearMonth((YearMonth)object);

        // is it a BitSet?

        if (object instanceof BitSet)
            return serializeBitSet((BitSet)object);

        // is it an Optional?

        if (object instanceof Optional)
            return serializeOptional((Optional<?>)object);

        // is it an OptionalInt?

        if (object instanceof OptionalInt)
            return serializeOptionalInt((OptionalInt)object);

        // is it an OptionalLong?

        if (object instanceof OptionalLong)
            return serializeOptionalLong((OptionalLong)object);

        // is it an OptionalDouble?

        if (object instanceof OptionalDouble)
            return serializeOptionalDouble((OptionalDouble)object);

        // TODO - add UUID, java.time classes, ...

        // serialize it as an Object (this may not be a satisfactory default behaviour)

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

        // is it an Integer?

        if (number instanceof Integer || number instanceof Short || number instanceof Byte)
            return JSONInteger.valueOf(number.intValue());

        // is it a Long?

        if (number instanceof Long)
            return JSONLong.valueOf(number.longValue());

        // is it a Double?

        if (number instanceof Double)
            return JSONDouble.valueOf(number.doubleValue());

        // is it a Float?

        if (number instanceof Float)
            return JSONFloat.valueOf(number.floatValue());

        // find the best representation

        long longValue = number.longValue();
        double doubleValue = number.doubleValue();
        if (doubleValue != longValue)
            return JSONDouble.valueOf(number.doubleValue());

        int intValue = number.intValue();
        if (longValue != intValue)
            return JSONLong.valueOf(longValue);

        return intValue == 0 ? new JSONZero() : JSONInteger.valueOf(intValue);
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
     * Serialize a {@link Collection}.
     *
     * @param   collection    the {@link Collection}
     * @return  the JSON for that {@link Collection}
     */
    public static JSONArray serializeCollection(Collection<?> collection) {
        JSONArray jsonArray = new JSONArray();
        for (Object item : collection)
            jsonArray.add(serialize(item));
        return jsonArray;
    }

    /**
     * Serialize a {@link List}.  Synonym for {@link #serializeCollection(Collection)}.
     *
     * @param   list    the {@link List}
     * @return  the JSON for that {@link List}
     */
    public static JSONArray serializeList(List<?> list) {
        return serializeCollection(list);
    }

    /**
     * Serialize a {@link Set}.  Synonym for {@link #serializeCollection(Collection)}.
     *
     * @param   set     the {@link Set}
     * @return  the JSON for that {@link Set}
     */
    public static JSONArray serializeSet(Set<?> set) {
        return serializeCollection(set);
    }

    /**
     * Serialize a {@link Map} to a {@link JSONObject}.  The key is converted to a string (by
     * means of the {@link Object#toString() toString()} method), and the value is serialized
     * using this class.
     *
     * @param   map     the {@link Map}
     * @return  the JSON for that {@link Map}
     */
    public static JSONObject serializeMap(Map<?, ?> map) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<?, ?> entry : map.entrySet())
            jsonObject.put(entry.getKey().toString(), serialize(entry.getValue()));
        return jsonObject;
    }

    /**
     * Serialize a {@link Date}.
     *
     * @param   date    the {@link Date}
     * @return  the JSON for that {@link Date}
     */
    public static JSONString serializeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return serializeCalendar(calendar);
    }

    /**
     * Serialize a {@link Calendar}.
     *
     * @param   calendar    the {@link Calendar}
     * @return  the JSON for that {@link Calendar}
     */
    public static JSONString serializeCalendar(Calendar calendar) {
        StringBuilder sb = new StringBuilder();
        try {
            Strings.appendPositiveInt(sb, calendar.get(Calendar.YEAR));
            sb.append('-');
            Strings.append2Digits(sb, calendar.get(Calendar.MONTH) + 1);
            sb.append('-');
            Strings.append2Digits(sb, calendar.get(Calendar.DAY_OF_MONTH));
            sb.append('T');
            Strings.append2Digits(sb, calendar.get(Calendar.HOUR_OF_DAY));
            sb.append(':');
            Strings.append2Digits(sb, calendar.get(Calendar.MINUTE));
            sb.append(':');
            Strings.append2Digits(sb, calendar.get(Calendar.SECOND));
            sb.append('.');
            Strings.append3Digits(sb, calendar.get(Calendar.MILLISECOND));
            int offset = calendar.get(Calendar.ZONE_OFFSET);
            if (calendar.getTimeZone().inDaylightTime(calendar.getTime()))
                offset += calendar.get(Calendar.DST_OFFSET);
            offset /= 60 * 1000;
            if (offset == 0)
                sb.append('Z');
            else {
                if (offset < 0) {
                    sb.append('-');
                    offset = -offset;
                }
                else
                    sb.append('+');
                Strings.append2Digits(sb, offset / 60);
                sb.append(':');
                Strings.append2Digits(sb, offset % 60);
            }
        }
        catch (IOException ioe) {
            // can't happen - StringBuilder does not throw IOException
        }
        return new JSONString(sb);
    }

    /**
     * Serialize an {@link Instant}.
     *
     * @param   instant the {@link Instant}
     * @return  the JSON for that {@link Instant}
     */
    public static JSONString serializeInstant(Instant instant) {
        return new JSONString(instant.toString());
    }

    /**
     * Serialize a {@link LocalDate}.
     *
     * @param   localDate   the {@link LocalDate}
     * @return  the JSON for that {@link LocalDate}
     */
    public static JSONString serializeLocalDate(LocalDate localDate) {
        return new JSONString(localDate.toString());
    }

    /**
     * Serialize a {@link LocalDateTime}.
     *
     * @param   localDateTime   the {@link LocalDateTime}
     * @return  the JSON for that {@link LocalDateTime}
     */
    public static JSONString serializeLocalDateTime(LocalDateTime localDateTime) {
        return new JSONString(localDateTime.toString());
    }

    /**
     * Serialize an {@link OffsetTime}.
     *
     * @param   offsetTime  the {@link OffsetTime}
     * @return  the JSON for that {@link OffsetTime}
     */
    public static JSONString serializeOffsetTime(OffsetTime offsetTime) {
        return new JSONString(offsetTime.toString());
    }

    /**
     * Serialize an {@link OffsetDateTime}.
     *
     * @param   offsetDateTime  the {@link OffsetDateTime}
     * @return  the JSON for that {@link OffsetDateTime}
     */
    public static JSONString serializeOffsetDateTime(OffsetDateTime offsetDateTime) {
        return new JSONString(offsetDateTime.toString());
    }

    /**
     * Serialize a {@link ZonedDateTime}.
     *
     * @param   zonedDateTime   the {@link ZonedDateTime}
     * @return  the JSON for that {@link ZonedDateTime}
     */
    public static JSONString serializeZonedDateTime(ZonedDateTime zonedDateTime) {
        return new JSONString(zonedDateTime.toString());
    }

    /**
     * Serialize a {@link Year}.
     *
     * @param   year    the {@link Year}
     * @return  the JSON for that {@link Year}
     */
    public static JSONString serializeYear(Year year) {
        return new JSONString(year.toString());
    }

    /**
     * Serialize a {@link YearMonth}.
     *
     * @param   yearMonth   the {@link YearMonth}
     * @return  the JSON for that {@link YearMonth}
     */
    public static JSONString serializeYearMonth(YearMonth yearMonth) {
        return new JSONString(yearMonth.toString());
    }

    /**
     * Serialize a {@link BitSet}.
     *
     * @param   bitSet  the {@link BitSet}
     * @return  the JSON for that {@link BitSet}
     */
    public static JSONArray serializeBitSet(BitSet bitSet) {
        JSONArray array = new JSONArray();
        for (int i = 0, n = bitSet.length(); i < n; i++)
            if (bitSet.get(i))
                array.addValue(i);
        return array;
    }

    /**
     * Serialize an {@link Optional}.
     *
     * @param   optional    the {@link Optional}
     * @return  the JSON for that {@link Optional}
     */
    public static JSONValue serializeOptional(Optional<?> optional) {
        return optional.isPresent() ? serialize(optional.get()) : null;
    }

    /**
     * Serialize an {@link OptionalInt}.
     *
     * @param   optional    the {@link OptionalInt}
     * @return  the JSON for that {@link OptionalInt}
     */
    public static JSONValue serializeOptionalInt(OptionalInt optional) {
        return optional.isPresent() ? serialize(optional.getAsInt()) : null;
    }

    /**
     * Serialize an {@link OptionalLong}.
     *
     * @param   optional    the {@link OptionalLong}
     * @return  the JSON for that {@link OptionalLong}
     */
    public static JSONValue serializeOptionalLong(OptionalLong optional) {
        return optional.isPresent() ? serialize(optional.getAsLong()) : null;
    }

    /**
     * Serialize an {@link OptionalDouble}.
     *
     * @param   optional    the {@link OptionalDouble}
     * @return  the JSON for that {@link OptionalDouble}
     */
    public static JSONValue serializeOptionalDouble(OptionalDouble optional) {
        return optional.isPresent() ? serialize(optional.getAsDouble()) : null;
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

        // TODO check class-based annotations, including option to apply @JSONAlways on all

        // first deal with fields of superclass

        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class))
            addFieldsToJSONObject(jsonObject, superClass, object);

        // now, for each field in this class

        for (Field field : objectClass.getDeclaredFields()) {

            // ignore fields marked as static or transient, or annotated with @JSONIgnore

            if (!fieldStaticOrTransient(field) && !fieldAnnotated(field, JSONIgnore.class)) {

                // check for explicit name annotation

                String fieldName = field.getName();
                JSONName nameAnnotation = field.getAnnotation(JSONName.class);
                if (nameAnnotation != null) {
                    String nameValue = nameAnnotation.value();
                    if (nameValue != null)
                        fieldName = nameValue;
                }

                // add the field to the object if not null, or if annotated with @JSONAlways

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value != null) {
                        if (value instanceof Optional) {
                            Optional<?> optional = (Optional<?>)value;
                            if (optional.isPresent())
                                jsonObject.put(fieldName, serialize(optional.get()));
                            else if (fieldAnnotated(field, JSONAlways.class))
                                jsonObject.putNull(fieldName);
                        }
                        else if (value instanceof OptionalInt) {
                            OptionalInt optional = (OptionalInt)value;
                            if (optional.isPresent())
                                jsonObject.putValue(fieldName, optional.getAsInt());
                            else if (fieldAnnotated(field, JSONAlways.class))
                                jsonObject.putNull(fieldName);
                        }
                        else if (value instanceof OptionalLong) {
                            OptionalLong optional = (OptionalLong)value;
                            if (optional.isPresent())
                                jsonObject.putValue(fieldName, optional.getAsLong());
                            else if (fieldAnnotated(field, JSONAlways.class))
                                jsonObject.putNull(fieldName);
                        }
                        else if (value instanceof OptionalDouble) {
                            OptionalDouble optional = (OptionalDouble)value;
                            if (optional.isPresent())
                                jsonObject.putValue(fieldName, optional.getAsDouble());
                            else if (fieldAnnotated(field, JSONAlways.class))
                                jsonObject.putNull(fieldName);
                        }
                        else
                            jsonObject.put(fieldName, serialize(value));
                    }
                    else if (fieldAnnotated(field, JSONAlways.class))
                        jsonObject.putNull(fieldName);
                }
                catch (JSONException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new JSONException("Error serializing " + objectClass.getName() + '.' +
                            fieldName);
                }

            }

        }

    }

    private static boolean fieldStaticOrTransient(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers);
    }

    private static boolean fieldAnnotated(Field field,
            Class<? extends Annotation> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }

}
