package com.xxx.demo;

import java.lang.reflect.Array;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class AccessingArrays {
    public static void main(String[] args) {
        int[] intArray = (int[]) Array.newInstance(int.class, 3);
        Array.set(intArray, 0, 123);
        Array.set(intArray, 1, 465);
        Array.set(intArray, 2, 789);

        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 0));
        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 1));
        System.out.println("Array.get(intArray, 0) = " + Array.get(intArray, 2));
        // Array.get(intArray, 0) = 123
        // Array.get(intArray, 0) = 465
        // Array.get(intArray, 0) = 789
    }
}
