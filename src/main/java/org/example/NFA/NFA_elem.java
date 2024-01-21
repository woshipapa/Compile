package org.example.NFA;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@Data
@EqualsAndHashCode
public class NFA_elem {

    private String regex;
    private Integer edgeCount = 0;

    private List<Edge> edgeList = new ArrayList<>();

    private Node start;
    private Node end;

    private String filePath;

    public void insertEdgeBatch(List<Edge> edges){
        this.edgeList.addAll(edges);
        this.edgeCount += edges.size();
    }

}




