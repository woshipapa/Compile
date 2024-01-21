package org.example.Lexer;

import lombok.extern.slf4j.Slf4j;
import org.example.DFA.DFA;
import org.example.Token.Token;
import org.example.Token.Token_Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;

@Component
@Slf4j
@Scope("singleton")
@PropertySource("classpath:application.properties")
public class Scanner {

    private DFA WhiteSpaceDFA;
    @Resource
    private Lexer lexer;


    @Value("${filepath}")
    private String filePath;


    private StringBuilder sb;

    private RandomAccessFile reader;
    //记录文件读到哪里
    private Long pos = 0l;
    private Integer lineNum = 0;

    public Integer getLineNum() {
        return lineNum;
    }

    @PostConstruct
    public void init() throws IOException {
        WhiteSpaceDFA = lexer.getWhiteSpaceDFA();
        sb = new StringBuilder();
        reader = new RandomAccessFile(filePath,"rw");
        reader.seek(pos);
    }

    private boolean isWhiteSpace(Character c){
        return WhiteSpaceDFA.verify(String.valueOf(c));
    }


    public Token scan(){
            int i ;
            Token pre = new Token(),curr;
        while ((i = readChar()) != -1) {
            char c = (char) i;
            sb.append(c);
            curr = lexer.match(sb.toString());
            //最大化的匹配
            while (curr != null) {
                pre = curr;
                i = readChar();
                c = (char) i;
                sb.append(c);
                curr = lexer.match(sb.toString());
            }
            if (i != -1)
            {
                backChar();
            }
            if(pre != null && pre.getType().equals(Token_Type.WHITE_SPACE) && pre.getLexeme().contains("\n")){
                pre.getLexeme().replace("\r\n","\n");
                for(int k = 0;k<pre.getLexeme().length();k++){
                    if(pre.getLexeme().charAt(k) == '\n'){
                        lineNum++;
                    }
                }

            }else if(pre!=null && pre.getType().equals(Token_Type.COMMENT)){
                //读到了注释
                skipComment();
                //这里词法分析器不能把注释的记号流提供给语法分析器，所以这里就提供了一个空白符号
                pre.setType(Token_Type.WHITE_SPACE);
                pre.setLexeme("\n");
            }
            if(pre == null) {
                pre.setType(Token_Type.ERRTOKEN);
                pre.setLexeme(sb.toString());
            }
            sb.setLength(0);
            return pre;
        }
        pre.setType(Token_Type.NONTOKEN);
        return pre;
    }

    private void skipComment(){
        try {
            reader.readLine();//将注释的这一行内容吞掉
            pos = reader.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int readChar(){
        try {
            reader.seek(pos);
            int i  = reader.read();
            pos++;
            return i;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void backChar(){
        pos--;
    }
}
