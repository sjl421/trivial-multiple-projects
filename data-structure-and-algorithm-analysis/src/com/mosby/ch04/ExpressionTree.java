package com.mosby.ch04;

import java.util.Stack;

import com.mosby.common.structure.BinaryNode;

/**
 * ��һ���ַ������ʽת��Ϊ���ʽ��
 */
public class ExpressionTree {
    /**
     * �ַ���λ�õ�����
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
     * �� index λ�ÿ�ʼ����ȡ���ʽ�ַ����е���һ���ڵ㣬����ڵ������һ�����֣�Ҳ������һ��������<br>
     * 
     * @param expression ���ʽ
     * @return �����һ�����ʽ���ϵĽڵ���ַ���
     */
    private String getNextNodeString(String expression){
        if(expression == null || expression.length() <= index){
            throw new IllegalArgumentException("index �������ʽ��Χ");
        }
        String node = "";
        char[] expChars = expression.toCharArray();
        //�����һ���ַ��ǲ�������ô�����ظò�����
        if(isOperator(expChars[index])){
            node = String.valueOf(expChars[index]);
            index++;
        }else{
            //�����һ���ַ����ǲ���������ô���ز������ո������
            for(int i = index; i < expChars.length; i++){
                if(!isOperator(expChars[i]) && expChars[i] != ' '){
                    index++;
                    node += expChars[i];
                }else if(expChars[i] == ' '){
                    //������ ' ' ʱ������������� node Ϊ�գ���ô��Ҫ���¶�ȡ����� node ��Ϊ�գ�˵�������˽����ָ��
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
     * ����һ����׺���ʽ������׺���ʽת��Ϊһ�����ʽ����<br><br>
     * ������׺���ʽ�����ǿ��Խ���׺���ʽת��Ϊ��׺���ʽ����ø÷�����
     * 
     * @param postfixExpression ��׺���ʽ
     * @return �ɺ�׺���ʽת��Ϊ�ı��ʽ��
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
