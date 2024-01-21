package org.example.DFA;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Data
@EqualsAndHashCode
@Slf4j
public class DFA implements Serializable {

    private DFAState start;

    private List<DFAState> endStates = new ArrayList<>();

    private List<DFAState> states = new ArrayList<>();


    private List<DFATransition> transitions = new ArrayList<>();

    private Integer edgeCount;

    private String filePath;

    public byte[] toByteArray(){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            // 将 DFA 对象写入 ObjectOutputStream，它会将对象序列化为字节数组
            objectOutputStream.writeObject(this);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // 自定义方法，从字节数组反序列化为 DFA 对象
    public static DFA fromByteArray(byte[] byteArray) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            // 从 ObjectInputStream 中读取字节数组并反序列化为 DFA 对象
            return (DFA) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean verify(String s){
        DFAState currState = this.getStart();
        boolean flag = false;
        for(int i =0;i<s.length();i++){
            char c = s.charAt(i);
            currState = dfa_move(currState,c);
            if(currState == null) {
                flag=true;
                break;
            }
        }
        if(currState != null) {
            if (!this.getEndStates().contains(currState)) {
                flag = true;
            }
        }
//        log.info("s : {} 的检验结果为 {}",s,!flag);
        return !flag;
    }
    private DFAState dfa_move(DFAState curr,char c){
        List<DFATransition> transitions = this.getTransitions();
        for(DFATransition t : transitions){
            if(t.getStart().getStateName().equals(curr.getStateName()) && t.getTrans().equals(c)){
                return t.getEnd();
            }
        }
        return null;
    }
}
