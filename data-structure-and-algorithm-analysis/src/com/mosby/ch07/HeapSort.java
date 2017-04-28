package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/18.
 * 堆排序
 */
public class HeapSort {
    private static int leftChild(int i) {
        return 2 * i + 1;
    }

    /**
     * Internal method for heapsort that is used in deleteMax and buildHeap
     * @param a an array of Comparable items
     * @param i the position from which to percolate down.
     * @param n the logical size of the binary heap.
     * @param <T> a Comparable items.
     */
    private static <T extends Comparable<? super T>> void perDown(T[] a, int i, int n) {
        int child;
        T tmp;

        for (tmp = a[i]; leftChild(i) < n; i = child) {
            child = leftChild(i);
            if (child != n - 1 && a[child].compareTo(a[child+1]) < 0) {
                child++;
            }
            if (tmp.compareTo(a[child]) < 0) {
                a[i] = a[child];
            } else {
                break;
            }
        }
        a[i] = tmp;
    }

    private static  <T extends Comparable<? super T>> void swapReferences(T[] a, int i, int n) {
        T tmp = a[i];
        a[i] = a[n];
        a[n] = tmp;
    }

    public static <T extends Comparable<? super T>> void heapSort(T[] a) {
        for (int i = a.length / 2; i >= 0; i--) {
            perDown(a, i, a.length);
        }
        for (int i = a.length - 1; i > 0; i--) {
            swapReferences(a, 0, i);
            perDown(a, 0, i);
        }
    }
}
