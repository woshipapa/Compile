package org.example.Token;

public enum Token_Type {
    ID,// 标识符
    COMMENT,// 注释
    ORIGIN,SCALE,ROT,IS,TO,STEP,DRAW,FOR,FROM,// 关键字
    T,//参数
    SEMICO(";"),L_BRACKET("("),R_BRACKET(")"),COMMA(","),//分隔符
    PLUS("+"),MINUS("-"),MUL("*"),DIV("/"),POWER("**"),//运算符
    FUNC,//函数
    CONST_ID,//常数
    NONTOKEN,//空
    ERRTOKEN,//错误记号

    WHITE_SPACE,//空白字符
    COLOR, RED, BLACK,BLUE,//画图颜色
    LET,EQUALS("=");


    private String signal;

    Token_Type(String signal){
        this.signal = signal;
    }

    Token_Type(){}

    public String getSignal(){
        return this.signal;
    }
}

