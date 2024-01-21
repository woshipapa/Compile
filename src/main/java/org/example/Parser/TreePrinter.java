package org.example.Parser;

import java.util.Collections;

public class TreePrinter {

    public static void print(TreeNode root , int level){
        if(root == null) return ;
        //用缩进的格数表示树的深度
        String indent = String.join("", Collections.nCopies(level," "));

        if(root.getCaseOperator()!=null){
            //说明是二元运算符
            System.out.println(indent + root.getOpCode().getSignal());
            print(root.getCaseOperator().getLeft(), level+1);
            print(root.getCaseOperator().getRight(), level+1);
        }else if(root.getCaseFunc()!=null){
            //是一个函数调用节点
            System.out.println(indent + root.getCaseFunc().getFunc());
            print(root.getCaseFunc().getChild(), level+1);
        }else if(root.getCaseParamPtr()!=null){
            System.out.println(indent + root.getCaseParamPtr().getValue());
        }else {
            //常数
            System.out.println(indent + root.getCaseConst());
        }
        if(level == 1){
            System.out.println("-------------------------------");
        }
    }

}
