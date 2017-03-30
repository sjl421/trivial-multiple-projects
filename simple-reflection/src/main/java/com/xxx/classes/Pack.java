package com.xxx.classes;

import java.lang.annotation.Annotation;

/**
 * Created by dhy on 17-3-30.
 *
 */
public class Pack {
    public static void main(String[] args) {
        Class<Object> clazz = Object.class;
        Package pack = clazz.getPackage();
        System.out.println(pack.getName());
        for (Annotation annotation : pack.getAnnotations()) {
            System.out.println(annotation);
        }
        System.out.println("implementationTitle = [" + pack.getImplementationTitle() + "]");
        //
        System.out.println("implementationVendor = [" + pack.getImplementationVendor() + "]");
        System.out.println("implementationVersion = [" + pack.getImplementationVersion() + "]");
        // java.lang
        // implementationTitle = [Java Runtime Environment]
        // implementationVendor = [Oracle Corporation]
        // implementationVersion = [1.8.0_111]
    }
}
