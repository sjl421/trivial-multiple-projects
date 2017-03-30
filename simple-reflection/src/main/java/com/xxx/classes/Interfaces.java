package com.xxx.classes;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Interfaces {
    private interface ExplicitIn {}
    private interface ImplicitIn {}
    private static class SubClass implements ExplicitIn, ImplicitIn {}
    private static class SubSubClass extends SubClass implements ExplicitIn  {}

    public static void main(String[] args) {
        Class<SubSubClass> clazz = SubSubClass.class;
        for (Class<?> c : clazz.getInterfaces()) {
            System.out.println("The interface of SubSubClass = " + c.getSimpleName());
        }
        System.out.println("--------");
        Class<SubClass> subClazz = SubClass.class;
        for (Class<?> c : subClazz.getInterfaces()) {
            System.out.println("The interface of SubClass = " + c.getSimpleName());
        }
    }
}
