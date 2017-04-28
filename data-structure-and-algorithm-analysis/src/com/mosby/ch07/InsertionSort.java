package com.mosby.ch07;

/**
 * 插入排序：
 * 对于已经排好序的数组运行时间为 O(N).
 */
public class InsertionSort {
    public static <T extends Comparable<? super T>> void insertionSort(T[] array){
        int j;
        /*
         * 我们可以默认 a[0] 是一个已经排好序数组.
         */
        for (int i = 1; i < array.length; i++) {
            /*
             * 我们目前要做的就是将 a[i] 插入到已经排好序的数组中的正确位置
             */
            T tmp = array[i];
            for (j = i; j > 0 && tmp.compareTo(array[j - 1]) < 0; j--) {
                array[j] = array[j - 1];
            }
//            for (j = i; j > 0; j--) {
//                if (tmp.compareTo(array[j-1]) < 0) {
//                    array[j] = array[j - 1];
//                } else {
//                    break;;
//                }
//            }
            array[j] = tmp;
        }
    }
}
