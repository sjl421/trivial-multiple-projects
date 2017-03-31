package com.xxx.demo;

import java.lang.reflect.Array;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class CreatingArrays {
    public static void main(String[] args) {
        int[] array = (int[]) Array.newInstance(int.class, 3);
    }
}
