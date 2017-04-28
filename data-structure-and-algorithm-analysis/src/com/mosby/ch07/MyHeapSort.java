package com.mosby.ch07;

/**
 * Created by dhy on 2016/10/19.
 *
 * 堆排序利用堆的性质，首先将数组转换为一个max堆。
 * 随后每次执行 deleteMax 并减小堆的大小1，直到堆的大小为0，堆排序结束。
 *
 */
public class MyHeapSort {

    /**
     * 根据当前节点获取该节点的左子节点
     * @param i 当前节点的索引
     * @return 子节点的索引
     */
    private static int leftChild(int i) {
        return 2 * i + 1;
    }

    /**
     * 用于构建堆和下滤堆
     * @param array 待排序的数组
     * @param i 被下滤的对象的索引
     * @param n 堆的大小
     * @param <T> 一个可比较的对象
     */
    private static <T extends Comparable<? super T>> void perDown(T[] array, int i, int n) {
        int child;
        T tmp;

        for (tmp = array[i]; leftChild(i) < n; i = child) {
            child = leftChild(i);
            if (child!=n-1 && array[child].compareTo(array[child+1])<0) {
                child++;
            }
            if (tmp.compareTo(array[child]) < 0) {
                array[i] = array[child];
            } else {
                break;
            }
        }
        array[i] = tmp;
    }

    private static <T extends Comparable<? super T>> void swapReference(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static <T extends Comparable<? super T>> void heapSort(T[] array) {
        for (int i = array.length / 2; i >= 0; i--) {
            perDown(array, i, array.length);
        }
        for (int i = array.length - 1; i > 0; i--){
            swapReference(array, 0, i);
            perDown(array, 0, i);
        }
    }

}
