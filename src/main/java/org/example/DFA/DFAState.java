package org.example.DFA;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.NFA.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Data
@EqualsAndHashCode
public class DFAState implements Serializable {

    private Set<Node> nfaNodes = new HashSet<>();

    private String stateName;
}
