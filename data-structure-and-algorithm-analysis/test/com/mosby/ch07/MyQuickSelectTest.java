package com.mosby.ch07;

import org.junit.Test;

import java.util.Random;

/**
 * Created by dhy on 2016/10/20.
 * Powered by dhy
 */
public class MyQuickSelectTest {
    @Test
    public void testQuickSelect() {
        Integer[] array = randomArray(100);
        MyQuickSelect.quickSelect(array, 10);
        System.out.println(array[10]);
        MyQuickSort.quickSort(array);
        System.out.println(array[10]);
    }

    private Integer[] randomArray(int size) {
        Integer[] arrays = new Integer[size];
        for (int i = 0; i < size; i++) {
            arrays[i] = random.nextInt();
        }
        return arrays;
    }

    private Random random = new Random();
}
