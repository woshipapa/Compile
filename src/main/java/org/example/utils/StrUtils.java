package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
@Slf4j
public class StrUtils {

    public static String dotCommand = "E:\\Study\\complier\\Graphviz\\bin\\dot.exe";
//    public static String convertPath(String dot){
//        int lastIndex = dot.lastIndexOf("\\");
//        String basePath = dot.substring(0,lastIndex);
//        String simpleName = dot.substring(lastIndex+1,dot.length());
//        String outputPath = basePath+"\\images";
//        File f = new File(outputPath);
//        if(!f.exists()) f.mkdirs();
//        log.info("存储png图片的文件夹路径: {}",outputPath);
//        log.info("这个文件的名字为: {}",simpleName);
//        String outputPngFilePath = outputPath+File.separator+simpleName.replace(".dot",".png");
//        log.info("这个文件的完整路径名为: {}",outputPngFilePath);
//        return outputPngFilePath;
//    }
}
