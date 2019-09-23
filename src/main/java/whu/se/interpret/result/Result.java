package whu.se.interpret.result;

import whu.se.interpret.po.token;

import java.util.List;

public class Result {

    //提交的程序是否正确
    private boolean is_success;

    //错误的原因
    private String wrongMessage;

    //词法分析单元返回的token序列
    private List<token> tokens;

    public Result(boolean is_success){
        this.is_success = is_success;
    }
}