package org.example.NFA;

import lombok.extern.slf4j.Slf4j;
import org.example.AbstractFA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;
@Slf4j
public class DrawNFA extends AbstractFA<NFA_elem> {

    public  String  generateDot(NFA_elem nfa,String regex){
        Pattern pattern = Pattern.compile(regex);
        int regexHash = pattern.toString().hashCode();
        String base64 = Base64.getEncoder().encodeToString(String.valueOf(regexHash).getBytes(StandardCharsets.UTF_8));
        String filePath = "E:\\Study\\complier\\regex_"+base64+"_nfa.dot";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("digraph NFA {\n");
            writer.write(" rankdir=LR;  //横向布局\n\n");
            writer.write(" node [shape = circle];    //状态节点\n\n");
            writer.write(" "+nfa.getEnd().getNodeName()+" [shape=doublecircle];\n");

            writer.write(" "+nfa.getStart().getNodeName()+" [label=\"Start State: "+nfa.getStart().getNodeName()+"\"];\n");
            writer.write("  " + nfa.getEnd().getNodeName() + " [label=\"End State: " + nfa.getEnd().getNodeName() + "\"];\n");
            for (int i = 0; i < nfa.getEdgeCount(); i++) {
                Edge currentEdge = nfa.getEdgeList().get(i);
                writer.write("  " + currentEdge.getStartNode().getNodeName() + " -> " + currentEdge.getEndNode().getNodeName() + " [label=\"" + currentEdge.getC() + "\"];\n");
            }
            writer.write("}\n");
            System.out.println("NFA DOT file generated successfully.");
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public  String convertPath(String dot){
        int lastIndex = dot.lastIndexOf("\\");
        String basePath = dot.substring(0,lastIndex);
        String simpleName = dot.substring(lastIndex+1);
        String outputPath = basePath+"\\images\\NFA";
        File f = new File(outputPath);
        if(!f.exists()) f.mkdirs();
        log.info("存储png图片的文件夹路径: {}",outputPath);
        log.info("这个文件的名字为: {}",simpleName);
        String outputPngFilePath = outputPath+File.separator+simpleName.replace(".dot",".png");
        log.info("这个文件的完整路径名为: {}",outputPngFilePath);
        return outputPngFilePath;
    }
}
