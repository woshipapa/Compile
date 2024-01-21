package org.example.Parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.Token.Token_Type;
@Data
@EqualsAndHashCode
public class TreeNode {
    //运算符种类
    private Token_Type OpCode;

    //二元运算,左右孩子作为算子
    private CaseOperator caseOperator;

    //函数调用，就是一个孩子
    private CaseFunc caseFunc;


    private Double caseConst;

    private CaseParamPtr caseParamPtr;

}
