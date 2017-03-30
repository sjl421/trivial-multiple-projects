package com.xxx.classes;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.Arrays;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Annotations {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    private  @interface SupAnnotation {}

    @SupAnnotation
    @Documented
    @Target({ElementType.TYPE, ANNOTATION_TYPE, CONSTRUCTOR})
    private  @interface SubAnnotation {
        String value() default "";
    }

    private class Sup implements Serializable, Runnable {

        @Override
        public void run() {
        }
    }
    private class Sub extends Sup {}

    public static void main(String[] args) {
        System.out.println(Arrays.asList(Sup.class.getAnnotatedInterfaces()));
        System.out.println(Arrays.asList(Sub.class.getAnnotatedInterfaces()));
    }
}
