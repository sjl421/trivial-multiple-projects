package com.mosby.ch05;

public class HashValue {
    /**
     * 一种实现字符串对象的哈希路由的方式<br>
     * 在这种方式下，hash 仍然没有解决冲突问题
     * @param key
     * @param tableSize
     * @return
     */
    public static int hash(String key, int tableSize){
        int hashVal = 0;
        for(int i = 0; i < key.length(); i++){
            hashVal = 37 * hashVal + key.charAt(i);
        }
        hashVal %= tableSize;
        //hashVal 可能为负值，我们对其去 tableSize 的模
        if(hashVal < 0){
            hashVal += tableSize;
        }
        return hashVal;
    }
}
