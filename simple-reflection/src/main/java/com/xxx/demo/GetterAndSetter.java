package com.xxx.demo;

import java.lang.reflect.Method;

/**
 * Created by dhy on 17-3-31.
 *
 */
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
