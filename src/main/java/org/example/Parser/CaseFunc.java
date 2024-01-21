package org.example.Parser;

import lombok.Data;
import org.example.Token.Func;

@Data
public class CaseFunc{
    private TreeNode child;

    private Func func;
}