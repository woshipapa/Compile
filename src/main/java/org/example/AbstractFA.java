package org.example;

import org.example.utils.StrUtils;

import java.io.IOException;

public abstract class AbstractFA<T> implements FADrawer<T> {

    public  String generatePng(String filePath){
        //"E:\Study\complier\regex_0.dot"
        String outputPngFile = this.convertPath(filePath);
        try{
            Process process = new ProcessBuilder(StrUtils.dotCommand,"-Tpng",filePath,"-o",outputPngFile)
                    .redirectErrorStream(true).start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Converted " + filePath + " to " + outputPngFile);
            } else {
                System.err.println("Error converting " + filePath);
            }
            return outputPngFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public abstract String convertPath(String dotPath);
}
