# jsonauto

JSON auto serialize / deserialize from / to Java.

## Background

I am not a big fan of reflection-based automatic serialization and deserialization of Java
objects to and from JSON - reflection is always slower than explicit serialization and it
sometimes gives unexpected results.  But I know that many people prefer it so I've added it as
an optional additional package to my `jsonutil` library.

## Usage

To serialize any object:

```java
JSONValue json = JSONSerializer.serialize(object);
String text = json.toJSON();
```

Nested objects are handled automatically, as are many of the commonly-used classes from
`java.lang` and `java.util` (more documentation to follow).

To deserialize:

```java
JSONValue json = JSON.parse(text);
UserClass userObject = JSONDeserializer.deserialize(UserClass.class, json);
```

See the JavaDoc for more information.

## Annotations

Annotations are available to specify that a field is to be ignored, or always included even if
null (the default behaviour is to ignore null fields).  The field name may also be specified.

* `@JSONIgnore` - never serialize
* `@JSONAlways` - always serialize
* `@JSONName("name")` - use the specified name when serializing


## Maven

The library is in the Maven Central Repository; the co-ordinates are:

```xml
<dependency>
  <groupId>net.pwall.util</groupId>
  <artifactId>jsonauto</artifactId>
  <version>1.0</version>
</dependency>
```
