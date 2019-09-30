package whu.se.interpret.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import whu.se.interpret.po.Family;
import whu.se.interpret.po.Node;
import whu.se.interpret.po.SLRTable;
import whu.se.interpret.service.impl.ParserImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author xsy
 * @description: 语法分析方法的实现类
 * @date 2019/9/22 8:21
 */
@Getter
@Setter
@Service
public class Parser implements ParserImpl {

    private ArrayList<Node> grammar = new ArrayList<>();//产生式序列
    private HashMap<String, HashSet<String>> firstSet = new HashMap<>();//所有非终结符号的first集
    private HashMap<String, HashSet<String>> followSet = new HashMap<>();//所有非终结符号的follow集

    //初始化grammar，firstSet，followSet
    @Override
    public void init(String grammarFileName) throws IOException {
        this.grammar = getGrammar(grammarFileName);
        for (Node node : grammar) {
            String left = node.getLeft();//产生式左部
            if (!firstSet.containsKey(left)) {
                getFirst(left);
            }
        }
        followSet.put("<begin>", new HashSet<>());
        followSet.get("<begin>").add("$");
        for(int i = 1; i < grammar.size(); i++){
            String left = grammar.get(i).getLeft();//产生式左部
            if(!followSet.containsKey(left)) {
                getFollow(left);
            }
        }

    }


    @Override
    public boolean isTerm(String s) {
        //s为非终结符号
        if(s.charAt(0) == '<' && s.charAt(s.length()-1) == '>')
            return false;
        //s为空串
        else return !s.equals("ε");
        //s为终结符号
    }

    @Override
    public ArrayList<Node> getGrammar(String filename) throws IOException {
        //获取资源文件夹static
        Resource resource = new ClassPathResource("static/"+filename);
        //File path = new File(ResourceUtils.getURL("classpath:static").getPath().replace("%20"," ").replace('/', '\\'));
        //获取资源文件夹下的文法文件
        File grammarFile = resource.getFile();
        //最终的文法序列
        ArrayList<Node> grammar = new ArrayList<>();
        //逐行读取文法
        try(
                FileReader fr = new FileReader(grammarFile);
                BufferedReader br = new BufferedReader(fr)
                )
        {
            String line;

            while ((line = br.readLine())!=null){
                Node node = new Node();//产生式
                String[] splits = line.split(" ");//根据空格分割产生式字符串
                node.setLeft(splits[0]);//产生式左部
                ArrayList<String> right = new ArrayList<>();//产生式右部
                //将文法单元添加到产生式右部
                for(int i = 2; i < splits.length; i++){
                    right.add(splits[i]);
                }
                node.setRight(right);
                grammar.add(node);
            }
            return grammar;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }

    }


    private void getFirst(String target) {
        firstSet.put(target,new HashSet<>());
        //遍历产生式序列
        for (Node node : grammar) {
            //如果产生式左边等于target
            if (node.getLeft().equals(target)) {
                //遍历产生式右部
                ArrayList<String> right = node.getRight();//产生式右部
                for (int j = 0; j < right.size(); j++) {
                    //当前节点
                    String temp = right.get(j);

                    //当前节点为终结符号
                    if (isTerm(temp)) {
                        firstSet.get(target).add(temp);//直接加入target的first集
                        break;
                    }

                    //当前节点为空串
                    else if (temp.equals("ε")) {
                        //如果ε是产生式右部的最后一个元素，直接将ε加入到target的first集
                        if (j == right.size() - 1) {
                            firstSet.get(target).add("ε");
                        }
                    }

                    //当前节点为非终结符号
                    else {
                        //当右部当前元素等于左部时，直接跳过，避免左递归造成死循环
                        if (!temp.equals(node.getLeft())) {
                            //求当前非终结符号的first集
                            getFirst(temp);
                            //如果temp的first集包含空串
                            if (firstSet.get(temp).contains("ε")) {
                                firstSet.get(temp).remove("ε");
                                firstSet.get(target).addAll(firstSet.get(temp));//temp的first集 和 target的first集取并集
                            } else {
                                firstSet.get(target).addAll(firstSet.get(temp));//temp的first集 和 target的first集取并集
                                break;
                            }
                        } else
                            break;
                    }
                }
            }
        }
    }


    private void getFollow(String target) {
        followSet.put(target,new HashSet<>());
        for (Node node : grammar) {
            boolean flag = false;
            //遍历产生式右部，找到target
            int j;
            for (j = 0; j < node.getRight().size(); j++) {
                if (node.getRight().get(j).equals(target)) {
                    flag = true;
                    break;
                }
            }
            //在产生式右部找到了target后
            if (flag) {
                String left = node.getLeft();//产生式左部

                //target右边为空串
                if ((j == (node.getRight().size() - 1)) && !target.equals(left)) {
                    //并且target不等于产生式左部（等于左部时与自己取并集）
                    //与产生式左部的follow集取并集
                    if (!followSet.containsKey(left))
                        getFollow(left);
                    followSet.get(target).addAll(followSet.get(left));
                }

                //target右边不为空
                else {
                    //遍历右部符号
                    for (; j < node.getRight().size() - 1; j++) {
                        String temp = node.getRight().get(j + 1);
                        //右边为终结符号，直接装入
                        if (isTerm(temp)) {
                            followSet.get(target).add(temp);
                            break;
                        }
                        //右边为非终结符号，且first集不含ε
                        //则与右边非终结符号的first集求并集
                        else if (!firstSet.get(temp).contains("ε")) {
                            followSet.get(target).addAll(firstSet.get(temp));
                            break;
                        }
                        //右边为非终结符号，且first集含ε
                        //则与右边非终结符号的first集和follow集求并集
                        else {
                            followSet.get(target).addAll(firstSet.get(temp));
                            followSet.get(target).addAll(followSet.get(temp));
                            followSet.get(target).remove("ε");
                        }
                    }
                }
            }
            if (node.getLeft().equals("<begin>"))
                followSet.get(target).add("$");
        }
    }



    @Override
    public HashMap<String, HashSet<String>> getAllFirst() {
        return firstSet;
    }

    @Override
    public HashMap<String, HashSet<String>> getAllFollow() {
        return followSet;
    }
    @Override
    public Family generateFamily(ArrayList<Node> grammr){
        return null;
    }

    @Override
    public SLRTable generateSLRTable(Family family) {
        return null;
    }
}
