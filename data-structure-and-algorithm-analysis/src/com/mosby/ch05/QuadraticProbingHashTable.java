package com.mosby.ch05;

/**
 * 使用平方探测法解决散列冲突问题:<br><br>
 * 
 * 这里，我们不使用链表数组，而是使用散列表项单元的数组。<br>
 * HashEntry 引用数组的每一项是下列 3 中情形之一：<br>
 * 1. null.<br>
 * 2. 非 null，且该项是活动的（isActive 为 true）<br>
 * 3. 非 null，且该项标记被删除(isActive 为 false)<br>
 */
public class QuadraticProbingHashTable <T> {
    public QuadraticProbingHashTable(){
        this(DEFAULT_TABLE_SIZE);
    }
    public QuadraticProbingHashTable(int size){
        allocateArray(size);
        makeEmpty();
    }
    public void makeEmpty(){
        currentSize = 0;
        for(int i = 0; i < array.length; i++){
            array[i] = null;
        }
    }
    /**
     * 调用私有方法 isActive 和 findPos。这里的 private 方法findPos 实施对冲突的检测。<br>
     * 我们肯定在 insert 里程中散列表至少为该表中元素个数的两倍 (重载因子 λ 小于 0.5)，
     * 这样平方探测解决方案总可以实现。
     * @param x
     * @return
     */
    public boolean contains(T x){
        int currentPos = findPos(x);
        return isActive(currentPos);
    }
    public void insert(T x){
        int currentPos = findPos(x);
        //如果 x 已经存在，则我们什么都不做
        if(isActive(currentPos)){
            return;
        }
        array[currentPos] = new HashEntry<T>(x, true);
        if(++currentSize > array.length / 2){
            rehash();
        }
    }
    public void remove(T x){
        int currentPos = findPos(x);
        if(isActive(currentPos)){
            array[currentPos].isActive = false;
        }
    }
    private static class HashEntry<T>{
        public T element;
        public boolean isActive;
        public HashEntry(T e){
            this(e, true);
        }
        public HashEntry(T element, boolean isActive){
            this.element = element;
            this.isActive = isActive;
        }
    }
    private static final int DEFAULT_TABLE_SIZE = 11;
    
    private HashEntry<T>[] array;
    private int currentSize;
    
    private void allocateArray(int arraySize){
        array = new HashEntry[arraySize];
    }
    private boolean isActive(int currentPos){
        return array[currentPos] != null && array[currentPos].isActive;
    }
    /**
     * 寻找 x 插入哈希表中所在的位置
     * @param x
     * @return
     */
    private int findPos(T x){
        int offset = 1;
        int currentPos = myhash(x);
        
        while(array[currentPos] != null && !array[currentPos].element.equals(x)){
            currentPos += 2 * offset - 1;
            offset++;
            if(currentPos >= array.length){
                currentPos -= array.length;
            }
        }
        
        return currentPos;
    }
    private void rehash(){
        HashEntry<T>[] oldArray = array;
        
        allocateArray(nextPrime(2 * oldArray.length));
        currentSize = 0;
        for(int i = 0; i < oldArray.length; i++){
            if(oldArray[i] != null && oldArray[i].isActive){
                insert(oldArray[i].element);
            }
        }
    }
    private int myhash(T x){
        int hashVal = x.hashCode();
        hashVal %= array.length;
        if(hashVal < 0){
            hashVal += array.length;
        }
        return hashVal;
    }
    private static int nextPrime(int n){
        if(n % 2 == 0){
            n++;
        }
        for(; !isPrime(n); n += 2){
            
        }
        return n;
    }
    private static boolean isPrime(int n){
        if(n == 2 || n == 3){
            return true;
        }
        if(n == 1 || n % 2 == 0){
            return false;
        }
        for(int i = 3; i * i <= n; i += 2){
            if(n % i == 0){
                return false;
            }
        }
        return true;
    }
}
