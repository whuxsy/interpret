package whu.se.interpret.result;

import lombok.Getter;
import lombok.Setter;
import whu.se.interpret.po.Token;

import java.util.List;

//返回前端结果类
@Setter
@Getter
public class Result {

    //提交的程序是否正确 200为正确，400为错误
    private int status;

    //错误的原因
    private String wrongMessage;

    //词法分析单元返回的token序列的字符串形式
    private String tokens;

    //前端输出
    private String output;

    //判断debug有没有结束
    private boolean isFinished;

    //调试代码
    private String debug_code;


    //返回文件的内容
    private String code;

    //前端命令
    private String cmd;

    public Result(int status,String tokens,String wrongMessage){

        this.status = status;
        this.tokens = tokens;
        this.wrongMessage = wrongMessage;
    }

    public Result(String output){

        this.status = 200;
        this.output = output;
    }

    public Result(String output,String debug_code){

        this.status = 200;
        this.output = output;
        this.debug_code = debug_code;
    }

    public Result(int status){

        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getWrongMessage() {
        return wrongMessage;
    }

    public void setWrongMessage(String wrongMessage) {
        this.wrongMessage = wrongMessage;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public Result(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCmd() {return cmd; }
    
    public void setCmd(String cmd) { this.cmd = cmd; }
}
