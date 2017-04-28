package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/19.
 * 归并排序
 */
public class MergeSort {

    /**
     * Internal method that makes recursive calls.
     * @param array an array of Comparable items.
     * @param tmpArray an array to place the merged result.
     * @param left the left-most index of the sub array.
     * @param right the right-most index of the sub array.
     * @param <T> Comparable item.
     */
    private static <T extends Comparable<? super T>> void mergeSort(T[] array, T[] tmpArray, int left, int right) {
        if (left < right) {
            int center = (left + right) / 2;
            mergeSort(array, tmpArray, left, center);
            mergeSort(array, tmpArray,  center + 1, right);
            merge(array, tmpArray, left, center + 1, right);
        }
    }

    private static <T extends Comparable<? super T>> void merge(T[] array, T[] tmpArray, int leftPos, int rightPos, int rightEnd) {
        int leftEnd = rightPos - 1;
        int tmpPos = leftPos;
        int numElements = rightEnd - leftPos + 1;

        // Main loop
        while (leftPos <= leftEnd && rightPos <= rightEnd) {
            if (array[leftPos].compareTo(array[rightPos]) <= 0) {
                tmpArray[tmpPos++] = array[leftPos++];
            } else {
                tmpArray[tmpPos++] = array[rightPos++];
            }
        }

        while (leftPos <= leftEnd) {
            tmpArray[tmpPos++] = array[leftPos++];
        }

        while (rightPos <= rightEnd) {
            tmpArray[tmpPos++] = array[rightPos++];
        }

        // Copy tmpArray back
        for (int i = 0; i < numElements; i++, rightEnd--) {
            array[rightEnd] = tmpArray[rightEnd];
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> void mergeSort(T[] array) {
        T[] tmpArray = (T[]) new Comparable[array.length];
        mergeSort(array, tmpArray, 0, array.length - 1);
    }
}
