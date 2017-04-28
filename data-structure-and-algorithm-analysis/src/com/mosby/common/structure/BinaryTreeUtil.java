package com.mosby.common.structure;

public class BinaryTreeUtil {
	public static <T> void postOrderIterate(BinaryTree<T> root, int depth){
		if(root == null){
			return;
		}
		postOrderIterate(root.left, depth + 1);
		postOrderIterate(root.right, depth + 1);
		while(depth-- > 0){
			System.out.print("	");
		}
		System.out.println(root.val);
	}
}
