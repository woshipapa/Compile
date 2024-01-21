package org.example.NFA;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Edge{

    Node startNode;
    Node endNode;
    Character c;

}