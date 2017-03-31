package com.xxx.demo;

import java.lang.reflect.Constructor;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Constructors {
    public static void main(String[] args) {
        Class<Constructors> clazz = Constructors.class;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            System.out.println("c.getName() = " + c.getName());
        }
    }
}
