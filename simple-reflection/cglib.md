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

## How does it work?

When the `Enhancer` creates a class, it will set create a private field for each interceptor that was registered as a `Callback` for the enhanced class after its creation. This also means that class definitions that were created with cglib cannot be reused after their creation since the registration of callbacks does not become a part of the generated class's initialization phase but are prepared manually by cglib after the class was already initialized by the JVM. This also means that classes created with cglib are not technically ready after their initialization and for example cannot be sent over the wire since the callbacks would not exist for the class loaded in the target machine.

Depending on the registered interceptors, cglib might register additional fields such as for example for the `MethodInterceptor` where two private static fields (one holding a reflective Method and a the other holding MethodProxy) are registered per method that is intercepted in the enhanced class or any of its subclasses. Be aware that the `MethodProxy` is making excessive use of the FastClass which triggers the creation of additional classes and is described in further detail below.

```java
@Test
public void testFixedValue() throws Exception {
  Enhancer enhancer = new Enhancer();
  enhancer.setSuperclass(SampleClass.class);
  enhancer.setCallback(new FixedValue() {
    @Override
    public Object loadObject() throws Exception {
      return "Hello cglib!";
    }
  });
  SampleClass proxy = (SampleClass) enhancer.create();
  assertEquals("Hello cglib!", proxy.test(null));
}
```

**The anonymous subclass of FixedValue would become hardly referenced from the enhanced SampleClass such that neither the anonymous FixedValue instance or the class holding the @Test method would ever be garbage collected**. This can introduce nasty memory leaks in your applications. `Therefore, do not use non-static inner classes with cglib`. (I only use them in this overview for keeping the examples short.)

Finally, you should never intercept `Object#finalize()`. Due to the subclassing approach of cglib, intercepting finalize is implemented by overriding it what is in general a bad idea. **Enhanced instances that intercept finalize will be treated differently by the garbage collector and will also cause these objects being queued in the JVM's finalization queue**. Also, if you (accidentally) create a hard reference to the enhanced class in your intercepted call to finalize, you have effectively created an noncollectable instance. This is in general nothing you want. Note that final methods are never intercepted by cglib. Thus, `Object#wait`, `Object#notify` and `Object#notifyAll` do not impose the same problems. Be however aware that `Object#clone` can be intercepted what is something you might not want to do.

## Immutable bean

cglib's `ImmutableBean` allows you to create an immutability wrapper similar to for example `Collections#immutableSet`. All changes of the underlying bean will be prevented by an IllegalStateException (however, not by an UnsupportedOperationException as recommended by the Java API). Looking at some bean

```java
public class SampleBean {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
```

we can make this bean immutable:

```java
public class ImmutableBeanExample {
    @Test
    public void testImmutableBean() {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean);
        Assert.assertEquals("Hello world!", immutableBean.getValue());
        bean.setValue("Hello world, again!");
        Assert.assertEquals("Hello world, again!", immutableBean.getValue());
        immutableBean.setValue("Hello cglib!");
		// Causes exception.
    }
}
```

As obvious from the example, the immutable bean prevents all state changes by throwing an IllegalStateException. However, the state of the bean can be changed by changing the original object. All such changes will be reflected by the ImmutableBean.

## Bean generator

The BeanGenerator is another bean utility of cglib. It will create a bean for you at run time:

```java
public class BeanGeneratorExample {
    @Test
    public void testBeanGenerator() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();

        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib!");
        Method getter = myBean.getClass().getMethod("getValue");
        Assert.assertEquals("Hello cglib!", getter.invoke(myBean));
    }
}
```

As obvious from the example, the `BeanGenerator` first takes some properties as name value pairs. On creation, the `BeanGenerator` creates the accessors

```
<type> get<name>()
void set<name>(<type>)
```

for you. This might be useful when another library expects beans which it resolved by reflection but you do not know these beans at run time. (An example would be Apache Wicket which works a lot with beans.)

## Bean copier

The `BeanCopier` is another bean utility that copies beans by their property values. Consider another bean with similar properties as SampleBean:

```java
public class BeanCopierExample {
    @Test
    public void testBeanCopier() {
        BeanCopier copier = BeanCopier.create(SampleBean.class, AnotherSampleBean.class, false);
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        AnotherSampleBean anotherSampleBean = new AnotherSampleBean();
        copier.copy(bean, anotherSampleBean, null);
        Assert.assertEquals("Hello cglib!", anotherSampleBean.getValue());
    }
}
```

## Bulk bean

A `BulkBean` allows to use a specified set of a bean's accessors by arrays instead of method calls:

```java
public class BulkBeanExample {
    @Test
    public void testBulkBean() {
        BulkBean bulkBean = BulkBean.create(SampleBean.class,
                new String[]{"getValue"},
                new String[]{"setValue"},
                new Class[]{String.class});

        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        Assert.assertEquals(1, bulkBean.getPropertyValues(bean).length);
        Assert.assertEquals("Hello world!", bulkBean.getPropertyValues(bean)[0]);
        bulkBean.setPropertyValues(bean, new Object[]{"Hello cglib!"});
        Assert.assertEquals("Hello cglib!", bean.getValue());
    }
}
```

The BulkBean **takes an array of getter names**, **an array of setter names and an array of property types as its constructor arguments**. The resulting instrumented class can then extracted as an array by `BulkBean#getPropertyValues(Object)`. Similarly, a bean's properties can be set by `BulkBean#setPropertyValues(Object, Object[])`.

## Bean map

This is the last bean utility within the cglib library. The BeanMap converts all properties of a bean to a **String-to-Object** Java `Map`:

```java
public class BeanMapExample { @Test
    public void testBeanGenerator() {
        SampleBean bean = new SampleBean();
        BeanMap map = BeanMap.create(bean);
        bean.setValue("Hello cglib");
        Assert.assertEquals("Hello cglib", map.get("value"));
    }
}
```

Additionally, the `BeanMap#newInstance(Object)` method allows to create maps for other beans by reusing the same Class.

## Key factory

The `KeyFactory` factory allows the dynamic creation of keys that are composed of multiple values that can be used in for example `Map` implementations. For doing so, the KeyFactory requires some interface that defines the values that should be used in such a key. This interface must contain a single method by the name newInstance that returns an Object. For example:

```java
public interface SampleKeyFactory {
	Object newInstance(String first, int second);
}
```

```java
public class KeyFactoryExample {
    @Test
    public void testKeyFactory() {
        SampleKeyFactory keyFactory = (SampleKeyFactory) KeyFactory.create(SampleKeyFactory.class);
        Object key0 = keyFactory.newInstance("foo", 42);
        Object key1 = keyFactory.newInstance("foo", 41);
        Object key2 = keyFactory.newInstance("foo", 42);

        Assert.assertEquals(false, key0.equals(key1));
        Assert.assertEquals(true, key0.equals(key2));
    }
}
```

The `KeyFactory` will assure the correct implementation of the `Object#equals(Object)` and `Object#hashCode` methods such that the resulting key objects can be used in a Map or a Set. The KeyFactory is also used quite a lot internally in the cglib library.

## Mixin

Some might already know the concept of the `Mixin` class from other programing languages such as Ruby or Scala (where mixins are called traits). cglib `Mixins` allow the **combination of several objects into a single object**. However, in order to do so, those objects must be backed by interfaces:

```java
public interface IHello {
    String hello();
}
```

```java
public interface IGoodbye {
    String goodbye();
}
```

```java
public interface IMixin extends IHello, IGoodbye {
}
```

```java
public class Hello implements IHello {
    @Override
    public String hello() {
        return "hello";
    }
}
```

```java
public class Goodbye implements IGoodbye {
    @Override
    public String goodbye() {
        return "goodbye";
    }
}
```

```java
public class MixinExample {
    @Test
    public void testMixin() {
        Mixin mixin = Mixin.create(new Class[]{IHello.class, IGoodbye.class, IMixin.class},
                new Object[]{new Hello(), new Goodbye()});

        IMixin mixinDelegate = (IMixin) mixin;
        Assert.assertEquals("hello", mixinDelegate.hello());
        Assert.assertEquals("goodbye", mixinDelegate.goodbye());
    }
}
```

## String switcher

The `StringSwitcher` emulates a String to int Java mapping:

```java
public class StringSwitcherExample {
    @Test
    public void testStringSwitcher() {
        String[] strings = {"one", "two"};
        int[] values = {10, 20};
        StringSwitcher stringSwitcher = StringSwitcher.create(strings, values, true);
        Assert.assertEquals(10, stringSwitcher.intValue("one"));
        Assert.assertEquals(20, stringSwitcher.intValue("two"));
        Assert.assertEquals(-1, stringSwitcher.intValue("three"));
    }
}
```

The StringSwitcher allows to emulate a switch command on Strings such as it is possible with the built-in Java switch statement since Java 7. If using the StringSwitcher in Java 6 or less really adds a benefit to your code remains however doubtful and I would personally not recommend its use.

## Interface maker

The `InterfaceMaker` does what its name suggests: It dynamically creates a new interface.

```java
public class InterfaceMakerExample {
    @Test
    public void testInterfaceMaker() {
        Signature signature = new Signature("foo", Type.DOUBLE_TYPE, new Type[]{Type.INT_TYPE});
        InterfaceMaker maker = new InterfaceMaker();
        maker.add(signature, new Type[0]);
        Class iface = maker.create();
        Assert.assertEquals(1, iface.getMethods().length);
        Assert.assertEquals("foo", iface.getMethods()[0].getName());
        Assert.assertEquals(double.class, iface.getMethods()[0].getReturnType());
    }
}
```

Other than any other class of cglib's public API, the interface maker relies on ASM types. The creation of an interface in a running application will hardly make sense since an interface only represents a type which can be used by a compiler to check types. It can however make sense when you are generating code that is to be used in later development.

## Method delegate

A `MethodDelegate` allows to emulate a C#-like delegate to a specific method by binding a method call to some interface. For example, the following code would bind the `SampleBean#getValue` method to a delegate:

```java
public interface BeanDelegate {
    String getValueFromDelegate();
}
```

```java
public class MethodDelegateExample {
    @Test
    public void testMethodDelegate() {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        BeanDelegate delegate = (BeanDelegate) MethodDelegate.create(bean, "getValue", BeanDelegate.class);
        Assert.assertEquals("Hello cglib!", delegate.getValueFromDelegate());
    }
}
```

There are however some things to note:

1. The factory method `MethodDelegate#create` takes exactly one method name as its second argument. This is the method the MethodDelegate will proxy for you.
2. There must be a method without arguments defined for the object which is given to the factory method as its first argument. Thus, the MethodDelegate is not as strong as it could be.
3. **The third argument must be an interface with exactly one argument**. The MethodDelegate implements this interface and can be cast to it. When the method is invoked, it will call the proxied method on the object that is the first argument.

Furthermore, consider these drawbacks:

1. cglib creates a new class for each proxy. Eventually, this will litter up your permanent generation heap space
2. You cannot proxy methods that take arguments.
3. If your interface takes arguments, the method delegation will simply not work without an exception thrown (the return value will always be null). If your interface requires another return type (even if that is more general), you will get a `IllegalArgumentException`.

### Multicast delegate

The MulticastDelegate works a little different than the MethodDelegate even though it aims at similar functionality. For using the MulticastDelegate, we require an object that implements an interface:

```java
public interface DelegatationProvider {
    void setValue(String value);
}
```

```java
public class SimpleMulticastBean implements DelegatationProvider {

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
```

Based on this interface-backed bean we can create a `MulticastDelegate` that dispatches all calls to setValue(String) to several classes that implement the DelegationProvider interface:

```java
public class MulticastDelegateExample {
    @Test
    public void testMulticastDelegate() {
        MulticastDelegate multicastDelegate = MulticastDelegate.create(DelegatationProvider.class);
        SimpleMulticastBean first = new SimpleMulticastBean();
        SimpleMulticastBean second = new SimpleMulticastBean();
        multicastDelegate = multicastDelegate.add(first);
        multicastDelegate = multicastDelegate.add(second);

        DelegatationProvider provider = (DelegatationProvider) multicastDelegate;
        provider.setValue("Hello world!");

        Assert.assertEquals("Hello world!", first.getValue());
        Assert.assertEquals("Hello world!", second.getValue());
    }
}
```

1. The objects need to implement a single-method interface. This sucks for third-party libraries and is awkward when you use CGlib to do some magic where this magic gets exposed to the normal code. Also, you could implement your own delegate easily (without byte code though but I doubt that you win so much over manual delegation).
2. When your delegates return a value, you will receive only that of the last delegate you added. All other return values are lost (but retrieved at some point by the multicast delegate).

### Constructor delegate

A ConstructorDelegate allows to create a byte-instrumented factory method. For that, that we first require an interface with a single method newInstance which returns an Object and takes any amount of parameters to be used for a constructor call of the specified class. For example, in order to create a ConstructorDelegate for the SampleBean, we require the following to call SampleBean's default (no-argument) constructor:

```java
public class ConstructorDelegateExample {
    @Test
    public void testConstructorDelegate() {
        SampleBeanConstructorDelegate constructorDelegate =
                (SampleBeanConstructorDelegate) ConstructorDelegate.create(SampleBean.class, SampleBeanConstructorDelegate.class);
        SampleBean bean = (SampleBean) constructorDelegate.newInstance();
        Assert.assertTrue(SampleBean.class.isAssignableFrom(bean.getClass()));
        Assert.assertTrue(SampleBean.class.equals(bean.getClass()));
    }
}
```

### Parallel sorter

The ParallelSorter claims to be a faster alternative to the Java standard library's array sorters when sorting arrays of arrays:

```java
public class ParallelSorterExample {
    @Test
    public void testParallelSorter() {
        Integer[][] value = {
                {4, 3, 9, 0},
                {2, 1, 6, 0}
        };

        ParallelSorter.create(value).mergeSort(0);

        for (Integer[] row : value) {
            int former = -1;
            for (int val : row) {
                Assert.assertTrue(former < val);
                former = val;
            }
        }
    }
}
```

The ParallelSorter takes an array of arrays and allows to either apply a merge sort or a quick sort on every row of the array. Be however careful when you use it:
1. When using arrays of primitives, you have to call merge sort with explicit sorting ranges (e.g. ParallelSorter.create(value).mergeSort(0, 0, 3) in the example. Otherwise, the ParallelSorter has a pretty obvious bug where it tries to cast the primitive array to an array Object[] what will cause a ClassCastException.
2. If the array rows are uneven, the first argument will determine the length of what row to consider. Uneven rows will either lead to the extra values not being considered for sorting or a ArrayIndexOutOfBoundException.

### Fast class and fast members

The FastClass promises a faster invocation of methods than the Java reflection API by wrapping a Java class and offering similar methods to the reflection API:

```java
public class FastClassExample {
    @Test
    public void testFastClass() throws NoSuchMethodException, InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleBean.class);
        FastMethod fastMethod = fastClass.getMethod(SampleBean.class.getMethod("getValue"));
        SampleBean myBean = new SampleBean();
        myBean.setValue("Hello cglib!");
        Assert.assertEquals("Hello cglib!", fastMethod.invoke(myBean, new Object[0]));
    }
}
```

Besides the demonstrated FastMethod, the FastClass can also create FastConstructors but no fast fields. But how can the FastClass be faster than normal reflection? Java reflection is executed by JNI where method invocations are executed by some C-code. The FastClass on the other side creates some byte code that calls the method directly from within the JVM. However, the newer versions of the HotSpot JVM (and probably many other modern JVMs) know a concept called inflation where the JVM will translate reflective method calls into native version's of FastClass when a reflective method is executed often enough. You can even control this behavior (at least on a HotSpot JVM) with setting the sun.reflect.inflationThreshold property to a lower value. (The default is 15.) This property determines after how many reflective invocations a JNI call should be substituted by a byte code instrumented version. **I would therefore recommend to not use FastClass on modern JVMs, it can however fine-tune performance on older Java virtual machines**.
