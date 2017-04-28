package com.mosby.ch04;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 给定 N 个单词，求这些单词中经过变换某1个字母后能够得到新的单词的数量超过 15 的单词的合集.
 */
public class FindSimilarWord {
    /**
     * 给出包含一些单词作为关键字和只在一个字母上不同的一列单词作为关键字的值，
     * 输出那些具有 minWords 个或者更多个通过 1 字母替换得到的单词的单词。
     * 
     * @param adjWords key 为原始单词， value 为所有原始单词经过 1 字母转换后所能变成的单词的列表；
     * @param minWords 原始单词经过1字母转换后所能得到的单词最小数量，只有超过这个数量我们才认为是满足条件的。
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
     * 检测两个单词是否只在一个字母上不同的方法
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
     * 该方法将一个字典转换为以单词为 key，单词可以通过 1 字母转换为的所有单词的 List 为 value。
     * 该函数对一个 89,0000 单词的词典运行 96 秒
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
     * 上面的方法效率比较低，这个算法使用一些附加的映射！和前面的一样，将单词按照长度分组，然后分别对每组运算。
     * 为理解这个算法是如何工作的，假设我们队长度为 4 的单词操作。这时，首先要找出像 wine 和 nine 这样的单词对，
     * 他们除第 1 个字母外完全相同。
     * 对于长度为 4 的每一个单词，一种做法是删除第 1 个字母，留下一个 3 字母单词代表。这样就行成了一个 Map，其中的关键字
     * 为这种代表，而其值是所有包含同一代表的单词的一个 List。
     * 例如，在考虑 4 字母单词组的第 1 个字母时，代表 "ine" 对应  "dine"、"fine"、"nine"、"mine"、"pine"、"line"。
     * 每一个作为最后的 Map 的一个值得 List 对象都形成单词的一个集团，其中任意一个单词均可以通过单字母替换变为另外一个单词，
     * 因此在这个最后的 Map 构成之后，很容易遍历它添加一些项到正在计算的原始 Map 中。
     * 然后，我们使用一个新的 Map 再处理 4 字母词组的第 2 个字母。直至第 4 个字母。
     * @param theWords
     * @return
     */
    public static Map<String, List<String>> computeAdjacentWords2(List<String> words){
        Map<String, List<String>> adjWords = new TreeMap<String, List<String>>();
        Map<Integer, List<String>> wordsByLength = new TreeMap<Integer, List<String>>();
        
        //根据字母的长度进行分组
        for(String word : words){
            update(wordsByLength, word.length(), word);
        }
        
        //对于每一组根据字母长度分组进行操作
        for(Map.Entry<Integer, List<String>> entry : wordsByLength.entrySet()){
            List<String> groupsWords = entry.getValue();
            int groupNum = entry.getKey();
            
            //在分组字符的每一位进行操作
            for(int i = 0; i < groupNum; i++){
                //删除指定位置上的字符，在删除字符使用 update 方法更新，这些相似字符就可以被存放到一个 Map 中了。
                Map<String, List<String>> repToWord = new TreeMap<String, List<String>>();
                
                //将所有的相同长度的字符删除掉第 i 位后存放到一个数组中
                for(String str : groupsWords){
                    String rep = str.substring(0, i) + str.substring(i+1);
                    update(repToWord, rep, str);
                }
                
                /*
                 * 在 repToWord 中，key 是删除了字符串中某一位字符的字符，而 value 是字符的原本字符；
                 * 并且，这些字符间是可以通过字符改变字符串的某 1 位而相互转换的；
                 * 我们遍历这个 List，对于字符的每一个字符，我们都将它添加到他对应的 Map<String, List<String>> 中，
                 * 其中 String 就是这个字符串本身，而 List<String> 就是它可以通过改变某一位而得到的新的字符。
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
     * 将一个 value 根据 key 插入到 m 对应的分组中。
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



























