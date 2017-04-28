package com.mosby.common.structure;

/**
 * �����������Ķ���
 */
public class BinarySearchTree<T extends Comparable<? super T>> {
	private static class BinaryNode<T>{
		BinaryNode(T theElement){
			this(theElement, null, null);
		}
		BinaryNode(T theElement, BinaryNode<T> lt, BinaryNode<T> rt){
			element = theElement;
			left = lt;
			right = rt;
		}
		T element;
		BinaryNode<T> left;
		BinaryNode<T> right;
	}
	
	//����������Ψһ���������ǶԸ��ڵ�����á�
	private BinaryNode<T> root;
	public BinarySearchTree(){
		root = null;
	}
	
	public void makeEmpty(){
		root = null;
	}
	public boolean isEmpty(){
		return root == null;
	}
	
	public boolean containts(T t){
		return contains(t, root);
	}
	public T findMin(){
		if(isEmpty()){
			throw new RuntimeException();
		}
		return findMin(root).element;
	}
	public T findMax(){
		if(isEmpty()){
			throw new RuntimeException();
		}
		return findMax(root).element;
	}
	public void insert(T x){
		root = insert(x, root);
	}
	public void remove(T x){
		remove(x, root);
	}
	public void printTree(){
		
	}
	//�� contains(T t) ���������أ��������Ǹ� private �ķ���
	private boolean contains(T x, BinaryNode<T> t){
		if(t == null){
			return false;
		}
		//���ö��������������ʽ��е�����������Ϊ����ƽ��ʱ�临�Ӷ��� O(long N)�����Բ��õ���ջ���������
		int compareResult = x.compareTo(t.element);
		if(compareResult > 0){
			return contains(x, t.right);
		}else if(compareResult < 0){
			return contains(x, t.left);
		}else{
			return true;
		}
	}
	//����ʹ�õݹ��д findMin��ʹ�÷ǵݹ��д findMax
	private BinaryNode<T> findMin(BinaryNode<T> t){
		if(t == null){
			return null;
		}else if(t.left == null){
			return t;
		}
		return findMin(t.left);
	}
	private BinaryNode<T> findMax(BinaryNode<T> t){
		if(t == null){
			return null;
		}
		while(t.left != null){
			t = t.left;
		}
		return t;
	}
	/**
	 * ���в��������ʱ������ t ���ø����ĸ����������ڵ�һ�β���ʱ�仯����� insert ��д��һ�����ض����������õķ���
	 * @param x
	 * @param t
	 * @return
	 */
	private BinaryNode<T> insert(T x, BinaryNode<T> t){
		//�������ҵ�һ���սڵ��ʱ��������� x ����������ڵ�λ�á�
		if(t == null){
			return new BinaryNode<T>(x);
		}
		int compareResult = x.compareTo(t.element);
		if(compareResult > 0){
			insert(x, t.left);
		}else if(compareResult < 0){
			insert(x, t.right);
		}
		//�� compareResult == 0 ��ʱ��˵������ڵ��Ѿ������룬���ǲ������κβ���
		return t;
	}
	/**
	 * �����ѵĲ����� remove ��������Ϊ remove һ�����Ƿ���Ҫ��ɾ���Ľڵ㣬��Ҫ���Ǽ��������
	 * ����ڵ���һƬ��Ҷ����ô�����Ա�����ɾ��������ڵ���һ�����ӣ���ýڵ�������丸�ڵ�����Լ��������ƹ��ýڵ��ɾ����
	 * ���ӵ�����Ǵ�������������ӵĽڵ㡣һ���ɾ����������������������С���ݣ��������ҵ�)����ýڵ�����ݲ��ݹ��ɾ���Ǹ��ڵ㡣
	 * ��Ϊ�������е���С�ڵ㲻����������ӡ�Ҫ��ɾ���Ľڵ��Ǹ�������ӣ�
	 * @param x
	 * @param t
	 */
	private BinaryNode<T> remove(T x, BinaryNode<T> t){
		if(t == null){
			return t;//Item not fount; do nothing;
		}
		int compareResult = x.compareTo(t.element);
		if(compareResult < 0){
			t.left = remove(x, t.left);
		}else if(compareResult > 0){
			t.right = remove(x, t.right);
		}else if(t.left != null && t.right != null){
			//Two children;
			t.element = findMin(t.right).element;
			t.right = remove(t.element, t.right);
		}else{
			t = (t.left != null) ? t.left : t.right;
		}
		return t;
	}
}






































