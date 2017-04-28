package com.mosby.ch02;

public class Power {
	public static long pow(long x, int n){
		if(n == 0){
			return 1;
		}else if(n == 1){
			return x;
		}else if(n % 2 == 0){
			return pow(x * x, n /2);
		}else{
			return x * pow(x * x, (n - 1) / 2);
		}
	}
}
