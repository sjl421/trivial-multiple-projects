package com.xxx.demo;

import java.lang.reflect.Method;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Methods {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object obj = new Object();

        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            System.out.println("methodName = " + method.getName());
        }
    }
}
