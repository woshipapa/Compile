package org.example.Parser;

import lombok.extern.slf4j.Slf4j;
import org.example.Lexer.Scanner;
import org.example.Semantic.SemanticAnalyzer;
import org.example.Token.Func;
import org.example.Token.Token;
import org.example.Token.Token_Type;
import org.example.utils.ExpUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;

@Component
@Slf4j
public class Parser {

    //全局的token，用于读取下一个记号
    private Token token;

    @Resource
    private Scanner scanner;


    @Resource
    private SemanticAnalyzer semanticAnalyzer;

    @Resource
    private VariableStorage variableStorage;

    private TreeNode x;

    private TreeNode y;

    private TreeNode from;

    private TreeNode to;

    private TreeNode step;

    private TreeNode angle;

//    private CaseParamPtr paramPtr = new CaseParamPtr(0.0);

    @Resource
    private ParamPtrManager paramPtrManager;
    public void FetchToken(){
        //跳过空白字符
        while((token = scanner.scan()).getType() == Token_Type.WHITE_SPACE){

        };
        if(token.getType().equals(Token_Type.ERRTOKEN))
        {
            SyntaxError(1);
        }
    }

    public void MatchToken(Token_Type type){
        if(token.getType() != type){
            SyntaxError(2);
        }
        else{
            Match(type.toString());
        }
        FetchToken();
    }



    public void Parse(){
        FetchToken();
        Program();
    }



    public void SyntaxError(int case_value)   //打印错误信息
    {
        switch (case_value)
        {
            case 1:
                System.err.println("Line: "+scanner.getLineNum()+" "+"非法记号"+" "+token.getLexeme());
                break;
            case 2:
                System.err.println("Line: "+scanner.getLineNum()+" "+token.getLexeme()+" "+"不是预期记号");
                break;
            case 3:
                System.err.println("Line: "+scanner.getLineNum()+" "+token.getLexeme()+" "+"不存在该类型的语句");
                break;
            case 4:
                System.err.println("Line: "+scanner.getLineNum()+" "+token.getLexeme()+" "+"是不允许定义的变量名字");
                break;
            case 5:
                System.err.println("Line: "+scanner.getLineNum()+" "+token.getLexeme()+" "+"是没有定义过的变量名");
        }
    }


    public void Enter(String s)
    {
        System.out.println("Enter in  " + s);
    }


    public void Exit(String s)
    {
        System.out.println("Exit from  " + s);
    }


    public void Match(String s)
    {
        System.out.println("MatchToken  " + s);
    }

    private void Program(){
        Enter("Program");
        while(!token.getType().equals(Token_Type.NONTOKEN)){
            Statement();
            MatchToken(Token_Type.SEMICO);
        }
        Exit("Program");
    }


    private void Statement(){
        Enter("Statement");
        switch (token.getType()){
            case ORIGIN:{
                OriginStatement();
            };break;
            case SCALE:{
                ScaleStatement();
            };break;
            case ROT: {
                RotStatement();
            };break;
            case FOR:{
                ForStatement();
            };break;
            case COLOR:{
                ColorStatement();
            };break;
            case LET:{
                LetStatement();
            };break;
            default:{
                SyntaxError(3);
                break;
            }
        }
        Exit("Statement");
    }

    private void LetStatement(){
        MatchToken(Token_Type.LET);
        String name = Id();
        MatchToken(Token_Type.ID);
        MatchToken(Token_Type.EQUALS);
        TreeNode e = Expression();
        Double value = ExpUtils.getExpValue(e);
        variableStorage.setVariable(name,value);
    }

    private String Id(){
        if(!token.getType().equals(Token_Type.ID)){
            //说明与其他关键字重合了，报错
            SyntaxError(4);
            return "";
        }
        String name = token.getLexeme();
        return name;
    }

    private void ColorStatement(){
        MatchToken(Token_Type.COLOR);
        MatchToken(Token_Type.IS);
        Color color =  ColorName();
        semanticAnalyzer.setColor(color);
    }

    private Color ColorName(){
        Token_Type type = token.getType();
        Color res = null;
        switch (type){
            case BLACK:{
                res = Color.BLACK;
                MatchToken(Token_Type.BLACK);
            };break;
            case BLUE:{
                res = Color.BLUE;
                MatchToken(Token_Type.BLUE);
            };break;
            case RED:{
                res = Color.RED;
                MatchToken(Token_Type.RED);
            };break;
            default:{
                SyntaxError(2);
                MatchToken(type);//消化掉这个未知的颜色
                //按照默认的颜色来
                break;
            }
        }
        return res;
    }

    private void OriginStatement(){
        Enter("OriginStatement");
        MatchToken(Token_Type.ORIGIN);
        MatchToken(Token_Type.IS);
        MatchToken(Token_Type.L_BRACKET);
        x = Expression();
        Double xVal = ExpUtils.getExpValue(x);
        semanticAnalyzer.setOrigin_x(xVal);
        MatchToken(Token_Type.COMMA);
        y = Expression();
        Double yVal = ExpUtils.getExpValue(y);
        semanticAnalyzer.setOrigin_y(yVal);
        MatchToken(Token_Type.R_BRACKET);
        Exit("OriginStatement");
    }

    private void RotStatement(){
        Enter("RotStatement");
        MatchToken(Token_Type.ROT);
        MatchToken(Token_Type.IS);
        angle = Expression();
        Double angleValue = ExpUtils.getExpValue(angle);
        semanticAnalyzer.setRot_ang(angleValue);
        Exit("RotStatement");
    }

    private void ScaleStatement(){
        Enter("ScaleStatement");
        MatchToken(Token_Type.SCALE);
        MatchToken(Token_Type.IS);
        MatchToken(Token_Type.L_BRACKET);
        x = Expression();
        Double xVal = ExpUtils.getExpValue(x);
        semanticAnalyzer.setScale_x(xVal);
        MatchToken(Token_Type.COMMA);
        y = Expression();
        Double yVal = ExpUtils.getExpValue(y);
        semanticAnalyzer.setScale_y(yVal);
        MatchToken(Token_Type.R_BRACKET);
        Exit("ScaleStatement");
    }

    private void ForStatement(){
        Double start,end,stp;

        Enter("ForStatement");
        MatchToken(Token_Type.FOR);
        MatchToken(Token_Type.T);
        MatchToken(Token_Type.FROM);
        from = Expression();
        start = ExpUtils.getExpValue(from);
        MatchToken(Token_Type.TO);
        to = Expression();
        end = ExpUtils.getExpValue(to);
        MatchToken(Token_Type.STEP);
        step = Expression();
        stp = ExpUtils.getExpValue(step);
        MatchToken(Token_Type.DRAW);
        MatchToken(Token_Type.L_BRACKET);
        x = Expression();
        MatchToken(Token_Type.COMMA);
        y = Expression();
        MatchToken(Token_Type.R_BRACKET);
        semanticAnalyzer.loopDraw(start,end,stp,x,y);
        Exit("ForStatement");
    }

    public TreeNode Expression(){
        Enter("Expression");
        TreeNode left,right;
        left = Term();
        Token_Type type;
        while(token.getType().equals(Token_Type.PLUS) || token.getType().equals(Token_Type.MINUS)){
            type = token.getType();
            MatchToken(type);
            right = Term();
            left = MakeTreeNode(type,left,right);
        }
        TreePrinter.print(left,1);
        Exit("Expression");
        return left;

    }


    private TreeNode Term(){
        Enter("Term");
        TreeNode left,right;
        left = Factor();
//        Token_Type preType = token.getType();//这里埋了个雷，如果这里是乘法或者除法，preType不会再改变了，会一直进行
        Token_Type preType;
        //所以这里依然用最新的token中的type
        while(token.getType().equals(Token_Type.MUL) || token.getType().equals(Token_Type.DIV)){
            preType = token.getType();
            MatchToken(preType);
            right = Factor();
            left = MakeTreeNode(preType,left,right);
        }
        Exit("Term");
        return left;
    }


    private TreeNode Factor(){
        Enter("Factor");
        TreeNode left,right;
        if(token.getType().equals(Token_Type.PLUS)){
            MatchToken(Token_Type.PLUS);
            right = Factor();
        }else if(token.getType().equals(Token_Type.MINUS)){
            MatchToken(Token_Type.MINUS);
            left = new TreeNode();
            left.setCaseConst(0.0);
            left.setOpCode(Token_Type.CONST_ID);
            right = MakeTreeNode(Token_Type.MINUS,left,Factor());
        }else{
            right = Component();
        }
        Exit("Factor");
        return right;
    }




    private TreeNode Component(){
        Enter("Component");
        TreeNode left = Atom(),right;
        if(token.getType().equals(Token_Type.POWER)){
            MatchToken(Token_Type.POWER);
            right = Component(); //通过递归来完成Component的右结合
            left = MakeTreeNode(Token_Type.POWER,left,right);
        }
        Exit("Component");
        return left;
    }



    /**
     * Atom → CONST_ID
     | T
     | FUNC L_BRACKET Expression R_BRACKET
     | L_BRACKET Expression R_BRACKET
     */
    private TreeNode Atom(){
        Enter("Atom");
        Token temp = token;
        Token_Type type = token.getType();
        TreeNode res = null;
        switch (type){
            case CONST_ID:{
                MatchToken(Token_Type.CONST_ID);
                res = MakeTreeNode(type,temp.getValue());
            };break;
            case T:{
                MatchToken(Token_Type.T);
                res = MakeTreeNode(type,temp.getValue());
            };break;
            case FUNC:{
                Func func = token.getFunction();
                MatchToken(Token_Type.FUNC);
                MatchToken(Token_Type.L_BRACKET);
                TreeNode exp = Expression();
                res = MakeTreeNode(type,func,exp);
                MatchToken(Token_Type.R_BRACKET);
            };break;
            case L_BRACKET:{
                MatchToken(Token_Type.L_BRACKET);
                res = Expression();
                MatchToken(Token_Type.R_BRACKET);
            };break;
            case ID:{
                //变量名字，将变量名换成他所表示的值
                Double value = variableStorage.getVariable(temp.getLexeme());
                if(value == null){
                    SyntaxError(5);
                    res = MakeTreeNode(Token_Type.CONST_ID,0.0);
                }else{
                    res = MakeTreeNode(Token_Type.CONST_ID,value);
                }
                MatchToken(Token_Type.ID);
            };break;
            default:{
                SyntaxError(2);
                break;
            }
        }
        Exit("Atom");
        return res;
    }


    private TreeNode MakeTreeNode(Token_Type type,Double value){
        TreeNode treeNode = new TreeNode();
        treeNode.setOpCode(type);

        if(type.equals(Token_Type.T)){
            //如果是T，就要引用这个全局变量
            treeNode.setCaseParamPtr(paramPtrManager.getParamPtr());
        }else if(type.equals(Token_Type.CONST_ID)){
            treeNode.setCaseConst(value);
        }
        return treeNode;
    }


    private TreeNode MakeTreeNode(Token_Type type,TreeNode left,TreeNode right){

        TreeNode t = new TreeNode();
        t.setOpCode(type);
        CaseOperator caseOperator = new CaseOperator();
        caseOperator.setLeft(left);
        caseOperator.setRight(right);
        t.setCaseOperator(caseOperator);
        return t;
    }

    private TreeNode MakeTreeNode(Token_Type type, Func func,TreeNode value){

        TreeNode t = new TreeNode();
        t.setOpCode(type);
        CaseFunc caseFunc = new CaseFunc();
        caseFunc.setFunc(func);
        caseFunc.setChild(value);

        t.setCaseFunc(caseFunc);
        return t;
    }
}
