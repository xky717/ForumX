package io.github.xky717.forumX.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {


    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final  String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){

        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                ){
                  String keyword;
                  while ((keyword = bufferedReader.readLine()) != null){
                      //添加到Trie里
                      this.addKeyword(keyword);
                  }
        } catch (IOException e) {
            logger.error("loading sensitive words failed", e);
        }
    }

    //将敏感词加入到trie里
    private void addKeyword(String keyword){

        TrieNode tmpNode = rootNode;

        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);

            TrieNode subNode = tmpNode.getSubNode(c);

            if(subNode== null){
                //初始化子节点
                subNode = new TrieNode();
                tmpNode.addNode(c,subNode);
            }
            //下一个子节点
            tmpNode = subNode;
            //设置结束标识
            if(i == keyword.length() - 1 ){
                tmpNode.setKeyWordEnd(true);
            }

        }

    }


    /**
     * 过滤敏感词
     * @param text 等待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针一
        TrieNode tmpNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //result
        StringBuilder stringBuilder = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);
            // spring symbol
            if (isSymbol(c)){
              //如果指针1在根结点，那么指针2向下走一步
                if(tmpNode == rootNode){
                    stringBuilder.append(c);
                    begin ++;
                }
                position ++;
                continue;
            }
            //检查下级节点
            tmpNode = tmpNode.getSubNode(c);
            if(tmpNode == null){
                //以begin开头的字符串不是敏感词
                stringBuilder.append(text.charAt(begin));
                position = ++begin;
                tmpNode = rootNode;
            }else if (tmpNode.isKeyWordEnd()){
                //找到敏感词，替换在begin - position位置上的字符串
                stringBuilder.append(REPLACEMENT);
                begin = ++position;
                //重新指向根节点
                tmpNode = rootNode;
            }else{
                //检查下一个字符
                position ++;
            }
        }
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();


    }

    //判断特殊符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c);
    }

    

    private class TrieNode{

        private boolean isKeyWordEnd = false;

        //子节点(key是下级的字符，value是下级的节点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addNode( Character c, TrieNode node){
            subNodes.put(c,node);

        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }


    }
    
}
