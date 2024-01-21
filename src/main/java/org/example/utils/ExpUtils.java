package org.example.utils;

import org.example.Parser.TreeNode;

public class ExpUtils {

    public static Double getExpValue(TreeNode root){
        if(root == null) return 0.0;
        switch ( root.getOpCode() ){
            case CONST_ID:{
                //叶子节点常数值
                return root.getCaseConst();
            }
            case T:{
                //叶子节点T，返回引用的值
                return root.getCaseParamPtr().getValue();
            }
            case PLUS: {
                return getExpValue(root.getCaseOperator().getLeft()) + getExpValue(root.getCaseOperator().getRight());
            }
            case MINUS:{
                return getExpValue(root.getCaseOperator().getLeft()) - getExpValue(root.getCaseOperator().getRight());
            }
            case MUL:{
                return getExpValue(root.getCaseOperator().getLeft()) * getExpValue(root.getCaseOperator().getRight());
            }
            case DIV:{
                return getExpValue(root.getCaseOperator().getLeft())/getExpValue(root.getCaseOperator().getRight());
            }
            case POWER:{
                return Math.pow( getExpValue(root.getCaseOperator().getLeft()), getExpValue(root.getCaseOperator().getRight()));
            }
            case FUNC:{
                return root.getCaseFunc().getFunc().execute(getExpValue( root.getCaseFunc().getChild() ) );
            }
            default:{
                return 0.0;
            }
        }



    }
}
