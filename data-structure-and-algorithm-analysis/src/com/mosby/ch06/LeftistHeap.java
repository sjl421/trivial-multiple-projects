package com.mosby.ch06;

/**
 * 左式堆：与普通二叉堆区别在于，左式堆不是一个完全二叉树，并且左式堆不是一个理想平衡二叉树。
 */
public class LeftistHeap <E extends Comparable<? super E>> {
    public LeftistHeap(){
        root = null;
    }

    /**
     * 公有的 merge 方法将 anotherLeftistHeap 合并到控制堆中。
     * 随后 anotherLeftistHeap 变成了空的。
     * 在第一趟，我们通过合并两个堆的右路径建立一棵新的树。为此，以排序的方式安排 H<sub>1</sub> 和 H<sub>2</sub>
     * 右路径上的节点，保持他们各自的左儿子不变。
     * 第二趟构成堆，儿子的交换工作在左式堆性质被破坏的那些节点上进行。
     * <br>
     * @param anotherLeftistHeap 被合并的左式树
     */
    public void merge(LeftistHeap<E> anotherLeftistHeap){
        if(this == null){
            return ;
        }
        root = merge(root, this.root);
        anotherLeftistHeap.root = null;
    }

    /**
     * 向左式树中插入新的元素
     * <br>
     * @param x
     */
    public void insert(E x){
        root = merge(new Node<>(x), root);
    }

    /**
     * 寻找左式堆中最小的元素
     * <br>
     * @return 左式堆最小元素
     */
    public E findMin(){
        if(isEmpty()){
            return null;
        }
        return root.theElement;
    }

    /**
     * 删除左式堆中最小元素，并返回该元素
     * <br>
     * @return 被删除的元素
     */
    public E deleteMin(){
        if(isEmpty()){
            return null;
        }
        E minItem = root.theElement;
        root = merge(root.left, root.right);

        return minItem;
    }

    /**
     * 返回左式堆是否为空
     * <br>
     * @return
     */
    public boolean isEmpty(){
        return root == null;
    }

    /**
     * 将左式堆设置为空堆
     */
    public void makeEmpty(){
        root = null;
    }

    /**
     * 内部类用于表示左式堆的节点，相对于普通的二叉树多了 npl（null path length）用于记录空路径长
     * <br>
     * @param <E> 节点中的存储的对象
     */
    private static class Node<E>{
        Node(E theElement){
            this(theElement, null, null);
        }
        Node(E theElement, Node<E> left, Node<E> right){
            this.theElement = theElement;
            this.left = left;
            this.right = right;
            npl = 0;
        }

        E theElement;
        Node<E> left;
        Node<E> right;
        int npl;
    }

    private Node<E> root;

    /**
     * merge 方法被用于消除一些特殊情形并保证 H<sub>1</sub> 有较小的根。
     * <br>
     * @param h1
     * @param h2
     * @return
     */
    private Node<E> merge(Node<E> h1, Node<E> h2){
        if(h1 == null){
            return h2;
        }
        if(h2 == null){
            return h1;
        }
        if(h1.theElement.compareTo(h2.theElement) < 0){
            return merge1(h1, h2);
        }else{
            return merge1(h2, h1);
        }
    }

    /**
     * merge1 执行实际的合并操作，并且在 merge1 的调用中，h<sub>1</sub> 小于 h<sub>2</sub>
     * <br>
     * @param h1
     * @param h2
     * @return
     */
    private Node<E> merge1(Node<E> h1, Node<E> h2){
        //根据左式堆的性质，如果 h1.left == null，那么 h1.right == null 也成立
        if(h1.left == null){
            h1.left = h2;
        }else{
            h1.right = merge(h1.right, h2);
            if(h1.left.npl < h1.right.npl){
                swapChildren(h1);
            }
            h1.npl = h1.right.npl + 1;
        }
        return h1;
    }

    private void swapChildren(Node<E> t){
        Node<E> tmp = t.left;
        t.right = t.left;
        t.left = tmp;
    }
}
