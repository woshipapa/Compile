package org.example.Draw;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;

@Component
public class Draw extends JFrame {

    @Resource
    private DrawPanel drawPanel;

    private JTextField textField;
    @PostConstruct
    public void init(){
//        setLayout(null);
        drawPanel.setPreferredSize(new Dimension(1000,1000));
//        drawPanel.setBounds(0,0,1000,1000);
        add(drawPanel);
//        textField = new JTextField();
//        Double testFieldX = drawPanel.getMaxX() + 20;
//        Double testFieldY = drawPanel.getMaxY() + 20;
//        textField.setBounds(testFieldX.intValue(),testFieldY.intValue(),50,20);
//        add(textField);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setVisible(true);
    }
    public void display(){
        setVisible(true);
    }
}
