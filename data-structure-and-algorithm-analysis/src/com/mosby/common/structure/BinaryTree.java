package com.mosby.common.structure;

/**
 *¶þ²æÊ÷
 * @param <T>
 */
public class BinaryTree <T>{
	public T val;
	public BinaryTree<T> left;
	public BinaryTree<T> right;
	public BinaryTree(T val){
		this.val = val;
	}
	
	public String toString(){
		return this.val.toString();
	}
}
