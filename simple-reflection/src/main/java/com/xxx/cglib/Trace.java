package com.xxx.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

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
