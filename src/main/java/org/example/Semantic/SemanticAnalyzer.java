package org.example.Semantic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.Draw.DrawPanel;
import org.example.Parser.ParamPtrManager;
import org.example.Parser.TreeNode;
import org.example.utils.ExpUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;

@Data
@EqualsAndHashCode
@Component
public class SemanticAnalyzer {


    private Double origin_x = 0.0;
    private Double origin_y = 0.0;

    private Double scale_x = 1.0;

    private Double scale_y = 1.0;

    private Double rot_ang = 0.0;


    @Resource
    private ParamPtrManager paramPtrManager;

    @Resource
    private DrawPanel drawPanel;
    private void CalcCord(TreeNode x,TreeNode y,Point p){
        Double xVal = ExpUtils.getExpValue(x);
        Double yVal = ExpUtils.getExpValue(y);
        xVal *= scale_x;
        yVal *= scale_y;
        Double temp = xVal*Math.cos(rot_ang)+yVal*Math.sin(rot_ang);
        yVal = yVal*Math.cos(rot_ang) - xVal*Math.sin(rot_ang);
        xVal = temp;
        xVal += origin_x;
        yVal += origin_y;

        p.setLocation(xVal,yVal);

    }


    public void loopDraw(Double start, Double end, Double step, TreeNode x,TreeNode y ){

        for(;start <= end;start+=step){
            //这里T的节点的值已经修改
            paramPtrManager.updateParamValue(start);
            Point point = new Point();
            CalcCord(x,y,point);
            drawPanel.addPoint(point);
        }

    }

    public void setColor(Color color){
        this.drawPanel.setPointColor(color);
    }
}
