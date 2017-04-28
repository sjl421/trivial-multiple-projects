package com.mosby.common.structure;

public class BinaryNode <T> {
	public T element;
	public BinaryNode<T> left;
	public BinaryNode<T> right;
	public BinaryNode(T element, BinaryNode<T> left, BinaryNode<T> right){
		this.element = element;
		this.left = left;
		this.right = right;
	}
	public BinaryNode(T element){
		this(element, null, null);
	}
	@Override
	public String toString(){
	    return this.element.toString();
	}
	@SuppressWarnings("unchecked")
    @Override
	public boolean equals(Object obj){
	    if(!(obj instanceof BinaryNode)){
	        return false;
	    }else if(obj != null){
	        BinaryNode<T> node =(BinaryNode<T>) obj;
	        if(node.element.equals(this.element)){
	            if(node.left == null && node.right == null){
	                return this.left == null && this.right == null;
	            }else if(node.left == null){
	                return node.right.equals(this.right);
	            }else if(node.right == null){
	                return node.left.equals(this.left);
	            }else{
	                return node.left.equals(this.left) && node.right.equals(this.right);
	            }
	        }
	    }
	    return false;
	}
	
	/**
	 * 先序遍历输出二叉树
	 * 
	 * @param node
	 * @param depth
	 */
	public static <T> void preorderTraversal(BinaryNode<T> node){
	    preorderTraversal(node, 0);
	}
	private static <T> void preorderTraversal(BinaryNode<T> node, int depth){
	    if(node == null){
	        return;
	    }
	    for(int i = 0; i < depth; i++){
	        System.out.print("    ");
	    }
	    System.out.println(node);
	    preorderTraversal(node.left, depth + 1);
	    preorderTraversal(node.right, depth + 1);
	}
	
	public static void main(String[] args) {
        BinaryNode<String> root = new BinaryNode<String>("0");
        BinaryNode<String> left = new BinaryNode<String>("1");
        BinaryNode<String> right = new BinaryNode<String>("2");
        BinaryNode<String> left2 = new BinaryNode<String>("3");
        BinaryNode<String> right2 = new BinaryNode<String>("5");
        left.left = left2;
        left.right = right2;
        root.left = left;
        root.right = right;
        preorderTraversal(root);
    }
}
