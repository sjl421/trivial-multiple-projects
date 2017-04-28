package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/20.
 *
 */
public class MyQuickSelect {

    public static <T extends Comparable<? super T>> T quickSelect(T[] array, int k) {
        quickSelect(array, 0, array.length - 1, k);
        return array[k - 1];
    }

    private static <T extends Comparable<? super T>> void quickSelect(T[] array,int left, int right, int k) {
        if (left + CUTOFF <= right) {
            T pivot = median3(array, left, right);
            int i = left, j = right - 1;
            for (;;) {
                while(array[++i].compareTo(pivot) < 0) {

                }
                while (array[--j].compareTo(pivot) > 0) {

                }
                if (i < j) {
                    swapReference(array, i, j);
                } else {
                    break;
                }
            }
            swapReference(array, i, right - 1);
            if (k <= i) {
                quickSelect(array, left, i - 1, k);
            } else if (k > i + 1) {
                quickSelect(array, i+1, right, k);
            }
        } else {
            insertionSort(array, left, right);
        }
    }

    private static <T extends Comparable<? super T>> void insertionSort(T[] array, int left, int right) {
        int j;
        for (int i = left + 1; i <= right; i++) {
            T tmp = array[i];
            for (j = i; j > left && tmp.compareTo(array[j-1]) < 0; j--) {
                array[j] = array[j - 1];
            }
            array[j] = tmp;
        }
    }

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

        swapReference(array, center, right - 1);
        return array[right - 1];
    }

    private static <T extends Comparable<? super T>> void swapReference(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    private final static int CUTOFF = 10;
}
