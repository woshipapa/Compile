package org.example.Parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class VariableStorage {
    private Map<String, Double> variables = new HashMap<>();

    public void setVariable(String name, Double value) {
        variables.put(name, value);
    }

    public Double getVariable(String name) {
        return variables.getOrDefault(name, null);
    }


}

