package org.example.Token;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
@Data
@EqualsAndHashCode
public class TokenRegex {

    public static final Map<String,Token_Type> regexMap = new HashMap<>();



    //空白字符
    private static String whiteSpace = "(' '|\t|\n|\r|\f)+";

    public static String getIdregex() {
        return Idregex;
    }

    public static String getConstIdRegex(){
        return constIdRegex;
    }

    public static String getWhiteSpace() {
        return whiteSpace;
    }
    private static String letter = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)";

    private static String digit = "(0|1|2|3|4|5|6|7|8|9)";

    //ID的正规式，以至少一个字母开头，后面是数字或者字母的任意组合
    private static String Idregex = letter+"+"+"("+letter+"|"+digit+")*";

    //匹配数值型，包括整数和小数
    private static String constIdRegex = digit+"+(#|."+digit+"*)";
    public static Token_Type getType(String regex){
        return regexMap.get(regex);
    }
    static {
        regexMap.put(Idregex,Token_Type.ID);
        regexMap.put("//|--",Token_Type.COMMENT);
        regexMap.put(";",Token_Type.SEMICO);
        regexMap.put("\\(",Token_Type.L_BRACKET);
        regexMap.put("\\)",Token_Type.R_BRACKET);
        regexMap.put(",",Token_Type.COMMA);
        regexMap.put("\\+",Token_Type.PLUS);
        regexMap.put("-",Token_Type.MINUS);
        regexMap.put("/",Token_Type.DIV);
        regexMap.put("\\*",Token_Type.MUL);
        regexMap.put("\\*\\*",Token_Type.POWER);
        regexMap.put(constIdRegex,Token_Type.CONST_ID);
        regexMap.put(whiteSpace,Token_Type.WHITE_SPACE);
        regexMap.put("=",Token_Type.EQUALS);
    }

}
