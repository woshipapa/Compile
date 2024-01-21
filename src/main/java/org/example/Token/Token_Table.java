package org.example.Token;

import java.util.HashMap;
import java.util.Map;

public class Token_Table {

    public static Token[] token_table = new Token[]
            {
                    new Token(Token_Type.CONST_ID, "PI", 3.1415926535, null),
                    new Token(Token_Type.CONST_ID, "E", 2.71828,null),
                    new Token(Token_Type.T, "T", 0.0,null),
                    new Token(Token_Type.ORIGIN, "ORIGIN", 0.0,null),
                    new Token(Token_Type.SCALE, "SCALE", 0.0,null),
                    new Token(Token_Type.ROT, "ROT", 0.0,null),
                    new Token(Token_Type.IS, "IS", 0.0,null),
                    new Token(Token_Type.FOR, "FOR", 0.0,null),
                    new Token(Token_Type.FROM, "FROM", 0.0,null),
                    new Token(Token_Type.TO, "TO", 0.0,null),
                    new Token(Token_Type.STEP, "STEP", 0.0,null),
                    new Token(Token_Type.DRAW, "DRAW", 0.0,null),
                    new Token(Token_Type.FUNC, "COS", 0.0,new Func_Cos()),
                    new Token(Token_Type.FUNC, "SIN", 0.0, new Func_Sin()),
                    new Token(Token_Type.FUNC, "LN", 0.0,new Func_Ln()),
                    new Token(Token_Type.FUNC, "EXP", 0.0, new Func_Exp()),
                    new Token(Token_Type.FUNC, "SQRT", 0.0, new Func_Sqrt()),
                    new Token(Token_Type.FUNC, "TAN", 0.0, new Func_Tan()),
                    new Token(Token_Type.COLOR, "COLOR", 0.0,null),
                    new Token(Token_Type.RED, "RED", 0.0,null),
                    new Token(Token_Type.BLACK, "BLACK", 0.0,null),
                    new Token(Token_Type.BLUE,"BLUE",0.0,null),
                    new Token(Token_Type.LET,"LET",0.0,null)
            };
    public static Map<String,Token> tokenMap = new HashMap<>();
    static {
        for(Token t : token_table){
            tokenMap.put(t.getLexeme(),t);
        }
    }
    public static Token getToken(String s){
        s = s.toUpperCase();
        return tokenMap.get(s);
    }
}
