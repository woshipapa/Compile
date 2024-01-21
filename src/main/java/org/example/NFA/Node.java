package org.example.NFA;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class Node implements Serializable {

    private String nodeName;

}