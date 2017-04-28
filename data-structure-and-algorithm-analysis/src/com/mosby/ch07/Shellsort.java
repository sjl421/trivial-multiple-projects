package com.mosby.ch07;

/**
 * 希尔排序
 */
public class Shellsort {
    /**
     * @param a 待排序的数组
     * @param <T> 可以比较的泛型变量
     */
    public static <T extends Comparable<? super T>> void shellSort(T[] a){
        //使用希尔增量
        for(int gap = a.length / 2; gap > 0; gap /= 2){
            int j;
            for(int i = gap; i < a.length; i++){
                T tmp = a[i];
                for(j = i; j >= gap && tmp.compareTo(a[j - gap]) < 0; j -= gap){
                    a[j] = a[j - gap];
                }
                a[j] = tmp;
            }
        }
    }

    public static <T extends Comparable<? super T>>  void myShellSort(T[] a) {
        // 初始使用数组长度的二分之一作为增量
        for (int gap = a.length / 2; gap > 0; gap /= 2) {
            int j;
            for (int i = gap; i < a.length; i++) {
                T tmp = a[i];
                for (j = i; j >= gap && tmp.compareTo(a[j-gap]) > 0; j -= gap) {
                    a[j] = a[j-gap];
                }
                a[j] = tmp;
            }
        }
    }
}
