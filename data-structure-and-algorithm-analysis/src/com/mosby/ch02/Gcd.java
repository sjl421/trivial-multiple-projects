package com.mosby.ch02;

/**
 * gcd(m, n) = gcd(n, m % n)
 */
public class Gcd {
	public static long gcd(long m, long n){
		while(n != 0){
			long rem = m % n;
			m = n;
			n = rem;
		}
		return m;
	}
	
	public static void main(String[] args) {
		System.out.println(gcd(25, 4));
		System.out.println(gcd(4, 25));
	}
}
