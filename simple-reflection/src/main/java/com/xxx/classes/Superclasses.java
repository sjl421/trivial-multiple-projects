package com.xxx.classes;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Superclasses {
    private static class SubClass extends Superclasses {

    }

    public static void main(String[] args) {
        Class<SubClass> clazz = SubClass.class;
        System.out.println("clazz = " + clazz.getName());
        System.out.println("clazz.getSuperclass().getName() = " + clazz.getSuperclass().getName());
        System.out.println("clazz.getSuperclass().getSuperclass().getName() = " + clazz.getSuperclass().getSuperclass().getName());
    }
}
