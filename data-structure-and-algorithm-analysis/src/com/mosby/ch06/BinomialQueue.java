package com.mosby.ch06;

/**
 * 二项队列
 */
@SuppressWarnings("unchecked")
public class BinomialQueue<E extends Comparable<? super E>> {
    /**
     * 在合并森林中的高度为 k 的二项树时的几种情况<br/>
     * 包含 this.forest[i],anotherBinomialQueue.forest[i],carry<br/>
     * carry 为由高度为 k-1 的二项树合并成为的二项树
     */
    //没有二项树
    private static final int NO_TREES = 0x0;
    //只有 this 中有二项树
    private static final int ONLY_THIS = 0x1;
    //只有 anotherBinomialQueue 中有二项树
    private static final int ONLY_MERGED_TREES = 0x2;
    //只有 carry 包含二项树
    private static final int ONLY_CARRY = 0x4;
    //只有 this 和 carry 中的二项树
    private static final int THIS_AND_MERGED_TRESS = 0x3;
    //只有 this 和 carry 中有二项树
    private static final int THIS_AND_CARRY = 0x5;
    //只有 anotherBinomialQueue 中有二项树
    private static final int MERGED_TREES_AND_CARRY = 0x6;
    //this、anotherBinomialQueue、carry 中都包含了二项树
    private static final int ALL_TREES = 0x7;

    public BinomialQueue(){
        forest = new BinomialNode[MAX_TREES];
        makeEmpty();
    }
    public BinomialQueue(E e){
        this();
        currentSize = 1;
        insert(e);
    }

    public void merge(BinomialQueue<E> anotherBinomialQueue){
        if(this == anotherBinomialQueue || anotherBinomialQueue == null){
            return;
        }

        currentSize += anotherBinomialQueue.currentSize;

        if(currentSize > capacity()){
            int maxLength = Math.max(forest.length, anotherBinomialQueue.forest.length);
            expandTheForest(maxLength + 1);
        }

        //carry 是从上一步得来的树
        BinomialNode<E> carry = null;
        for(int i = 0, j = 1; j <= currentSize; i++, j*=2){
            BinomialNode<E> t1 = forest[i];
            BinomialNode<E> t2 = i < anotherBinomialQueue.forest.length ? anotherBinomialQueue.forest[i] : null;

            int whichCase = (t1 == null ? 0 : 1) & 0x0;
            whichCase = (t2 == null ? 0 : 2) & whichCase;
            whichCase = (carry == null ? 0 : 4) & whichCase;

            switch(whichCase){
                case NO_TREES:
                    break;
                case ONLY_THIS:
                    break;
                case ONLY_MERGED_TREES:
                    forest[i] = t2;
                    anotherBinomialQueue.forest[i] = null;
                    break;
                case ONLY_CARRY:
                    forest[i] = carry;
                    carry = null;
                    break;
                case THIS_AND_MERGED_TRESS:
                    carry = combineTrees(t1, t2);
                    forest[i] =  null;
                    anotherBinomialQueue.forest[i] = null;
                    break;
                case THIS_AND_CARRY:
                    carry = combineTrees(t1, carry);
                    forest[i] = null;
                    break;
                case MERGED_TREES_AND_CARRY:
                    carry = combineTrees(t2, carry);
                    anotherBinomialQueue.forest[i] = null;
                    break;
                //在这种情况下，其实选择合并那两棵树是不会影响程序的正确性的
                case ALL_TREES:
                    forest[i] = carry;
                    carry = combineTrees(t1, t2);
                    anotherBinomialQueue.forest[i] = null;
                    break;
            }
        }
        anotherBinomialQueue.makeEmpty();
    }
    public void insert(E x){
        merge(new BinomialQueue<>(x));
    }
    public E findMin(){
        if(isEmpty()){
            return null;
        }
        return forest[finMinIndex()].element;
    }
    public E deleteMin(){
        if(isEmpty()){
            return null;
        }

        int minIndex = finMinIndex();
        E minItem = forest[minIndex].element;

        //在我们使用的二项树中，根节点只包含自身 element 以及它的儿子，并没有兄弟节点
        BinomialNode<E> deletedTree = forest[minIndex].leftChild;

        BinomialQueue<E> deletedQueue = new BinomialQueue<>();
        /**
         * 高度为 minIndex 的二项树的元素个数为 2<sup>minIndex</sup>个
         * 删除掉 root 元素后，转化为了 0,1,..., minIndex-1 棵二项树。
         */

        deletedQueue.currentSize = (1 << minIndex) - 1;

        for(int j = minIndex - 1; j >= 0; j--){
            deletedQueue.forest[j] = deletedTree;
            deletedTree = deletedTree.nextSibling;
            deletedQueue.forest[j].nextSibling = null;
        }

        forest[minIndex] = null;
        currentSize -= deletedQueue.currentSize + 1;

        merge(deletedQueue);

        return minItem;
    }
    public boolean isEmpty(){
        return  currentSize == 0;
    }
    public void makeEmpty(){
        currentSize = 0;
        for(int i = 0; i < forest.length; i++){
            forest[i] = null;
        }
    }

    //
    private static final int MAX_TREES = 14;
    private int currentSize; //整个森林中 Node 的数量
    private BinomialNode<E>[] forest;//森林，森林中的每一个元素都是一个二项树

    private void expandTheForest(int newNumTrees){
        BinomialNode<E>[] newForest = new BinomialNode[newNumTrees];
        for(int i = 0; i < forest.length; i++){
            newForest[i] = forest[i];
        }
        forest = newForest;
    }

    /**
     * 当森林中有两棵同阶的二项树时，这两颗二项树必须合并为一棵更高阶的二项树。<br/>
     * 在合并的时候，应当以两棵二项树中 root 值较小的节点作为根节点，
     * 将 root 值较大的节点作为根节点的左子节点，同时，以降秩的形式排列它的所有子节点。
     * @param binomialTree 第一棵二项树
     * @param anotherBinomialTree 另外的二项树
     * @return 合并后得到的比另外两棵二项树高一阶的二项树
     */
    private BinomialNode<E> combineTrees(BinomialNode<E> binomialTree, BinomialNode<E> anotherBinomialTree){
        if(binomialTree.element.compareTo(anotherBinomialTree.element) > 0){
            return combineTrees(anotherBinomialTree, binomialTree);
        }
        anotherBinomialTree.nextSibling = binomialTree.leftChild;
        binomialTree.leftChild = anotherBinomialTree;

        return binomialTree;
    }

    /**
     * 返回森林的最大容积，一棵高度为 K 的二项树的元素个数为 2<sup>K</sup> 个<br/>
     * 而根据森林的定义，forest 数组从 0->forest.length - 1 分别存放了高度为1,...,
     * 高度为 forest.length 的二项树<br/>
     * 所以总的容积为 2<sup>forest.length</sup> - 1 个元素。
     * @return 二项队列的最大容积
     */
    private int capacity(){
        return (1 << forest.length) - 1;
    }
    private int finMinIndex(){
        int i, minIndex;

        //这个循环是必要的，因为我们在后面的循环中初始化 minIndex 为 i
        //如果缺少这个循环，那么在后面的循环中，如果 forest[0] 是 null 的话，
        //那么我们的 compareTo 的参数将为 null
        for(i = 0; forest[i] == null; i++)
            ;

        for(minIndex = i; i < forest.length; i++){
            if(forest[i] != null && forest[i].element.compareTo(forest[minIndex].element) < 0){
                minIndex = i;
            }
        }

        return minIndex;
    }

    /**
     * 每一个 BinomialNode 都必须保存三个值：<br/>
     * 1. 该节点的值
     * 2. 该节点的左子节点（注意，这里的左子节点是对应图 6-51 中的左子节点）
     * 3. 该节点的右兄弟，根元素没有右兄弟
     * @param <E>
     */
    public static class BinomialNode<E>{
        BinomialNode(E element){
            this(element, null, null);
        }
        BinomialNode(E element, BinomialNode<E> leftChild, BinomialNode<E> nextSibling){
            this.element = element;
            this.leftChild = leftChild;
            this.nextSibling = nextSibling;
        }
        E element;
        BinomialNode<E> leftChild;
        BinomialNode<E> nextSibling;
    }
}
