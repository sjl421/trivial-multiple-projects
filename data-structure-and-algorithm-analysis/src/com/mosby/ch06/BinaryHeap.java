package com.mosby.ch06;

/**
 * @author dhy
 */
@SuppressWarnings("unchecked")
public class BinaryHeap <T extends Comparable<? super T>> {
    public BinaryHeap(){
        this(DEFAULT_CAPACITY);
    }
    
    public BinaryHeap(int capacity){
        currentSize = 0;
        array = new Comparable[capacity];
    }

    /**
     * 向堆插入一个元素<br><br>
     *
     * <blockquote>
     *
     * 在这里我们的代码使用了一个小技巧：我们现在的目的是要将当前堆中的空穴（初始为数组中最后一个元素之后）
     * 移动到一个满足将 X 插入该空穴后不影响堆的性质的位置。<br><br>
     *
     * 如果我们每次都将当前空穴的位置和它的父元素交换，那么对于一个元素上滤 d 层，
     * 那么由于交换而执行的赋值次数就是 3d。<br><br>
     *
     * 而这里，我们每次只是在满足条件时将父节点的值赋给了这个空穴而没有将空穴的值上滤。<br>
     * 这样上滤 d 层将只需要 d 次对空穴的赋值和一次最后将 X 插入的赋值。总共 d+1 次赋值。
     *
     * </blockquote>
     *
     * @param x
     */
    public void insert(T x){
        //因为堆内部的数组实现的第一个元素是空
        if(currentSize == array.length - 1){
            enlargeArray(array.length * 2 + 1);
        }
        
        //当前空穴的位置在最后一个元素的后一位，同时插入空穴之后 currentSize 增加一。等同于下面的代码
        //int hole = currentSize + 1;
        //currentSize++;
        int hole = ++currentSize;
        for(; hole > 1 && x.compareTo((T) array[hole / 2]) < 0; hole /= 2){
            array[hole] = array[hole / 2];
        }
        array[hole] = x;
    }
    
    public T findMin(){
        if(isEmpty()){
            return null;
        }
        return (T) array[1];
    }
    
    public T deleteMin(){
        if(isEmpty()){
            throw new RuntimeException("Under flow");
        }
        
        T minItem = findMin();
        
        array[1] = array[currentSize--];
        percolateDown(1);
        
        return minItem;
    }
    
    public boolean isEmpty(){
        return currentSize == 0;
    }
    
    public void makeEmpty(){
        currentSize = 0;
    }
    
    private static final int DEFAULT_CAPACITY = 100;
    
    private int currentSize;//当前堆中元素个数
    private Comparable<? super T>[] array;//堆内部的以数组的形式存放
    
    /**
     * 对空穴进行下滤
     * @param hole 空穴
     */
    private void percolateDown(int hole){
        //下面这个位置的代码在 eclipse 下将会报错，这是 eclipse 下的一个 bug。在 IDEA 下将不会有这个问题
        int child;
        Comparable<? super T> tmp = array[hole];
       
        /**
         * 这里注意一点，hole * 2 <= currentSize，因为数组的第一个元素为空<br>
         * 数组中的实际元素应该是 array[i] - array[currentSize]
         */
        for(; hole * 2 <= currentSize; hole = child){
            child = hole * 2;
            /**
             * 在下滤的过程中，我们每次将当前节点的两个子节点中较小的那个子节点跟空穴交换<br>
             * 
             * 但是这必须要考虑一个问题，在最下层的时候，可能会有某个节点只有一个子节点<br>
             * 
             * 而在非最下层则不会有这个问题，因为二叉堆是一个完全二叉树。<br>
             * 
             * 而根据二叉堆的插入性质（从左往右插入），那么只有一个元素的节点，这个元素的子节点肯定
             * 就是二叉堆的最后一个节点。此时 hole == currentSize.
             */
            if(child != currentSize && array[child + 1].compareTo((T) array[child]) < 0){
                child++;
            }
            if(array[child].compareTo((T) tmp) < 0){
                array[hole] = array[child];
            }else{
                break;
            }
        }
        array[hole] = tmp;
    }
    
    /**
     * 
     */
    private void buildHeap(){
        for(int i = currentSize / 2; i > 0; i--){
            percolateDown(i);
        }
    }
    public void enlargeArray(int newSize){
        Comparable[] newArray = new Comparable[newSize];
        for(int i = 1; i <= currentSize; i++){
            newArray[i] = array[i];
        }
        array = newArray;
    }
    public int size(){
        return currentSize;
    }

    public static void main(String[] args) {
        BinaryHeap<Integer> instance = new BinaryHeap<>();
        instance.insert(1);
    }
}

































