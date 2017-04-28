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
     * ��Ѳ���һ��Ԫ��<br><br>
     *
     * <blockquote>
     *
     * ���������ǵĴ���ʹ����һ��С���ɣ��������ڵ�Ŀ����Ҫ����ǰ���еĿ�Ѩ����ʼΪ���������һ��Ԫ��֮��
     * �ƶ���һ�����㽫 X ����ÿ�Ѩ��Ӱ��ѵ����ʵ�λ�á�<br><br>
     *
     * �������ÿ�ζ�����ǰ��Ѩ��λ�ú����ĸ�Ԫ�ؽ�������ô����һ��Ԫ������ d �㣬
     * ��ô���ڽ�����ִ�еĸ�ֵ�������� 3d��<br><br>
     *
     * ���������ÿ��ֻ������������ʱ�����ڵ��ֵ�����������Ѩ��û�н���Ѩ��ֵ���ˡ�<br>
     * �������� d �㽫ֻ��Ҫ d �ζԿ�Ѩ�ĸ�ֵ��һ����� X ����ĸ�ֵ���ܹ� d+1 �θ�ֵ��
     *
     * </blockquote>
     *
     * @param x
     */
    public void insert(T x){
        //��Ϊ���ڲ�������ʵ�ֵĵ�һ��Ԫ���ǿ�
        if(currentSize == array.length - 1){
            enlargeArray(array.length * 2 + 1);
        }
        
        //��ǰ��Ѩ��λ�������һ��Ԫ�صĺ�һλ��ͬʱ�����Ѩ֮�� currentSize ����һ����ͬ������Ĵ���
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
    
    private int currentSize;//��ǰ����Ԫ�ظ���
    private Comparable<? super T>[] array;//���ڲ������������ʽ���
    
    /**
     * �Կ�Ѩ��������
     * @param hole ��Ѩ
     */
    private void percolateDown(int hole){
        //�������λ�õĴ����� eclipse �½��ᱨ������ eclipse �µ�һ�� bug���� IDEA �½��������������
        int child;
        Comparable<? super T> tmp = array[hole];
       
        /**
         * ����ע��һ�㣬hole * 2 <= currentSize����Ϊ����ĵ�һ��Ԫ��Ϊ��<br>
         * �����е�ʵ��Ԫ��Ӧ���� array[i] - array[currentSize]
         */
        for(; hole * 2 <= currentSize; hole = child){
            child = hole * 2;
            /**
             * �����˵Ĺ����У�����ÿ�ν���ǰ�ڵ�������ӽڵ��н�С���Ǹ��ӽڵ����Ѩ����<br>
             * 
             * ���������Ҫ����һ�����⣬�����²��ʱ�򣬿��ܻ���ĳ���ڵ�ֻ��һ���ӽڵ�<br>
             * 
             * ���ڷ����²��򲻻���������⣬��Ϊ�������һ����ȫ��������<br>
             * 
             * �����ݶ���ѵĲ������ʣ��������Ҳ��룩����ôֻ��һ��Ԫ�صĽڵ㣬���Ԫ�ص��ӽڵ�϶�
             * ���Ƕ���ѵ����һ���ڵ㡣��ʱ hole == currentSize.
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

































