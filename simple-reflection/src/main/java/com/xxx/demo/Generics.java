package com.xxx.demo;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class Generics {
    public static void main(String[] args) throws NoSuchMethodException {
        Method method = MyClass.class.getMethod("getStringList");
        Type returnType = method.getGenericReturnType();
        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();
        // sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedParameterizedTypeImpl@5ccd43c2
        System.out.println(annotatedReturnType);
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                // typeArgClass = class java.lang.String
            }
        }
    }

    private static class MyClass {
        private List<String> stringList = new ArrayList<>();

        public List<String> getStringList() {
            return stringList;
        }

        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }
    }
}
