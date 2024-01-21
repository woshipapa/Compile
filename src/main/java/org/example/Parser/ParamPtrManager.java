package org.example.Parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode
public class ParamPtrManager {
    private CaseParamPtr paramPtr = new CaseParamPtr(0.0);


    public void updateParamValue(Double val){
        this.paramPtr.setValue(val);
    }
}
