package com.xxx.classes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Modifiers {
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
