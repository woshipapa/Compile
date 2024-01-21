package org.example.DFA;

import lombok.extern.slf4j.Slf4j;
import org.example.AbstractFA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
@Slf4j
public class DrawDFA extends AbstractFA<DFA> {

    public  String generateDot(DFA dfa,String nfaPngPath){
        String basePath = nfaPngPath.substring(0,nfaPngPath.lastIndexOf("\\images\\"));
        String simpleName = nfaPngPath.substring(nfaPngPath.lastIndexOf("\\")+1,nfaPngPath.lastIndexOf(".png"));
        String filePath = basePath+ File.separator+simpleName+"_dfa.dot";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("digraph NFA {\n");
            writer.write(" rankdir=LR;  //横向布局\n\n");
            writer.write(" node [shape = circle];    //状态节点\n\n");
            List<DFAState> ends = dfa.getEndStates();
            for(DFAState s : ends) {
                writer.write("  \"" + s.getStateName() + "\"   [shape=doublecircle];\n");
            }

            for( DFAState s  : dfa.getStates()){
                writer.write("  \""+s.getStateName()+"\"  [label=\"State  "+s.getStateName());
                if(s.getStateName().equals(dfa.getStart().getStateName())){
                    writer.write("\\n(startState)");
                }else if(dfa.getEndStates().stream().anyMatch(it->it.getStateName().equals(s.getStateName()))){
                    writer.write("\\n(endState)");
                }
                writer.write("\"];\n");
            }
            for (int i = 0; i < dfa.getEdgeCount(); i++) {
                DFATransition transition = dfa.getTransitions().get(i);
                writer.write("  \"" + transition.getStart().getStateName() + "\"  ->  \"" + transition.getEnd().getStateName() + "\"   [label= \"" + transition.getTrans() + "\"];\n");
            }
            writer.write("}\n");
            System.out.println("DFA DOT file generated successfully.");
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    @Override
    public String convertPath(String dot) {
        int lastIndex = dot.lastIndexOf("\\");
        String basePath = dot.substring(0,lastIndex);
        String simpleName = dot.substring(lastIndex+1);
        String outputPath = basePath+"\\images\\DFA";
        File f = new File(outputPath);
        if(!f.exists()) f.mkdirs();
        log.info("存储png图片的文件夹路径: {}",outputPath);
        log.info("这个文件的名字为: {}",simpleName);
        String outputPngFilePath = outputPath+File.separator+simpleName.replace(".dot",".png");
        log.info("这个文件的完整路径名为: {}",outputPngFilePath);
        return outputPngFilePath;
    }
}
