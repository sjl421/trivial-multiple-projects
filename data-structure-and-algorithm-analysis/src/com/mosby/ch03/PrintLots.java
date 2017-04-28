package com.mosby.ch03;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PrintLots{
	public static void printLots(List<Integer> L, List<Integer> P){
		Iterator<Integer> iterL = L.iterator();
		Iterator<Integer> iterP = P.iterator();

		int start = -1;
		Integer itemL;
		
		while(iterP.hasNext()){
			Integer index = iterP.next();
			System.out.println("Looking for " + index + "th element in L");
			while(iterL.hasNext()){
				start++;
				itemL = iterL.next();
				if(start == index){
					System.out.println("The " + index + "th element in L is :" + itemL);
					break;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> L = new ArrayList<Integer>();
		L.add(0);
		L.add(1);
		L.add(2);
		L.add(3);
		L.add(4);
		L.add(5);
		L.add(6);
		L.add(7);
		L.add(8);
		L.add(9);
		L.add(10);
		ArrayList<Integer> P = new ArrayList<Integer>();
		P.add(0);
		P.add(5);
		P.add(6);
		P.add(10);
		printLots(L, P);
	}
}
