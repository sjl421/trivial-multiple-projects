package com.mosby.ch03;

import java.util.Stack;

/**
 *
 */
public class InfixToPostfix {
    private static final char[] OPERATOR = {'+', '-', '*', '/', '(', ')'};
    private static boolean isOperator(char ch){
        for(int i = 0; i < OPERATOR.length; i++){
            if(ch == OPERATOR[i]){
                return true;
            }
        }
        return false;
    }
    private static int getPriority(char ch){
        if(ch == '('){
            return Integer.MAX_VALUE;
        }else if(ch == ')'){
        	return -1;
        }else if(ch == '*' || ch == '/'){
            return 10;
        }else if(ch == '+' || ch == '-'){
            return 1;
        }
        throw new RuntimeException("Unsupported Operator");
    }
    private static int comparePriority(char o1, char o2){
        return getPriority(o1) - getPriority(o2);
    }
    
    public static String infixToPostfix(String infix){
        StringBuffer postfix = new StringBuffer();
        Stack<Character> operatorRepertory = new Stack<Character>();
        char[] infixChar = infix.toCharArray();
        char ch;
        for(int i = 0; i < infixChar.length; i++){
            ch = infixChar[i];
            if(!isOperator(ch)){
            	if(ch != ' '){
            		postfix.append(ch);
            	}
                continue;
            }
            postfix.append(' ');
            if (!operatorRepertory.isEmpty()) {
				while (!operatorRepertory.isEmpty() && operatorRepertory.peek() != '(' && comparePriority(ch, operatorRepertory.peek()) <= 0) {
					if (operatorRepertory.peek() != '(' && operatorRepertory.peek() != ')') {
						postfix.append(operatorRepertory.pop());
					} else {
						operatorRepertory.pop();
					}
				}
				if(ch == ')'){
					operatorRepertory.pop();
				}else{
					operatorRepertory.push(ch);
				}
            } else {
                operatorRepertory.push(ch);
            }
        }
        while(!operatorRepertory.isEmpty()){
        	char operator = operatorRepertory.pop();
        	if(operator != '(' && operator != ')'){
        		postfix.append(operator);
        	}
        }
        return postfix.toString();
    }
    
    public static void main(String[] args) {
    	System.out.println(infixToPostfix("10 / (10 + 20 + 10) * 20 + 20 /10 * 10 + 10"));
    	System.out.println("10  10 20 +10 + /20 *20 10 /10 *+10+");
        System.out.println(ReversePolish.solveReversePolish(infixToPostfix("10 / (10 + 20 + 10) * 20 + 20 /10 * 10 + 10")));
    }
}





































