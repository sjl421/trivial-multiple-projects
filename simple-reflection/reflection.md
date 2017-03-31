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

If you know the precise parameter types of the method you want to access, you can do so rather than obtain the array all methods. This example returns the public method named "doSomething", in the given class which takes a String as parameter:

```java
Method method = clazz.getMethod("doSomething", new Class[]{String.class});
```

If no method matches the given method name and arguments, in this case String.class, a NoSuchMethodException is thrown.

If the method you are trying to access takes no parameters, pass null as the parameter type array, like this:

```java
Method method = clazz.getMethod("doSomething", null);
```

### Method Parameters and Return Types

You can read what parameters a given method takes like this:

```java
Class[] parameterTypes = method.getParameterTypes();
```

You can access the return type of a method like this:

```java
Class returnType = method.getReturnType();
```

### Invoking Methods using Method Object

You can invoke a method like this:

```java
// get method that takes a string as argument
Method method = MyObject.getMethod("doSomething", String.class);
Object returnValue = method.invoke(null, "parameter-value1");
```

The null parameter is the object you want to invoke the method on. If the method is `static` you supply null instead of an object instance. In this example, if doSomething(String.class) is not static, you need to supply a valid MyObject instance instead of null;

The Method.invoke(Object target, Object ... parameters) method takes an optional amount of parameters, but you must supply exactly one parameter per argument in the method you are invoking. In this case it was a method taking a String, so one String must be supplied.

## Getters and Setters

This can be used to detect what getters and setters a given class has. You cannot ask for getters and setters explicitly, so you will have to scan through all the methods of a class and check if each method is a getter or setter.

First let's establish the rules that characterizes getters and setters:

- **Getter** : A getter method have its name start with "get", take 0 parameters, and returns a value. 
- **Setter** : A setter method have its name start with "set", and takes 1 parameter.

Setters may or may not return a value. Some setters return void, some the value set, others the object the setter were called on for use in method chaining. Therefore you should make no assumptions about the return type of a setter.

```java
public class GetterAndSetter {

    private static void printGettersSetters(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (isGetter(method))
                System.out.println("setter = " + method);
            if (isSetter(method))
                System.out.println("getter = " + method);
        }
    }

    public static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) return false;
        if (method.getParameterCount() != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;

        return true;
    }

    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) return false;
        if (method.getParameterCount() != 1) return false;
        return true;
    }
}
```

## Private Fields and Methods

### Accessing Private Fields

To access a private field you will need to call the `Class.getDeclaredField(String name)` or `Class.getDeclaredFields()` method. The methods Class.getField(String name) and Class.getFields() methods only return public fields, so they won't work. Here is a simple example of a class with a private field, and below that the code to access that field via Java Reflection:

```java
public class PrivateField {

    public static void main(String[] args) throws IllegalAccessException {
        PrivateObject privateObject = new PrivateObject();
        privateObject.setPrivateString("private string");
        Field[] fields = privateObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println("field = " + field);
            // field = private java.lang.String com.xxx.classes.PrivateField$PrivateObject.privateString
            // field = public java.lang.String com.xxx.classes.PrivateField$PrivateObject.publicString
        }

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                field.setAccessible(true);
            }
            System.out.println("field.get(privateObject) = " + field.get(privateObject));
            // field.get(privateObject) = private string
            // field.get(privateObject) = null
        }
    }

    private final static class PrivateObject {
        private String privateString;
        public String publicString;

        public String getPrivateString() {
            return privateString;
        }

        public void setPrivateString(String privateString) {
            this.privateString = privateString;
        }

        public String getPublicString() {
            return publicString;
        }

        public void setPublicString(String publicString) {
            this.publicString = publicString;
        }
    }
}
```

Notice the use of the method `PrivateObject.class.getDeclaredField("privateString")`. It is this method call that returns the private field. This method only returns fields declared in that particular class, not fields declared in any superclasses.

### Accessing Private Methods

To access a private method you will need to call the `Class.getDeclaredMethod(String name, Class[] parameterTypes)` or `Class.getDeclaredMethods()` method. The methods `Class.getMethod(String name, Class[] parameterTypes)` and `Class.getMethods()` methods only return public methods, so they won't work. Here is a simple example of a class with a private method, and below that the code to access that method via Java Reflection:

```java
public class PrivateMethodTest {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        PrivateMethod privateMethod = new PrivateMethod();
        privateMethod.name = "xxx";
        privateMethod.age = 20;

        Method[] methods = PrivateMethod.class.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPrivate(method.getModifiers())) {
                method.setAccessible(true);
            }
            System.out.println("method = " + method);
            // method = private int com.xxx.demo.PrivateMethodTest$PrivateMethod.getAge()
            // method = private java.lang.String com.xxx.demo.PrivateMethodTest$PrivateMethod.getName()
            // method = static java.lang.String com.xxx.demo.PrivateMethodTest$PrivateMethod.access$102(com.xxx.demo.PrivateMethodTest$PrivateMethod,java.lang.String)
            // method = static int com.xxx.demo.PrivateMethodTest$PrivateMethod.access$202(com.xxx.demo.PrivateMethodTest$PrivateMethod,int)
            Class<?> returnType = method.getReturnType();

            System.out.println("returnType = " + returnType);
            // returnType = int
            // returnType = class java.lang.String
            // returnType = class java.lang.String
            // returnType = int

            if (method.getParameterCount() == 0) {
                Object invoke = method.invoke(privateMethod);
                System.out.println("invoke = " + invoke);
                // invoke = 20
                // invoke = xxx
            }
        }
    }

    private static class PrivateMethod {
        private String name;
        private int age;

        private String getName() {
            return name;
        }

        private int getAge() {
            return age;
        }
    }
}
```

>As we can see, there is more method as we expeceted which because we use a inner static class.

## Annotations

## What are Java Annotations?

Annotations is a new feature from Java 5. Annotations are a kind of comment or meta data you can insert in your Java code. These annotations can then be processed at compile time by pre-compiler tools, or at runtime via Java Reflection. Here is an example of class annotation:

```java
@MyAnnotation(name="someName",  value = "Hello World")
public class TheClass {
}
```

The class TheClass has the annotation @MyAnnotation written ontop. Annotations are defined like interfaces. Here is the MyAnnotation definition:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface MyAnnotation {
    public String name();
	public String value();
}
```

### Class Annotations

You can access the annotations of a class, method or field at runtime. Here is an example that accesses the class annotations:

```java
Class clazz = TheClass.class;
Annotation[] annotations = clazz.getAnnotations();

for (Annotation annotation : annotations) {
	if (annotation instanceof MyAnnotation) {
		MyAnnotation myAnnotation = (MyAnnotation) annotation;
		System.out.println("name = " + myAnnotation.name());
		System.out.println("value  = " + myAnnotation.value());
	}
}
```

### Method Annotations

```java
public class TheClass {
	@MyAnnotation(name="someName",  value = "Hello World")
	public void doSomething(){}
}
```

You can access method annotations like this:

```java
Method method = TheClass.class.getMethod("doSomething");
Annotation[] annotations = method.getDeclaredAnnotations();

for(Annotation annotation : annotations){
    if(annotation instanceof MyAnnotation){
        MyAnnotation myAnnotation = (MyAnnotation) annotation;
        System.out.println("name: " + myAnnotation.name());
        System.out.println("value: " + myAnnotation.value());
    }
}
```

## Generics

It is possible to access generics information at runtime in a handful of cases.

### The Generics Reflection Rule of Thumb

Using Java Generics typically falls into one of two different situations:
	1. Declaring a class/interface as being parameterizable.
	2. Using a parameterizable class.

When you write a class or interface you can specify that it should be paramerizable. This is the case with the `java.util.List` interface. Rather than create a list of Object you can parameterize java.util.List to create a list of say String.

When runtime inspecting a parameterizable type itself, like java.util.List, there is no way of knowing what type is has been parameterized to. This makes sense since the type can be parameterized to all kinds of types in the same application. But, when you inspect the **method or field** that declares the use of a parameterized type, you can see at runtime what type the paramerizable type was parameterized to. In short:

You cannot see on a type itself what type it is parameterized to a runtime, but you can see it in fields and methods where it is used and parameterized. 
Its concrete parameterizations in other words.

### Generic Method Return Types

If you have obtained a `java.lang.reflect.Method` object it is possible to obtain information about its generic return type. This cannot be any of the Method objects in the parameterized type, but in the class that uses the parameterized type.

```java
public class Generics {
    public static void main(String[] args) throws NoSuchMethodException {
        Method method = MyClass.class.getMethod("getStringList");
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
            }
        }
    }

    private static class MyClass {
        private List<String> stringList = new ArrayList<>();

        public List<String> getStringList() {
            return stringList;
        }

        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }
    }
}
```

```java
/**
 * Returns a {@code Type} object that represents the formal return type of the method 
 * represented by this {@code method} object.
 *
 * If the return type is a parameterized type, the {@code Type} object returned must
 * accurately reflect that actual type parameters used in the source code.
 *
 * If the return type is a type variable or a parameterized type, it is created.
 * Otherwise, it is resolved.
 *
 */
public Type getGenericReturnType() {
	if (getGenericSignature != null) {
		return getGenericInfo().getReturnType();
	} else {
		return getReturnType();
	}
}
```

```java
public class Generics {
    public static void main(String[] args) throws NoSuchMethodException {
        Method method = MyClass.class.getMethod("getStringList");

        Type returnType = method.getGenericReturnType();

        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();
        // sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedParameterizedTypeImpl@5ccd43c2
        System.out.println(annotatedReturnType);
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                // typeArgClass = class java.lang.String
            }
        }
    }

    private static class MyClass {
        private List<String> stringList = new ArrayList<>();

        public List<String> getStringList() {
            return stringList;
        }

        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }
    }
}
```

### Generic Method Parameter Types

You can also access the generic types of parameter types at runtime via Java Reflection. Here is an example class with a method taking a parameterized List as parameter:

```java
public class Test {
    private static class MyObj {
        public List<String> test;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        try {
            Field field = MyObj.class.getField("test");
            Class<?> type = field.getType();
            AnnotatedType annotatedType = field.getAnnotatedType();
            Type genericType = field.getGenericType();
            System.out.println("type = " + type);
            System.out.println("annotatedType = " + annotatedType);
            System.out.println("genericType = " + genericType);
			// type = interface java.util.List
			// annotatedType = sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedParameterizedTypeImpl@65ae6ba4
			// genericType = java.util.List<java.lang.String>
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
```

>we can use API under below:
>method.getGenericParameterTypes();
>method.getGenericType();

## Arrays

- java.lang.reflect.Array
- Creating Arrays
- Accessing Arrays
- Obtaining the Class Object of an Array
- Obtaining the Component Type of an Array

Working with arrays in Java Reflection can be a bit tricky at times. Especially if you need to obtain the Class object for a certain type of array, like int[] etc.

### java.lang.reflect.Array

Working with arrays via Java Reflection is done using the `java.lang.reflect.Array` class. Do not confuse this class with the `java.util.Arrays` class in the Java Collections suite, which contains utility methods for sorting arrays, converting them to collections etc.

### Creating Arrays

```java
int[] array = (int[]) Array.newInstance(int.class, 3);
```

### Accessing Arrays

It is also possible to access the elements of an array using Java Reflection. This is done via the Array.get(...) and Array.set(...) methods. Here is an example:

```java
public class AccessingArrays {
    public static void main(String[] args) {
        int[] intArray = (int[]) Array.newInstance(int.class, 3);
        Array.set(intArray, 0, 123);
        Array.set(intArray, 1, 465);
        Array.set(intArray, 2, 789);

        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 0));
        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 1));
        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 2));
        // Array.get(intArray, 0) = 123
        // Array.get(intArray, 0) = 465
        // Array.get(intArray, 0) = 789
    }
}
```

### Obtaining the Class Object of an Array

One of the problems I ran into when implementing the script language in Butterfly DI Container was how to obtain the Class object for arrays via Java Reflection. Using non-reflection code you can do like this:

```java
Class stringArrayClass = String[].class;
```

Doing this using Class.forName() is not quite straightforward. For instance, you can access the primitive int array class object like this:

```java
Class intArray = Class.forName("[I");
```

The JVM represents an int via the letter I. The [ on the left means it is the class of an int array I am interested in. This works for all other primitives too.

For objects you need to use a slightly different notation:

```java
Class stringArrayClass = Class.forName("[Ljava.lang.String;");
```

Notice the `[L` to the left of the class name, and the `;` to the right. This means an array of objects with the given type.

As a side note, you cannot obtain the class object of primitives using Class.forName(). Both of the examples below result in a `ClassNotFoundException`:

```java
Class intClass1 = Class.forName("I");
Class intClass2 = Class.forName("int");
```

I usually do something like this to obtain the class name for primitives as well as objects:

```java
public Class getClass(String className) {
	if ("int".equals(className)) return int.class;
	if ("long".equals(className)) return long.class;

	return Class.forName(className);
}
```

Once you have obtained the Class object of a type there is a simple way to obtain the Class of an array of that type. The solution, or workaround as you might call it, is to create an empty array of the desired type and obtain the class object from that empty array. It's a bit of a cheat, but it works. Here is how that looks:

```java
Class clazz = getClass(theClassName);
Class stringArrayClass = Array.newInstance(theClass, 0).getClass();
```

This presents a single, uniform method to access the array class of arrays of any type. No fiddling with class names etc.

To make sure that the Class object really is an array, you can call the Class.isArray() method to check:

```java
Class stringArrayClass = Array.newInstance(String.class, 0).getClass();
System.out.println("is array : " + stringArrayClass.isArray());
```

### Obtaining the Component Type of an Array

Once you have obtained the Class object for an array you can access its component type via the Class.getComponentType() method. The component type is the type of the items in the array. For instance, the component type of an int[] array is the int.class Class object. The component type of a String[] array is the java.lang.String Class object.

```java
String[] strings = new String[3];
Class stringArrayClass = strings.getClass();
Class stringArrayComponentType = stringArrayClass.getComponentType();
System.out.println(stringArrayComponentType);
```

## Dynamic Proxies

- Creating Proxies
- InvocationHandler's
- Known Use Cases
	- Database Connection and Transaction Management
	- Dynamic Mock Objects for Unit Testing
	- Adaptation of DI Container to Custom Factory Interfaces
	- AOP-like Method Interception
	
Using Java Reflection you create dynamic implementations of interfaces at runtime. You do so using the class java.lang.reflect.Proxy. The name of this class is why I refer to these dynamic interface implementations as dynamic proxies.

### Creating Proxies

You create dynamic proxies using the `Proxy.newProxyInstance()` method. The newProxyInstance() methods takes 3 parameters:
	1. The ClassLoader that is to "load" the dynamic proxy class.
	2. An array of interfaces to implement.
	3. An InvocationHandler to forward all methods calls on the proxy to.

```java
/**
 * {@code InvocationHandler} is the interface implemented by the invocation handler of
 * a proxy instance.
 *
 * Each proxy instance has an associated invocation handler.When a method is invoked on 
 * a proxy instance, the method invocation is encoded and dispatched to the {@code invoke}
 * method of its invocation handler.
 *
 */
public interface InvocationHandler {
	/**
	 * Processes a method invocation on a proxy instance and returns a result.
	 * This method will be invoked on an invocation handler when a method is invoked on
	 * a proxy instance that it is associated with.
	 *
	 * @param proxy 
	 * 			the proxy instance that the method was invoked on
	 * @param method
	 * 			the {@code Method} instance corresponding to the interface method invoked
	 * 			on the proxy instance. The declaring class of the {@code Method} object will
	 * 			be the interface that the method was declared in, which may be a superinterface
	 * 			of the proxy interface that the proxy class inherits the method through.
	 * @param args
	 * 			an array of objects containing the values of the argument passed in the method
	 * 			invocation n the proxy instance, or {@code null} if interface method takes no
	 * 			argument.Arguments of primitive types are wrapped in instances of the
	 * 			primitive wrapper class, such as {@code java.lang.Integer} or 
	 * 			{@code java.lang.Boolean}.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
```

```java
/**
 * {@code proxy} provides static methods for creating dynamic proxy classes and interfaces,
 * and it is also the superclass of all dynamic proxy classes created by those methods.
 *
 * A dynamic proxy class (simply referred to as a proxy class below) is a class that implements
 * a list of interfaces specified at runtime when the class is created, with behavior as
 * described below.
 *
 * A proxy interface is such a interface that is implemented by a proxy class.
 *
 * Each proxy instance has an associated invocation handler object, which implements the
 * interface {@link InvocationHandler}. A method invocation on a proxy instance through
 * one of its proxy interfaces will be dispatched to the 
 * {@link InvocationHandler#invoke invoke} method of the instance's invocation handler,
 * passing the proxy instance, a {@code java.lang.reflect.Method} object identifying the 
 * method that was invoked, and an array of type {@code Object} containing the arguments.
 * The invocation handler processes the encoded method invocation as appropriate and the
 * the result that is returns will be returned as the result of the method invocation on
 * the proxy instance.
 *
 * A proxy class has the following properties:
 *
 * 		1. Proxy classes are public, final, and not abstract if all proxy interface are public.
 * 		2. Proxy classes are non-public, final, and not abstract if any of the proxy interfaces
 * 		is non-public
 * 		3. The unqualified name of a proxy class is unspecified.The space of class names that
 * 		begin with the string {@code "$Proxy"} should be, however, reserved for proxy classes.
 * 		4. A proxy class extends {@code java.lang.reflect.Proxy}
 * 		5. A proxy class implements exactly the interfaces specified at its creating, in the
 * 		same order.
 * 		6. If a proxy class implements a non-public interface, then it will be defined in the
 * 		same package as that interface. Otherwise, the package of a proxy class is aslo
 * 		unspecified. Note that package sealing will not prevent a proxy class from being
 * 		successfully defined in a particular package at runtime, and neither will classes
 * 		already defined by the same class loader and the same package with particular signers.
 *
 * 
 * read the document.
 */
public class Proxy implements java.io.Serializable {
	@CallerSensitive
	public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) 
			throws IllegalArgumentException
	{
	
	}

	/**
	 * Returns an instance of a proxy class for the specified interfaces that dispatches
	 * method invocations to the specified invocation handler.
	 *
	 * {@code Proxy.newProxyInstance} throws {@code IllegalArgumentException} for the same
	 * reasons that {@code Proxy.getProxyClass} does.
	 *
	 * @param loader
	 * 			the class loader to define the proxy class
	 * @param interfaces
	 * 			the list of interfaces for the proxy class to implement
	 * @param h
	 * 			the invocation handler to dispatch method invocation to
	 *
	 * @return 
	 * 			a proxy instance with the specified invocation handler of a proxy class that
	 * 			implements the specified interfaces.
	 */
	@CallerSensitive
	public static Object newProxyInstance(ClassLoader loader, 
										  Class<?>[] interfaces, 
										  InvocationHandler h)
				throws IllegalArgumentException
	{
	
	}
}
```

```java
public interface ITransactionManager {
    void transaction();
}
```

```java
public class StrongTM implements ITransactionManager {
    @Override
    public void transaction() {
        System.out.println("strong tm is running");
    }
}
```

```java
public class WeakTm implements ITransactionManager {
    @Override
    public void transaction() {
        System.out.println("weak tm is running");
    }
}
```

```java
public class TransactionInvocationHandler implements InvocationHandler { 

    private ITransactionManager transactionManager;

    public TransactionInvocationHandler(ITransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("start transaction");
        Object invokeResult = method.invoke(transactionManager, args);
        System.out.println("commit");

        return invokeResult;
    }

}
```

```java
public class TransactionInvocationHandler implements InvocationHandler { 

    private ITransactionManager transactionManager;

    public TransactionInvocationHandler(ITransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("start transaction");
        method.invoke(transactionManager, args);
        System.out.println("commit");

        return null;
    }

}
```



### Known Use Cases

- Database Connection and Transaction Management
- Dynamic Mock Objects for Unit Testing
- Adaptation of DI Container to Custom Factory Interfaces
- AOP-like Method Interception

#### Database Connection and Transaction Management

The Spring framework has a transaction proxy that can start and commit / rollback a transaction for you. How this works is described in more detail in the text [Advanced Connection and Transaction Demarcation and Propagation](http://tutorials.jenkov.com/java-persistence/advanced-connection-and-transaction-demarcation-and-propagation.html) , so I'll only describe it briefly. The call sequence becomes something along this:

```
web controller --> proxy.execute(...);
  proxy --> connection.setAutoCommit(false);
  proxy --> realAction.execute();
    realAction does database work
  proxy --> connection.commit();
```

## Dynamic Class Loading and Reloading

- The ClassLoader
- The ClassLoader Hierarchy
- Class Loading
- Dynamic Class Loading
- Dynamic Class Reloading
- Designing your Code for Class Reloading
- ClassLoader Load / Reload Example

### The ClassLoader

All classes in a Java application are loaded using some subclass of `java.lang.ClassLoader`. Loading classes dynamically must therefore also be done using a `java.lang.ClassLoader` subclass.

When a class is loaded, all classes it references are loaded too. **This class loading pattern happens recursively**, until all classes needed are loaded. This may not be all classes in the application. Unreferenced classes are not loaded until the time they are referenced.

### Class Loading

The steps a given class loader uses when loading classes are:

1. Check if the class was already loaded.
2. If not loaded, ask parent class loader to load the class.
3. If parent class loader cannot load class, attempt to load it in this class loader.

When you implement a class loader that is capable of reloading classes you will need to deviate a bit from this sequence. The classes to reload should not be requested loaded by the parent class loader. More on that later.
