package com.mosby.ch03;

import java.util.Stack;

public class CheckSpecialChar {
	private static final char[] OPEN_PUNCTUATION = {'(', '[', '{'};
	private static final char[] CLOSE_PUNCTUATION = {')', ']', '}'};
	private boolean isOpenPunctuation(char ch){
		for(int i = 0; i < OPEN_PUNCTUATION.length; i++){
			if(OPEN_PUNCTUATION[i] == ch){
				return true;
			}
		}
		return false;
	}
	private boolean isClosePunctuation(char ch){
		for(int i = 0; i < CLOSE_PUNCTUATION.length; i++){
			if(CLOSE_PUNCTUATION[i] == ch){
				return true;
			}
		}
		return false;
	}
	private boolean isSymmetricPunctuation(char p1, char p2){
		if(p1 > p2){
			p1 ^= p2;
			p2 ^= p1;
			p1 ^= p2;
		}
		if((p1 == '[' && p2 == ']') || (p1 == '(') && p2 == ')' || (p1 == '{' && p2 == '}')){
			return true;
		}
		return false;
	}
	
	public boolean checkSpecialChar(String str){
		Stack<Character> stack = new Stack<Character>();
		char[] chars = str.toCharArray();
		char ch;
		for(int i = 0; i < chars.length; i++){
			ch = chars[i];
			if(isOpenPunctuation(ch)){
				stack.push(ch);
			}else if(isClosePunctuation(ch)){
				Character pop = stack.pop();
				if(!isSymmetricPunctuation(pop, ch)){
					return false;
				}
			}
		}
		return true;
	}
	public static void main(String[] args) {
		CheckSpecialChar instance = new CheckSpecialChar();
		System.out.println(instance.checkSpecialChar("{[()]}"));//true
		System.out.println(instance.checkSpecialChar("{[)(]}"));//false
		System.out.println(instance.checkSpecialChar("{}[]()"));//true
		System.out.println(instance.checkSpecialChar("{([)]}"));//false
	}
}
























