package com.mosby.ch03;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class AlgebraSetOperation <T extends Comparable<? super T>>{
	public List<T> union(List<T> list1, List<T> list2){
		List<T> union = new ArrayList<T>();

		Iterator<T> iter1 = list1.iterator();
		Iterator<T> iter2 = list2.iterator();
		T t1 = null, t2 = null;
		boolean flag = true;
		
		while(iter1.hasNext()){
			t1 = iter1.next();
			union.add(t1);
			if(t2!=null && t1.compareTo(t2)<0){
				continue;
			}else if(t2!=null && t1.compareTo(t2)==0){
				flag = false;
			}else if(t2!=null && t1.compareTo(t2) > 0 && flag){
				union.add(t2);
			}
			while(iter2.hasNext()){
				t2 = iter2.next();
				if(t2.compareTo(t1) < 0){
					union.add(t2);
				}else if(t2.compareTo(t1) > 0){
					flag = true;
					break;
				}
			}
		}
		while(iter2.hasNext()){
			t2 = iter2.next();
			union.add(t2);
		}
		
		return union;
	}
	public List<T> intersection(List<T> list1, List<T> list2){
		List<T> intersection = new ArrayList<T>();
		
		Iterator<T> iter1 = list1.iterator();
		Iterator<T> iter2 = list2.iterator();
		
		T tmp = null;
		while(iter1.hasNext()){
			T t1 = iter1.next();
			if(tmp!=null && t1.compareTo(tmp) == 0){
				intersection.add(t1);
			}else if(tmp!=null && t1.compareTo(tmp) < 0){
				continue;
			}
			while(iter2.hasNext()){
				T t2 = iter2.next();
				if(t2.compareTo(t1) == 0){
					intersection.add(t2);
				}else if(t2.compareTo(t1) > 0){
					tmp = t2;
					break;
				}
			}
		}
		
		return intersection;
	}
	
	public static void main(String[] args) {
		AlgebraSetOperation<Integer> instance = new AlgebraSetOperation<Integer>();
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(3);
		list1.add(5);
		list1.add(7);
		list1.add(9);
		list1.add(11);
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(2);
		list2.add(3);
		list2.add(4);
		list2.add(5);
		list2.add(9);
		list2.add(10);
		list2.add(11);
		list2.add(13);
		list2.add(14);
		
		System.out.println(instance.intersection(list1, list2));
		System.out.println(list1);
		System.out.println(list2);
		System.out.println(instance.union(list1, list2));
	}
}

















