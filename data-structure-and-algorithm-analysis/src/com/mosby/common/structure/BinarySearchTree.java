package com.mosby.common.structure;

/**
 * 二叉搜索树的定义
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
	
	//二叉搜索树唯一的数据域是对根节点的引用。
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
	//对 contains(T t) 方法的重载，但是它是个 private 的方法
	private boolean contains(T x, BinaryNode<T> t){
		if(t == null){
			return false;
		}
		//利用二叉搜索树的性质进行迭代搜索，因为它的平均时间复杂度是 O(long N)，所以不用担心栈溢出的问题
		int compareResult = x.compareTo(t.element);
		if(compareResult > 0){
			return contains(x, t.right);
		}else if(compareResult < 0){
			return contains(x, t.left);
		}else{
			return true;
		}
	}
	//我们使用递归编写 findMin，使用非递归编写 findMax
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
	 * 进行插入操作的时候，由于 t 引用该树的根，而根又在第一次插入时变化，因此 insert 被写成一个返回对新树根引用的方法
	 * @param x
	 * @param t
	 * @return
	 */
	private BinaryNode<T> insert(T x, BinaryNode<T> t){
		//当我们找到一个空节点的时候，这里就是 x 被插入后所在的位置。
		if(t == null){
			return new BinaryNode<T>(x);
		}
		int compareResult = x.compareTo(t.element);
		if(compareResult > 0){
			insert(x, t.left);
		}else if(compareResult < 0){
			insert(x, t.right);
		}
		//在 compareResult == 0 的时候，说明这个节点已经被插入，我们不用做任何操作
		return t;
	}
	/**
	 * 最困难的操作是 remove 操作，因为 remove 一旦我们发现要被删除的节点，就要考虑几种情况。
	 * 如果节点是一片树叶，那么它可以被立即删除。如果节点有一个儿子，则该节点可以在其父节点调整自己的链以绕过该节点后被删除。
	 * 复杂的情况是处理具有两个儿子的节点。一般的删除策略是用其右子树的最小数据（很容易找到)代替该节点的数据并递归的删除那个节点。
	 * 因为右子树中的最小节点不可能有左儿子。要被删除的节点是根的左儿子；
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






































