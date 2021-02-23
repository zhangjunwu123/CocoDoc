package main.java.com.jd.coco.helper;

import javax.swing.*;
import java.awt.*;

/*
* 窗口展示帮助类
*
* */
public class DisplayHelper {

    //开启遮罩
    public static void openMask(JFrame frame) {
        frame.setEnabled(false);
        frame.setBackground(Color.GRAY);
        // Setting the width and height of frame
        //开始显示遮罩
        frame.getGlassPane().setVisible(true);
    }
}
