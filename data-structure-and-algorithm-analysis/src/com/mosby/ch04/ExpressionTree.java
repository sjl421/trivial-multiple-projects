package com.mosby.ch04;

import java.util.Stack;

import com.mosby.common.structure.BinaryNode;

/**
 * 将一个字符串表达式转换为表达式树
 */
public class ExpressionTree {
    /**
     * 字符串位置的索引
     */
    private int index = 0;
    
    private char[] operator = {
            '+', '-', '*', '/'
    };
    
    private boolean isOperator(char ch){
        for (char anOperator : operator) {
            if (ch == anOperator) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isOperator(String s) {
        return !(s == null || s.length() == 0 || s.length() > 1) && isOperator(s.toCharArray()[0]);
    }
    
    private boolean isOperatorNode(BinaryNode<String> node){
        return isOperator(node.element);
    }
    
    /**
     * 从 index 位置开始，获取表达式字符串中的下一个节点，这个节点可能是一个数字，也可能是一个操作符<br>
     * 
     * @param expression 表达式
     * @return 获得下一个表达式树上的节点的字符串
     */
    private String getNextNodeString(String expression){
        if(expression == null || expression.length() <= index){
            throw new IllegalArgumentException("index 超出表达式范围");
        }
        String node = "";
        char[] expChars = expression.toCharArray();
        //如果第一个字符是操作符那么，返回该操作符
        if(isOperator(expChars[index])){
            node = String.valueOf(expChars[index]);
            index++;
        }else{
            //如果第一个字符不是操作符，那么返回不包含空格的数字
            for(int i = index; i < expChars.length; i++){
                if(!isOperator(expChars[i]) && expChars[i] != ' '){
                    index++;
                    node += expChars[i];
                }else if(expChars[i] == ' '){
                    //在碰到 ' ' 时，有两种情况， node 为空，那么需要向下读取，如果 node 不为空，说明碰到了结束分割符
                    index++;
                    if(node.length() != 0){
                        break;
                    }
                }else{
                    return node;
                }
            }
        }
        
        
        return node;
    }
    
    private BinaryNode<String> getNextNode(String postfixExpression){
        String nodeString = getNextNodeString(postfixExpression);
        
        return new BinaryNode<>(nodeString);
    }
    
    /**
     * 接受一个后缀表达式并将后缀表达式转换为一个表达式树。<br><br>
     * 对于中缀表达式，我们可以将中缀表达式转换为后缀表达式后调用该方法。
     * 
     * @param postfixExpression 后缀表达式
     * @return 由后缀表达式转换为的表达式树
     */
    private BinaryNode<String> createTreeByPostfix(String postfixExpression){
        Stack<BinaryNode<String>> nodeStack = new Stack<BinaryNode<String>>();
        
        while(index < postfixExpression.length()){
            BinaryNode<String> node = getNextNode(postfixExpression);
            if(!isOperatorNode(node)){
                nodeStack.push(node);
            }else if(isOperatorNode(node) && !nodeStack.isEmpty()){
                node.right = nodeStack.pop();
                node.left = nodeStack.pop();
                nodeStack.push(node);
            }
        }
        
        return nodeStack.pop();
    }
    
    public static void main(String[] args) {
        ExpressionTree instance = new ExpressionTree();
        BinaryNode<String> root = instance.createTreeByPostfix("1 2 + 3 *");
        BinaryNode.preorderTraversal(root);
        /**
         * *
         *     +
         *          1
         *          2
         *     3
         */
    }
}
