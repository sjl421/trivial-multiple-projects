package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/20.
 *
 */
public class MyQuickSort {

    public static <T extends Comparable<? super T>> void quickSort(T[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private static <T extends Comparable<? super T>> void quickSort(T[] array, int left, int right) {
        if (left + CUTOFF <= right) {
            T pivot = median3(array, left, right);

            int i = left, j = right - 1;
            for (;;) {
                // 这里不需要做额外的判断，因为 median3 的副作用
                // array[left] 和 array[right] 必然是大于 pivot 的.
                while (array[++i].compareTo(pivot) < 0) {

                }
                while (array[--j].compareTo(pivot) > 0) {

                }
                if (i < j) {
                    swapReference(array, i, j);
                } else {
                    break;
                }
            }
            swapReference(array, i, right - 1); //Restore pivot

            quickSort(array, left, i - 1);  // Sort small elements
            quickSort(array, i + 1, right); // Sort large elements
        } else {
            // Do an insertion sort on the sub array
            insertionSort(array, left, right);
        }
    }

    private static <T extends Comparable<? super T>> void insertionSort(T[] array, int left, int right) {
        if (left >= right) {
            return;
        }
        int j;
        for (int i = left + 1; i <= right; i++) {
            T tmp = array[i];
            for (j = i; j > left && tmp.compareTo(array[j-1]) < 0; j--) {
                array[j] = array[j - 1];
            }
            array[j] = tmp;
        }
    }

    /**
     * 这个程序会将当前子数组中的 left,mid,right 三个位置的值进行排序，
     * 并将最小值放在 left 位置，最大值放在 right 位置，并将中值放到 right - 1 的位置。
     */
    private static <T extends Comparable<? super T>> T median3(T[] array, int left, int right) {
        int center = (left + right) / 2;
        if (array[center].compareTo(array[left]) < 0) {
            swapReference(array, left, center);
        }
        if (array[right].compareTo(array[left]) < 0) {
            swapReference(array, left, right);
        }
        if (array[right].compareTo(array[center]) < 0) {
            swapReference(array, center, right);
        }

        // 将枢纽元放置在 right-1 的位置上
        swapReference(array, center, right - 1);
        return array[right - 1];
    }

    private static <T extends Comparable<? super T>> void swapReference(T[]array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    private static final int CUTOFF = 10;
}
