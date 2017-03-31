package com.xxx.demo;

import java.lang.reflect.Field;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Fields {
    public static class SubClass {
        private String privateSupField;
        String friendlySupField;
        protected String protectedSupField;
        public String publicSupField;
    }

    private static class SubSubClass extends SubClass {
        private String privateField;
        String friendlyField;
        protected String protectedField;
        public String publicField;
    }

    public static void main(String[] args) {
        Class<SubSubClass> clazz = SubSubClass.class;
        for (Field field : clazz.getFields()) {
            System.out.println("field.getName() = " + field.getName());
        }

        System.out.println("---------------------------");

        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("field.getName() = " + field.getName());
        }
    }
}
