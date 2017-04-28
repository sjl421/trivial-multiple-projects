package com.mosby.ch05;

public class HashValue {
    /**
     * һ��ʵ���ַ�������Ĺ�ϣ·�ɵķ�ʽ<br>
     * �����ַ�ʽ�£�hash ��Ȼû�н����ͻ����
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
        //hashVal ����Ϊ��ֵ�����Ƕ���ȥ tableSize ��ģ
        if(hashVal < 0){
            hashVal += tableSize;
        }
        return hashVal;
    }
}
