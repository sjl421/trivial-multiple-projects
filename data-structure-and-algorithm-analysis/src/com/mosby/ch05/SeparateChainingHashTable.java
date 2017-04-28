package com.mosby.ch05;

import java.util.LinkedList;
import java.util.List;

/**
 * 分离链接法实现哈希表：
 */
public class SeparateChainingHashTable <T> {

    public SeparateChainingHashTable(){
        this(DEFAULT_TABLE_SIZE);
    }
    
    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int size){
        //由于 Java 泛型使用类型擦除的方式实现，Java 不允许声明泛型数组
        //其次，我们在声明内部链表数组长度的时候应该声明一个素数作为链表数组的长度
        theLists = new LinkedList[nextPrime(size)];
        for(int i = 0; i < theLists.length; i++){
            theLists[i] = new LinkedList<>();
        }
    }
    
    /**
     * 在插入方法中，如果被插入的项已经存在，那么我们不执行任何操作；<br>
     * 否则，我们将其放入链表中。该元素可以被放到链表中的任何位置；在我们的情形下使用 add 方法最为方便。
     * <br><br>
     * 除链表外，任何方案都可以解决冲突现象；一棵二叉查找树甚至是另外一个散列表都将胜任这个工作，
     * 但是，我们期望如果散列表是大的并且散列函数是好的，那么所有的链表都应该是短的，
     * 从而任何复杂的尝试都不值得考虑了。
     * @param x
     */
    public void insert(T x){
        List<T> whichList = theLists[myHash(x)];
        if(!whichList.contains(x)){
            whichList.add(x);
        }
        /**
         * 装填因子 λ 在这里确定，currentSize 为整个哈希表元素的个数；
         * 这里 λ 为 1. 
         */
        if(++currentSize > theLists.length){
            rehash();
        }
    }
    
    public void remove(T x){
        List<T> whichList = theLists[myHash(x)];
        if(whichList.contains(x)){
            whichList.remove(x);
            currentSize--;
        }
    }
    public boolean contains(T x){
        //找到在链表数组中的位置
        List<T> whichList = theLists[myHash(x)];
        return whichList.contains(x);
    }
    public void makeEmpty(){
        for(int i = 0; i < theLists.length; i++){
            theLists[i].clear();
        }
        currentSize = 0;
    }
    public static final int DEFAULT_TABLE_SIZE = 101;
    
    private List<T>[] theLists;
    private int currentSize;
    
    @SuppressWarnings("unchecked")
    public void rehash(){
        List<T>[] oldLists = theLists;
        
        theLists = new List[nextPrime(2 * theLists.length)];
        for(int j = 0; j < theLists.length; j++){
            theLists[j] = new LinkedList<T>();
        }
        currentSize = 0;
        for(int i = 0; i < oldLists.length; i++){
            for(T item : oldLists[i]){
                insert(item);
            }
        }
    }
    /**
     * 对象的 hash 值可能会大于内部数组的长度，所以需要重新计算哈希值
     * @param x
     * @return
     */
    private int myHash(T x){
        int hashVal = x.hashCode();
        
        hashVal %= theLists.length;
        if(hashVal < 0){
            hashVal += theLists.length;
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
