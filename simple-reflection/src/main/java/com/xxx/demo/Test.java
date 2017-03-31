package com.xxx.demo;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class Test {
    private static class MyObj {
        public List<String> test;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        try {
            Field field = MyObj.class.getField("test");
            Class<?> type = field.getType();
            AnnotatedType annotatedType = field.getAnnotatedType();
            Type genericType = field.getGenericType();
            System.out.println("type = " + type);
            System.out.println("annotatedType = " + annotatedType);
            System.out.println("genericType = " + genericType);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
