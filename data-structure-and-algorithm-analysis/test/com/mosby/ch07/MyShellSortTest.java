package com.mosby.ch07;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by dhy on 2016/10/20.
 *
 */
public class MyShellSortTest {

    @Test
    public void testShellSort() {
        Integer[] array = randomArray(100);
        Assert.assertEquals(false, checkSortedArray(array));
        MyShellSort.shellSort(array);
        Assert.assertEquals(true, checkSortedArray(array));
    }

    private boolean checkSortedArray(Integer[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i+1]) {
                return false;
            }
        }
        return true;
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
