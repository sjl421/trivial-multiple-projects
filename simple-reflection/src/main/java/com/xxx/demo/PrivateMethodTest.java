package com.xxx.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by dhy on 17-3-31.
 *
 */
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
