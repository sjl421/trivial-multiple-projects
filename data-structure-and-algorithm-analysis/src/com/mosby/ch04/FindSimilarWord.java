package com.mosby.ch04;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ���� N �����ʣ�����Щ�����о����任ĳ1����ĸ���ܹ��õ��µĵ��ʵ��������� 15 �ĵ��ʵĺϼ�.
 */
public class FindSimilarWord {
    /**
     * ��������һЩ������Ϊ�ؼ��ֺ�ֻ��һ����ĸ�ϲ�ͬ��һ�е�����Ϊ�ؼ��ֵ�ֵ��
     * �����Щ���� minWords �����߸����ͨ�� 1 ��ĸ�滻�õ��ĵ��ʵĵ��ʡ�
     * 
     * @param adjWords key Ϊԭʼ���ʣ� value Ϊ����ԭʼ���ʾ��� 1 ��ĸת�������ܱ�ɵĵ��ʵ��б�
     * @param minWords ԭʼ���ʾ���1��ĸת�������ܵõ��ĵ�����С������ֻ�г�������������ǲ���Ϊ�����������ġ�
     */
    public static void printHighChangeables(Map<String, List<String>> adjWords, int minWords){
        for(Map.Entry<String, List<String>> entry : adjWords.entrySet()){
            List<String> words = entry.getValue();
            if(words.size() >= minWords){
                System.out.print(entry.getKey() + "(");
                System.out.print(words.size() + "):");
                for(String word : words){
                    System.out.print(" " + word);
                }
                System.out.println();
            }
        }
    }
    
    /**
     * ������������Ƿ�ֻ��һ����ĸ�ϲ�ͬ�ķ���
     * @param word1
     * @param word2
     * @return
     */
    public static boolean oneCharOff(String word1, String word2){
        if(word1.length() != word2.length()){
            return false;
        }
        int diffs = 0;
        for(int i = 0; i < word1.length(); i++){
            if(word1.charAt(i) != word2.charAt(i)){
                if(++diffs > 1){
                    return false;
                }
            }
        }
        return diffs == 1;
    }
    
    
    /**
     * �÷�����һ���ֵ�ת��Ϊ�Ե���Ϊ key�����ʿ���ͨ�� 1 ��ĸת��Ϊ�����е��ʵ� List Ϊ value��
     * �ú�����һ�� 89,0000 ���ʵĴʵ����� 96 ��
     * @param thWords
     * @return
     */
    public static Map<String, List<String>> computeAdjacentWords(List<String> theWords){
        Map<String, List<String>> adjWords = new TreeMap<String, List<String>>();
        
        String[] words = new String[theWords.size()];
        
        theWords.toArray(words);
        for(int i = 0; i < words.length; i++){
            for(int j = i + 1; j < words.length; j++){
                if(oneCharOff(words[i], words[j])){
                    update(adjWords, words[i], words[j]);
                    update(adjWords, words[j], words[i]);
                }
            }
        }
        
        return adjWords;
    }
    
    /**
     * ����ķ���Ч�ʱȽϵͣ�����㷨ʹ��һЩ���ӵ�ӳ�䣡��ǰ���һ���������ʰ��ճ��ȷ��飬Ȼ��ֱ��ÿ�����㡣
     * Ϊ�������㷨����ι����ģ��������Ƕӳ���Ϊ 4 �ĵ��ʲ�������ʱ������Ҫ�ҳ��� wine �� nine �����ĵ��ʶԣ�
     * ���ǳ��� 1 ����ĸ����ȫ��ͬ��
     * ���ڳ���Ϊ 4 ��ÿһ�����ʣ�һ��������ɾ���� 1 ����ĸ������һ�� 3 ��ĸ���ʴ����������г���һ�� Map�����еĹؼ���
     * Ϊ���ִ�������ֵ�����а���ͬһ����ĵ��ʵ�һ�� List��
     * ���磬�ڿ��� 4 ��ĸ������ĵ� 1 ����ĸʱ������ "ine" ��Ӧ  "dine"��"fine"��"nine"��"mine"��"pine"��"line"��
     * ÿһ����Ϊ���� Map ��һ��ֵ�� List �����γɵ��ʵ�һ�����ţ���������һ�����ʾ�����ͨ������ĸ�滻��Ϊ����һ�����ʣ�
     * ������������ Map ����֮�󣬺����ױ��������һЩ����ڼ����ԭʼ Map �С�
     * Ȼ������ʹ��һ���µ� Map �ٴ��� 4 ��ĸ����ĵ� 2 ����ĸ��ֱ���� 4 ����ĸ��
     * @param theWords
     * @return
     */
    public static Map<String, List<String>> computeAdjacentWords2(List<String> words){
        Map<String, List<String>> adjWords = new TreeMap<String, List<String>>();
        Map<Integer, List<String>> wordsByLength = new TreeMap<Integer, List<String>>();
        
        //������ĸ�ĳ��Ƚ��з���
        for(String word : words){
            update(wordsByLength, word.length(), word);
        }
        
        //����ÿһ�������ĸ���ȷ�����в���
        for(Map.Entry<Integer, List<String>> entry : wordsByLength.entrySet()){
            List<String> groupsWords = entry.getValue();
            int groupNum = entry.getKey();
            
            //�ڷ����ַ���ÿһλ���в���
            for(int i = 0; i < groupNum; i++){
                //ɾ��ָ��λ���ϵ��ַ�����ɾ���ַ�ʹ�� update �������£���Щ�����ַ��Ϳ��Ա���ŵ�һ�� Map ���ˡ�
                Map<String, List<String>> repToWord = new TreeMap<String, List<String>>();
                
                //�����е���ͬ���ȵ��ַ�ɾ������ i λ���ŵ�һ��������
                for(String str : groupsWords){
                    String rep = str.substring(0, i) + str.substring(i+1);
                    update(repToWord, rep, str);
                }
                
                /*
                 * �� repToWord �У�key ��ɾ�����ַ�����ĳһλ�ַ����ַ����� value ���ַ���ԭ���ַ���
                 * ���ң���Щ�ַ����ǿ���ͨ���ַ��ı��ַ�����ĳ 1 λ���໥ת���ģ�
                 * ���Ǳ������ List�������ַ���ÿһ���ַ������Ƕ�������ӵ�����Ӧ�� Map<String, List<String>> �У�
                 * ���� String ��������ַ��������� List<String> ����������ͨ���ı�ĳһλ���õ����µ��ַ���
                 */
                for(List<String> wordClique : repToWord.values()){
                    if(wordClique.size() >= 2){
                        for(String s1 : wordClique){
                            for(String s2 : wordClique){
                                if(s1 != s2){
                                    update(adjWords, s1, s2);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return adjWords;
    }
    
    /**
     * ��һ�� value ���� key ���뵽 m ��Ӧ�ķ����С�
     * @param m
     * @param key
     * @param value
     */
    private static <KeyType> void update(Map<KeyType, List<String>> m, KeyType key, String value){
        List<String> list = m.get(key);
        if(list == null){
            list = new ArrayList<String>();
            m.put(key, list);
        }
        list.add(value);
    }
}



























