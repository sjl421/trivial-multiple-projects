package com.mosby.ch05;

import java.util.LinkedList;
import java.util.List;

/**
 * �������ӷ�ʵ�ֹ�ϣ��
 */
public class SeparateChainingHashTable <T> {

    public SeparateChainingHashTable(){
        this(DEFAULT_TABLE_SIZE);
    }
    
    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int size){
        //���� Java ����ʹ�����Ͳ����ķ�ʽʵ�֣�Java ������������������
        //��Σ������������ڲ��������鳤�ȵ�ʱ��Ӧ������һ��������Ϊ��������ĳ���
        theLists = new LinkedList[nextPrime(size)];
        for(int i = 0; i < theLists.length; i++){
            theLists[i] = new LinkedList<>();
        }
    }
    
    /**
     * �ڲ��뷽���У��������������Ѿ����ڣ���ô���ǲ�ִ���κβ�����<br>
     * �������ǽ�����������С���Ԫ�ؿ��Ա��ŵ������е��κ�λ�ã������ǵ�������ʹ�� add ������Ϊ���㡣
     * <br><br>
     * �������⣬�κη��������Խ����ͻ����һ�ö������������������һ��ɢ�б���ʤ�����������
     * ���ǣ������������ɢ�б��Ǵ�Ĳ���ɢ�к����Ǻõģ���ô���е�����Ӧ���Ƕ̵ģ�
     * �Ӷ��κθ��ӵĳ��Զ���ֵ�ÿ����ˡ�
     * @param x
     */
    public void insert(T x){
        List<T> whichList = theLists[myHash(x)];
        if(!whichList.contains(x)){
            whichList.add(x);
        }
        /**
         * װ������ �� ������ȷ����currentSize Ϊ������ϣ��Ԫ�صĸ�����
         * ���� �� Ϊ 1. 
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
        //�ҵ������������е�λ��
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
     * ����� hash ֵ���ܻ�����ڲ�����ĳ��ȣ�������Ҫ���¼����ϣֵ
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
