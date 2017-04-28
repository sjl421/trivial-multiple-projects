package com.mosby.ch02;

public class BinarySearch {
	public static <T extends Comparable<? super T>> int binarySearch(T[] a, T x){
		int low = 0, high = a.length - 1;
		while(low <= high){
			int center = (low + high) / 2;
			if(x.compareTo(a[center]) > 0){
				low = center + 1;
			}else if(x.compareTo(a[center]) < 0){
				high = center - 1;
			}else{
				return center;
			}
		}
		return -1;
	}
}
