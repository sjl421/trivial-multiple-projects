package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/20.
 *
 * 归并排序：基本操作是合并两个已经排好序的表，所以我们可以使用递归的方式去实现归并。
 * 退出条件：当 left >= right 时退出。
 */
public class MyMergeSort {

    private static <T extends Comparable<? super T>> void mergeSort(T[] array, T[] tmpArray, int left, int right) {
        if (left < right) {
            int center = (left + right) / 2;
            mergeSort(array, tmpArray, left, center);
            mergeSort(array, tmpArray, center + 1, right);
            merge(array, tmpArray, left, center + 1, right);
        }
    }

    private static <T extends Comparable<? super T>> void merge(T[] array, T[] tmpArray, int leftPos, int rightPos, int rightEnd) {
        int leftEnd = rightPos - 1;
        int tmpPos = leftPos;
        int numElements = rightEnd - leftPos + 1;

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

        // 将合并好的数组复制回原数组
        for (int i = 0; i < numElements; i++, rightEnd--) {
            array[rightEnd] = tmpArray[rightEnd];
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> void sort(T[] array) {
        T[] tmpArray = (T[]) new Comparable[array.length];
        mergeSort(array, tmpArray, 0, array.length - 1);
    }
}
