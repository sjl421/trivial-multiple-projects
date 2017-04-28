package com.mosby.ch03;

import java.util.Stack;

/**
 * ����沨�����ʽ��ֵ�����Ǽٶ��沨�����ʽ����������ȫ�Ϸ��ġ�
 */
public class ReversePolish {
	private static final char[] OPERATOR = {'+', '-', '*', '/'};
	private static boolean isOperator(char ch){
		for(int i = 0; i < OPERATOR.length; i++){
			if(ch == OPERATOR[i]){
				return true;
			}
		}
		return false;
	}
	private static double operateDouble(double num1, double num2, char operator){
		switch(operator){
			case '+':
				return num1 + num2;
			case '-':
				return num1 - num2;
			case '*':
				return num1 * num2;
			case '/':
				return num1 / num2;
			default:
				throw new RuntimeException("No Such Operator");
		}
	}
	public static double solveReversePolish(String reversePolish){
		Stack<Double> numRepertory = new Stack<Double>();
		double result = 0.0;
		char[] chars = reversePolish.toCharArray();
		String numStr = "";
		for(int i = 0; i < chars.length; i++){
			//����һ������Ϊ ' ' �����ǲ�������ʱ�����ǵ�ǰ����������ϣ���������ѹ��ջ��
			if(chars[i] == ' ' || isOperator(chars[i])){
				if(numStr != null && numStr.length() != 0){
					numRepertory.push(Double.parseDouble(numStr));
					numStr = "";
				}
			}
			if(isOperator(chars[i])){
				double num2 = numRepertory.pop();
				double num1 = numRepertory.pop();
				result = operateDouble(num1, num2, chars[i]);
				numRepertory.push(result);
			}else if(chars[i] != ' '){
				numStr += chars[i];
			}
		}
		
		return numRepertory.pop();
	}
	
	public static void main(String[] args) {
		System.out.println(solveReversePolish("10  10 10 + /20*"));
	}
}
