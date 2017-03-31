## Description

[cglib-sample](https://github.com/cglib/cglib/tree/master/cglib-sample)

```java
public abstract class Bean implements java.io.Serializable {
    String sampleProperty;

    abstract public void addPropertyChangeListener(PropertyChangeListener listener);

    abstract public void removePropertyChangeListener(PropertyChangeListener listener);

    public String getSampleProperty() {
        return sampleProperty;
    }

    public void setSampleProperty(String sampleProperty) {
        this.sampleProperty = sampleProperty;
    }

    @Override
    public String toString() {
        return "sampleProperty is " + sampleProperty;
    }
}
```

```java
/**
 * 
 */
public class Beans implements MethodInterceptor {

    static final Class c[] = new Class[0];
    static final Object emptyArgs[] = new Object[0];

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println(method.getName());
        Object retValFromSuper = null;
        try {
            if (!Modifier.isAbstract(method.getModifiers())) {
                retValFromSuper = proxy.invokeSuper(obj, args);
            }
        } finally {
            String name = method.getName();
            if (name.equals("addPropertyChangeListener")) {
                addPropertyChangeListener((PropertyChangeListener) args[0]);
            } else if (name.equals("removePropertyChangeListener")) {
                removePropertyChangeListener((PropertyChangeListener) args[0]);
            }
            if (name.startsWith("set") &&
                    args.length == 1 &&
                    method.getReturnType() == Void.TYPE) {

                char propName[] = name.substring("set".length()).toCharArray();

                propName[0] = Character.toLowerCase(propName[0]);
                propertySupport.firePropertyChange(new String(propName), null, args[0]);
            }
        }

        return retValFromSuper;
    }

    public static Object newInstance (Class clazz) {
        try {
            Beans interceptor = new Beans();
            Enhancer e = new Enhancer();
            e.setSuperclass(clazz);
            e.setCallback(interceptor);
            Object bean = e.create();
            interceptor.propertySupport = new PropertyChangeSupport(bean);

            return bean;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    private PropertyChangeSupport propertySupport;

    public static void main(String[] args) {
        Bean bean = (Bean) newInstance(Bean.class);
        bean.addPropertyChangeListener(System.out::println);
        bean.setSampleProperty("TEST");
    }
}
```

---

```java
public class KeySample {
    private interface MyFactory {
        Object newInstance(int a, char[] b, String d);
    }

    public static void main(String[] args) {
        MyFactory f = (MyFactory) KeyFactory.create(MyFactory.class);
        Object key1 = f.newInstance(20, new char[]{'a', 'b'}, "hello");
        Object key2 = f.newInstance(20, new char[]{'a', 'b'}, "hello");
        Object key3 = f.newInstance(20, new char[]{'a', '-'}, "hello");
        System.out.println(key1.equals(key2));
		// true
        System.out.println(key2.equals(key3));
		// false
    }
}
```

```java
/**
 * Generates classes to handle multi-valued keys, for use in things such as Maps and Sets.

 * Code for equals and hashCode methods follow the rules laid out in Effective Java.
 *
 * To generate a KeyFactory, you need to supply an interface which describes the structure of
 * the key.The interface should have a single method named newInstance, which returns a Object.
 * The arguments array can be anything--Object, primitive values, or single or multi-dimension
 * arrays of either.
 */
abstract public class KeyFactory {
}
```

---

```java
/**
 * Created by dhy on 17-3-31.
 * 使用动态代理输出 Vector 执行的每一步
 */
public class Trace implements MethodInterceptor {
    int line = 1;
    static Trace callback = new Trace();

    /** Creates a new instance of Trace */
    private Trace() {

    }

    public static Object newInstance(Class clazz) {
        try {
            Enhancer e = new Enhancer();
            e.setSuperclass(clazz);
            e.setCallback(callback);
            return e.create();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        List list = (List) newInstance(Vector.class);
        Object value = "TEST";
        list.add(value);
        list.contains(value);
        try {
            list.set(2, "ArrayIndexOutOfBounds");
        } catch (ArrayIndexOutOfBoundsException ignore) {
            // ignore
        }
        list.add(value + "1");
        list.add(value + "2");
        list.toString();
        list.equals(list);
        list.set( 0, null );
        list.toString();
        list.add(list);
        list.get(1);
        list.toArray();
        list.remove(list);
        list.remove("");
        list.containsAll(list);
        list.lastIndexOf(value);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        printLine(line);
        System.out.println(method);
        for (int i = 0; i < args.length; i++) {
            printLine(line);
            System.out.print("arg" + (i + 1) + ": ");
            if (obj == args[i])
                System.out.print("this");
            else
                System.out.println(args[i]);
        }
        line++;
        Object retValFromSuper = null;
        try {
            retValFromSuper = proxy.invokeSuper(obj, args);
            line--;
        } catch (Throwable t) {
            line--;
            printLine(line);
            System.out.println("throw " + t);
            System.out.println();
            throw t.fillInStackTrace();
        }
        printLine(line);
        System.out.print("return ");
        if (obj == retValFromSuper)
            System.out.println("this");
        else
            System.out.println(retValFromSuper);

        if (line == 1)
            System.out.println();

        return retValFromSuper;
    }

    void printLine(int line) {
        while(--line > 0) {
            System.out.print("......");
        }
        System.out.print(" ");
    }
}
```

## Home

cglib is a powerful, high performance and quality Code Generation Library, It is used to extend JAVA classes and implements interfaces at runtime.See [samples](https://github.com/cglib/cglib/tree/master/cglib-sample) and [API documentation](http://cglib.sourceforge.net/apidocs/index.html) to learn more about features.

## How To

### CGLIB and Java Security

Java security protects system resources from unauthorized access by untrusted code. Code can be identified by signer and code base url (jar or class file) it can be local or downloaded from network. Classes generated by CGLIB do not exist at configuration and JVM startup time (generated at runtime), but all generated classes have the same protection domain (signer and codebase) as cglib itself and can be used in WS or by RMI application with security manager. To grant permissions for generated classes grant permissions for cglib binaries. Default security configuration is in java.policy file. This is example policy file, it grants all permissions for cglib and generated code.

```java
grant codeBase "file:${user.dir}/jars/cglib.jar" {
	permission java.security.AllPermission;
}
```

### CGLIB and Java Serialization

Java objects can be serialized to binary streams, it is used to implement RMI too. **Serialization needs to load class before to deserialize object data**. It is possible there is no generated class on client or server for unmarshalled object,but serialization lets to replace objects in stream (writeReplace/readResolve contract). To add "writeReplace" method to proxy class declare this method in interface with exact signature specified by Java serialization. Implement writeReplace in interceptor. Proxy object can be replaced by handle, object stream invokes "readResolve" before to deserialize handle. Generate or find proxy class in "readResolve" method before to deserialize handle and return proxy instance.

### Access the generated byte[] array directly

Here is an example of just capturing the byte array:

```java
Enhancer e = new Enhancer();
e.setSuperclass(...);
// etc.
e.setStrategy(new DefaultGeneratorStrategy() {
	protected byte[] transform(byte[] b) {
		// do something with bytes here
	}
});
Object obj = e.create();
```

You can also easily hook in a ClassTransformer to affect the resulting class without having to re-parse the byte array, for example:

```java
e.setStrategy(new DefaultGeneratorStrategy() {
	protected ClassGenerator transform(ClassGenerator cg) {
	        return new TransformingGenerator(cg,
			        	new AddPropertyTransformer(new String[]{ "foo" },
					                         		new Class[]{ Integer.TYPE }));
}});
```

### Avoiding StackOverflowError

Common mistake is to cause recursion in MethodInterceptor implementation:

```java
  Object intercept(Object proxy, Method method,
                   MethodProxy fastMethod, Object args[]) throws Throwable {
    //ERROR 
    System.out.println(proxy.toString());
    //ERROR 
    return fastMethod.invoke(proxy,args);     
  }
```

invokeSuper method must be used to invoke super class method. It will throw AbstractMethodError if super method is abstract. See trace sample how to solve recursion problem caused by "this" parameter in "args[]" array.

### Optimizing Proxies

**Filter unused methods with CallbackFilter and use light Callback version if possible**. It can help to avoid hash lookup on method object if you use per method interceptors too.


## Tutorial

### Enhancer

Let's start with the `Enhancer` class, the probably most used class of the cglib library. **An enhancer allows the creation of Java proxies for `non-interface` types.** The `Enhancer` can be compared with the Java standard library's `Proxy` class which was introduced in Java 1.3. **The Enhancer dynamically creates a subclass of a given type but intercepts all method calls.** Other than with the `Proxy` class, this works for both class and interface types. The following example and some of the examples after are based on this simple Java POJO:

```java
public class SampleClass {
	public String test(String input) {
		return "Hello world!";
	}
}
```

Using cglib, the return value of `test(String)` method can easily be replaced by another value using an `Enhancer` and a `FixedValue` callback:

```java
@Test
public void testFixedValue() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(SampleClass.class);
    enhancer.setCallback((FixedValue) () -> "Hello cglib!");
    SampleClass proxy = (SampleClass) enhancer.create();
    assertEquals("Hello cglib!", proxy.test(null));
}
```

In the above example, the enhancer will return an instance of an instrumented subclass of `SampleClass` where all method calls return a fixed value which is generated by the anonymous `FixedValue` implementation above. The object is created by `Enhancer#create(Object...)` where the method takes any number of arguments which are used to pick any constructor of the enhanced class. (**Even though constructors are only methods on the Java byte code level, the Enhancer class cannot instrument constructors. Neither can it instrument static or final classes.**)If you only want to create a class, but no instance, Enhancer#createClass will create a Class instance which can be used to create instances dynamically. All constructors of the enhanced class will be available as delegation constructors in this dynamically generated class.

**Be aware that any method call will be delegated in the above example**, also calls to the methods defined in java.lang.Object. As a result, a call to proxy.toString() will also return "Hello cglib!". In contrast will a call to proxy.hashCode() result in a ClassCastException since the FixedValue interceptor always returns a String even though the Object#hashCode signature requires a primitive integer.

**Another observation that can be made is that final methods are not intercepted**.An example of such a method is `proxy#getClass` which will return something like ``"SampleClass$$EnhancerByCGLIB$$e277c63c"`` when it is invoked.This class name is generated randomly by cglib in order to avoid naming conflicts. Be aware of the different class of the enhanced instance when you are making use of explicit types in your program code.The class generated by cglib will however be in the same package as the enhanced class (and therefore be able to override package-private methods). Similar to final methods, the subclassing approach makes for the inability of enhancing final classes. Therefore frameworks as Hibernate cannot persist final classes.

```java
public class SampleClass {
    public String test(String input) {
        return "Hello world!";
    }

    private String testPrivate() {
        return "private method!";
    }

    public final String finalTest() {
        return "final method!";
    }

    @Test
    public void testFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback((FixedValue) () -> "Hello cglib!");
        SampleClass proxy = (SampleClass) enhancer.create();
        assertEquals("Hello cglib!", proxy.test(null));
        assertEquals("private method!", proxy.testPrivate());
        assertEquals("final method!", proxy.finalTest());
    }
}
```

Next, let us look at a more powerful callback class, the `InvocationHandler`, that can also be used with an Enhancer:

```java
public class TestInvocationHandler {
    @Test
    public void testInvocationHandler() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback((InvocationHandler) (proxy, method, args) -> {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello cglib!";
            } else {
                throw new RuntimeException("Do not know what to do.");
            }
        });
        SampleClass proxy = (SampleClass) enhancer.create();
        assertEquals("Hello cglib!", proxy.test(null));
        assertEquals("Hello cglib!", proxy.toString());
    }
}
```

This callback allows us to answer with regards to the invoked method. However, you should be careful when calling a method on the proxy object that comes with the `InvocationHandler#invoke` method. All calls on this method will be dispatched with the same InvocationHandler and might therefore result in an endless loop. In order to avoid this, we can use yet another callback dispatcher:

```java
public class TestMethodInterceptor {
    @Test
    public void testMethodInterceptor() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello cglib!";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        });
        SampleClass proxy = (SampleClass) enhancer.create();
        assertEquals("Hello cglib!", proxy.test(null));
        assertNotEquals("Hello cglib!", proxy.toString());
        assertEquals(Integer.class, Integer.valueOf(proxy.hashCode()).getClass());
    }
}
```

The `MethodInterceptor` allows full control over the intercepted method and offers some utilities for calling the method of the enhanced class in their original state. But why would one want to use other methods anyways? Because the other methods are more efficient and cglib is often used in edge case frameworks where efficiency plays a significant role. **The creation and linkage of the MethodInterceptor requires for example the generation of a different type of byte code and the creation of some runtime objects that are not required with the InvocationHandler**. Because of that, there are other classes that can be used with the Enhancer:

1. `LazyLoader`: Even though the `LazyLoader`'s only method has the same method signature as `FixedValue`, the `LazyLoader` is fundamentally different to the FixedValue interceptor. The LazyLoader is actually supposed to return an instance of a subclass of the enhanced class. This instance is requested only when a method is called on the enhanced object and then stored for future invocations of the generated proxy. This makes sense if your object is expensive in its creation without knowing if the object will ever be used. Be aware that some constructor of the enhanced class must be called both for the proxy object and for the lazily loaded object. Thus, make sure that there is another cheap (maybe protected) constructor available or use an interface type for the proxy. You can choose the invoked constructed by supplying arguments to Enhancer#create(Object...).
2. `Dispatcher` : The Dispatcher is like the LazyLoader but will be invoked on every method call without storing the loaded object. This allows to change the implementation of a class without changing the reference to it. Again, be aware that some constructor must be called for both the proxy and the generated objects.
3. `ProxyRefDispatcher` : This class carries a reference to the proxy object it is invoked from in its signature. This allows for example to delegate method calls to another method of this proxy. Be aware that this can easily cause an endless loop and will always cause an endless loop if the same method is called from within `ProxyRefDispatcher#loadObject(Object)`.
4. `NoOp` : The NoOp class does not what its name suggests. Instead, it delegates each method call to the enhanced class's method implementation.


```java
public class LazyLoad {

    public LazyLoad(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(LazyLoad.class);
        enhancer.setCallback((LazyLoader) () -> {
            System.out.println("create a lazy loader instance");
			// everytime enhancer.create && instance.getName invoke will output a line
            return new LazyLoad("xxx");
        });
//        enhancer.setCallback((Dispatcher) () -> {
//            System.out.println("create a lazy loader instance");
//            return new LazyLoad("xxx");
//        });
//		everytime the instance.getName() is invoke will output a line to the system.out
//		but enhancer.create will not output a line.
        LazyLoad instance = (LazyLoad) enhancer.create(new Class[]{String.class}, new Object[]{"xxx"});
        Assert.assertEquals("xxx", instance.getName());
        Assert.assertEquals("xxx", instance.getName());
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```java
public class ProxyRefDispatcherExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ProxyRefDispatcherExample.class);
        enhancer.setCallback((ProxyRefDispatcher) proxy -> new ProxyRefDispatcherExample());
        ProxyRefDispatcherExample instance = (ProxyRefDispatcherExample) enhancer.create();
        instance.getName();
    }

    public void getName() {
        System.out.println("ProxyRefDispatcherExample#getName");
    }
}
```

```java
public class NoOpExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(NoOpExample.class);
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return 0;
            }
        });
        NoOpExample instance = (NoOpExample) enhancer.create();
        instance.getName();
        System.out.println(instance.hashCode());
		// not zero
    }

    public void getName() {
        System.out.println("NoOpExample#getName");
    }
}
```

At this point, the last two interceptors might not make sense to you. Why would you even want to enhance a class when you will always delegate method calls to the enhanced class anyways? And you are right. These interceptors should only be used together with a `CallbackFilter` as it is demonstrated in the following code snippet:

```java
public class CallbackFilterExample {
    public void callbackFilter() {
        Enhancer enhancer = new Enhancer();
        CallbackHelper callbackHelper = new CallbackHelper(SampleClass.class, new Class[0]) {
            @Override
            protected Object getCallback(Method method) {
                if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                    return (FixedValue) () -> "Hello cglib!";
                } else {
                    return NoOp.INSTANCE;
                }
            }
        };
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallbackFilter(callbackHelper);
        enhancer.setCallbacks(callbackHelper.getCallbacks());
        SampleClass proxy = (SampleClass) enhancer.create();
        System.out.println(proxy.test(null));
        System.out.println(proxy.toString());
        System.out.println(proxy.hashCode());
    }

    public static void main(String[] args) {
        CallbackFilterExample instance = new CallbackFilterExample();
        instance.callbackFilter();
    }
}
```

The `Enhancer` instance accepts a `CallbackFilter` in its `Enhancer#setCallbackFilter(CallbackFilter)` method where it expects methods of the enhanced class to be mapped to array indices of an array of Callback instances. When a method is invoked on the created proxy, the Enhancer will then choose the according interceptor and dispatch the called method on the corresponding Callback (which is a marker interface for all the interceptors that were introduced so far). To make this API less awkward, cglib offers a CallbackHelper which will represent a CallbackFilter and which can create an array of Callbacks for you. The enhanced object above will be functionally equivalent to the one in the example for the MethodInterceptor but it allows you to write specialized interceptors whilst keeping the dispatching logic to these interceptors separate.


