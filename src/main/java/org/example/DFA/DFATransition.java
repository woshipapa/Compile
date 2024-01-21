package org.example.DFA;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class DFATransition implements Serializable {

    DFAState start;

    DFAState end;

    Character trans;

}
