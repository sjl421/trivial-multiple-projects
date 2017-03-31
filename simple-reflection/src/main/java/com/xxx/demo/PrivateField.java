package com.xxx.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class PrivateField {

    public static void main(String[] args) throws IllegalAccessException {
        PrivateObject privateObject = new PrivateObject();
        privateObject.setPrivateString("private string");
        Field[] fields = privateObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println("field = " + field);
            // field = private java.lang.String com.xxx.demo.PrivateField$PrivateObject.privateString
            // field = public java.lang.String com.xxx.demo.PrivateField$PrivateObject.publicString
        }

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                field.setAccessible(true);
            }
            System.out.println("field.get(privateObject) = " + field.get(privateObject));
            // field.get(privateObject) = private string
            // field.get(privateObject) = null
        }
    }

    private final static class PrivateObject {
        private String privateString;
        public String publicString;

        public String getPrivateString() {
            return privateString;
        }

        public void setPrivateString(String privateString) {
            this.privateString = privateString;
        }

        public String getPublicString() {
            return publicString;
        }

        public void setPublicString(String publicString) {
            this.publicString = publicString;
        }
    }
}
