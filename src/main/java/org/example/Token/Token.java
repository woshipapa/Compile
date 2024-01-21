package org.example.Token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    //记号类型
    private Token_Type type;

    //原始输入的字符串
    private String lexeme;

    //常数的话，这里存放常数的值
    private double value;

    private Func function;
}
