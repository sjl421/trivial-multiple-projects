package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/19.
 *
 */
public class MyShellSort {
    public static <T extends Comparable<? super T>> void shellSort(T[] array) {
        T tmp;
        for (int gap = array.length / 2; gap > 0; gap /= 2) {
            int j;
            for (int i = gap; i < array.length; i++) {
                tmp = array[i];
                for (j = i; j >= gap; j -= gap) {
                    if (array[j].compareTo(array[j-gap]) < 0) {
                        array[j] = array[j - gap];
                    }
                }
                array[j] = tmp;
            }
        }
    }
}
