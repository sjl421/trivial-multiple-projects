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
