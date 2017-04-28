package com.mosby.ch07;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by dhy on 2016/10/20.
 *
 */
public class QuickSelectTest {

    @Test
    public void testQuickSelect() {
        Integer[] array = randomArray(100);
        Integer target = MyQuickSelect.quickSelect(array, 10);
        MyQuickSort.quickSort(array);
        Assert.assertEquals(target, array[9]);
    }

    private Integer[] randomArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    Random random = new Random();
}
