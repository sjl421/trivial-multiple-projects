# [Java Reflection](http://tutorials.jenkov.com/java-reflection/index.html)

## tutorial

Java Reflection makes it possible to inspect classes, interfaces, fields and methods at runtime, without knowing the names of the classes, methods etc. at compile time. It is also possible to instantiate new objects, invoke methods and get/set field values using reflection.

### Java Reflection Example

```java
public class PrintMethods {
    public static void main(String[] args) {
        Object obj = new Object();

        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            System.out.println("methodName = " + method.getName());
        }
    }
}
```

## Java Reflection - Classes

Using Java Reflection you can inspect Java classes at runtime. Inspecting classes is often the first thing you do when using Reflection. From the classes you can obtain information about


- Class Name
- Class Modifies(public, private, synchronized etc.)
- Package Info
- Superclass
- Implemented Interfaces
- Constructors
- Methods
- Fields
- Annotations

plus a lot more information related to Java classes. For a full list you should consult the [JavaDoc for java.lang.Class.](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html). This text will briefly touch upon all accessing of the above mentioned information. Some of the topics will also be examined in greater detail in separate texts. For instance, this text will show you how to obtain all methods or a specific method, but a separate text will show you how to invoke that method, how to find the method matching a given set of arguments if more than one method exists with the same name, what exceptions are thrown from method invocation via reflection, how to spot a getter/setter etc. The purpose of this text is primarily to introduce the Class object and the information you can obtain from it.

### The Class Object

Before you can do any inspection on a class you need to obtain its `java.lang.Class` object. All types in Java including the primitive types (int, long, float etc.) including arrays have an associated Class object. If you know the name of the class at compile time you can obtain a Class object like this:

```java
Class<MyObject> myObjectClass = MyObject.class;
```

If you don't know the name at compile time, but have the class name as a string at runtime, you can do like this:

```java
String className = "java.lang.Object";
Class<?> clazz = Class.forName(className);
Object o = clazz.newInstance();
```

When using the `Class.forName()` method you must supply the **fully qualified class name**. That is the class name including all package names. 

### Class Name

From a Class object you can obtain its name in two versions. The fully qualified class name (including package name) is obtained using the getName() method;

If you want the class name without the pacakge name you can obtain it using the getSimpleName() method, like this:

```java
Class clazz = Object.class;
String simpleClassName = clazz.getSimpleName();
```

### Modifiers

You can access the modifiers of a `class` via the Class object. The class modifiers are the keywords "public", "private", "static" etc. You obtain the class modifiers like this:

```java
public class PrintModifiers {
    public static void main(String[] args) {
        Class<Object> clazz = Object.class;
        for (Method method : clazz.getMethods()) {
            int modifiers = method.getModifiers();
            System.out.println(Modifier.isAbstract(modifiers));
            System.out.println(Modifier.isFinal(modifiers));
            System.out.println(Modifier.isInterface(modifiers));
            System.out.println(Modifier.isNative(modifiers));
            System.out.println(Modifier.isPrivate(modifiers));
            System.out.println(Modifier.isProtected(modifiers));
            System.out.println(Modifier.isPublic(modifiers));
            System.out.println(Modifier.isStatic(modifiers));
            // strictfp representing strict float point
            System.out.println(Modifier.isStrict(modifiers));
            System.out.println(Modifier.isSynchronized(modifiers));
            System.out.println(Modifier.isTransient(modifiers));
            System.out.println(Modifier.isVolatile(modifiers));
        }
    }
}
```

### Package Info

You can obtain information about the package from a `Class` object like this:

```java
public class Pack {
    public static void main(String[] args) {
        Class<Object> clazz = Object.class;
        Package pack = clazz.getPackage();
        System.out.println(pack.getName());
        for (Annotation annotation : pack.getAnnotations()) {
            System.out.println(annotation);
        }
        System.out.println("implementationTitle = [" + pack.getImplementationTitle() + "]");
        //
        System.out.println("implementationVendor = [" + pack.getImplementationVendor() + "]");
        System.out.println("implementationVersion = [" + pack.getImplementationVersion() + "]");
        // java.lang
        // implementationTitle = [Java Runtime Environment]
        // implementationVendor = [Oracle Corporation]
        // implementationVersion = [1.8.0_111]
    }
}
```

[read more about java.lang.Package](http://java.sun.com/javase/6/docs/api/java/lang/Package.html)

### Superclass

```java
public class Superclasses {
    private static class SubClass extends Superclasses {

    }

    public static void main(String[] args) {
        Class<SubClass> clazz = SubClass.class;
        System.out.println("clazz = " + clazz.getName());
        System.out.println("clazz.getSuperclass().getName() = " + clazz.getSuperclass().getName());
        System.out.println("clazz.getSuperclass().getSuperclass().getName() = " + clazz.getSuperclass().getSuperclass().getName());
		// clazz = com.xxx.classes.Superclasses$SubClass
		// clazz.getSuperclass().getName() = com.xxx.classes.Superclasses
		// clazz.getSuperclass().getSuperclass().getName() = java.lang.Object
    }
}
```

### Implemented Interfaces

It is possible to get a list of the interfaces implemented by a given class. Here is how:

```java
public class Interfaces {
    private interface ExplicitIn {}
    private interface ImplicitIn {}
    private static class SubClass implements ExplicitIn, ImplicitIn {}
    private static class SubSubClass extends SubClass implements ExplicitIn  {}

    public static void main(String[] args) {
        Class<SubSubClass> clazz = SubSubClass.class;
        for (Class<?> c : clazz.getInterfaces()) {
            System.out.println("The interface of SubSubClass = " + c.getSimpleName());
        }
		// The interface of SubSubClass = ExplicitIn

        Class<SubClass> subClazz = SubClass.class;
        for (Class<?> c : subClazz.getInterfaces()) {
            System.out.println("The interface of SubClass = " + c.getSimpleName());
        }
		// The interface of SubClass = ExplicitIn
		// The interface of SubClass = ImplicitIn
    }
}
```

A class can implement many interfaces. Therefore an array of `Class` is returned. Interfaces are also represented by Class objects in Java Reflection.

NOTE: Only the interfaces specifically declared implemented by a given class is returned. If a superclass of the class implements an interface, but the class doesn't specifically state that it also implements that interface, that interface will not be returned in the array. Even if the class in practice implements that interface, because the superclass does.

To get a complete list of the interfaces implemented by a given class you will have to consult both the class and its superclasses recursively.

### Constructors

You can access the constructors of a class like this:

```java
public class Constructors {
    public static void main(String[] args) {
        Class<Constructors> clazz = Constructors.class;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            System.out.println("c.getName() = " + c.getName());
        }
    }
}
```

### Fields

You can access the fields (member variables) of a class like this:

```java
public class Fields {
    public static class SubClass {
        private String privateSubClassField;
        public String publicSubClassField;
    }

    private static class SubSubClass extends SubClass {
        private String privateSubSubClassField;
        public String publicSubSubClassField;
    }
    public static void main(String[] args) {
        Class<SubSubClass> clazz = SubSubClass.class;
        for (Field field : clazz.getFields()) {
            System.out.println("field.getName() = " + field.getName());
        }
		// field.getName() = publicField
		// field.getName() = publicSupField

        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("field.getName() = " + field.getName());
        }
		// field.getName() = privateField
		// field.getName() = friendlyField
		// field.getName() = protectedField
		// field.getName() = publicField
    }
}
```

1. clazz.getFields() can get all public field include super class;
2. clazz.getDeclaredFields() can get all field which declared in the class.

### Annotations

You can access the class annotations of a class like this:

```java
clazz.getAnnotations();
clazz.getDeclaredAnnotations();
clazz.getAnnotatedInterfaces();
clazz.getAnnotatedSuperclass();
```

```java
/**
 * Type is the common superinterface for all types in the Java programming language.
 *
 * These include raw types, parameterized types, array types, type avaiables and
 * primitive types.
 *
 */
public interface Type {
	/**
	 * Returns a string describing this type, including information about any type parameters.
	 *
	 */
	default String getTypeName() {
		return toString();
	}
}
```

```java
/**
 * {@code AnnotatedType} represents the potentially annotated use of a type in the 
 * program currently running in this VM. The use may be of any type in the Java 
 * Programming language, including an array type, a parameterized type, a type
 * variable, or a wildcard type.
 */
public interface AnnotatedType extends AnnotatedElement {
	/**
	 * Returns the underlying type that this annotated type represents.
	 *
	 * @return the type this annotated type represents
	 */
	public Type getType();
}
```

```java
/**
 * Returns an array of {@code AnnotatedType} objects that represent the use of types to
 * specify superinterfaces of the entity represented by this {@code Class} object.
 */
public AnnotatedType[] getAnnotatedInterfaces() {
	return TypeAnnotationParser.buildAnnotatedInterfaces(getRawTypeAnnotations(), getConstantPool(), this);
}
```

## Constructors

- Obtaining Constructor Objects
- Constructor Parameters
- Instantiating Objects using Constructor Object

### Obtaining Constructor Objects

The Constructor class is obtained from the Class object. Here is an example:

```java
Class clazz = Object.class;
Constructor[] constructors = clazz.getConstructors();
```

The Constructor[] array will have one Constructor instance for each public constructor declared in the class.

If you know the precise parameter types of the constructor you want to access, you can do so rather than obtain the array all constructors. This example returns the public constructor of the given class which takes a String as parameter:

```java
Constructor constructor = clazz.getConstructor(new Class[]{String.class});
```

If no constructor matches the given constructor arguments, in this case String.class, a NoSuchMethodException is thrown.

### Constructor Parameters

```java
Class[] parameterTypes = constructor.getParameterTyps();
```

### Instantiating Objects using Constructor Object

```java
//get constructor that takes a String as argument
Constructor constructor = MyObject.class.getConstructor(String.class);
MyObject myObject = (MyObject) constructor.newInstance("constructor-arg1");
```

The `Constructor.newInstance()` method takes an optional amount of parameters, but you must supply exactly one parameter per argument in the constructor you are invoking. In this case it was a constructor taking a String, so one String must be supplied.

## Fields

- Obtaining Field Objects
- Field Name
- Field Type
- Getting and Setting Field Values

### Obtaining Field Objects

The Field class is obtained from the Class object. Here is an example:

```java
Field[] fields = clazz.getFields();
```

The Field[] array will have one Field instance for each public field declared in the class.

If you know the name of the field you want to access, you can access it like this:

```java
Field field = clazz.getField("someField");
```

### Field Name && Field Type

Once you have obtained a `Field` instance, you can get its field name using the Field.getName() method, like this:

```java
// fieldName
String fieldName = field.getName();

// fieldType
Object fieldType = field.getType();
```

### Getting and Setting Field Values

Once you have obtained a Field reference you can get and set its values using the Field.get() and Field.set()methods, like this:

```java
Class clazz = MyObject.class;
Field field = clazz.getField("someField");

MyObject objectInstance = new MyObject();

Object value = field.get(objectInstance);

field.set(objectInstance, value);
```

## Methods

### Obtaining Method Objects

The Method class is obtained from the Class object. Here is an example:

```java
Method[] methods = clazz.getMethods();
```

The Method[] array will have one Method instance for each public method declared in the class.


