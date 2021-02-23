package main.java.com.jd.coco.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;
import main.java.com.jd.coco.listener.SubmitActionListener;
import main.java.com.jd.coco.mask.InfiniteProgressPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;

public class DocGenerateAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        super.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {


        //获取当前鼠标选定的目录或者元素
        Navigatable nav = e.getData(CommonDataKeys.NAVIGATABLE);
        String dirPath;
        if(nav != null){
            //PsiDirectory:
            //TODO 这里需要考虑选择单个文件导出的情况
            String navString = nav.toString();
            //dirPath = navString.split("\\src\main\\java");
            dirPath = navString.substring(13,navString.length());
        }else{
            Messages.showWarningDialog("请先选定要导出的目录","警告");
            return;
        }
        //Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
        // 创建 JFrame 实例
        JFrame frame = new JFrame("CocoDoc文档生成工具");
        // Setting the width and height of frame
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);//Frame居中显示
        //设置glasspane透明遮罩
        InfiniteProgressPanel glasspane = new InfiniteProgressPanel();;
        //openMask(glasspane);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        glasspane.setBounds(250, 150, (dimension.width) / 6, (dimension.height) / 6);
        frame.setGlassPane(glasspane);
        glasspane.start();//开始动画加载效果
        glasspane.setVisible(false);
        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel, dirPath,e,frame);

        // 设置界面可见
        frame.setVisible(true);


    }

    private static void placeComponents(JPanel panel, String dirPath, AnActionEvent e,JFrame frame) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        JLabel userLabel = new JLabel("选定路径:");
        userLabel.setBounds(10,20,100,50);
        panel.add(userLabel);

        JTextField userText = new JTextField(null, dirPath, 20);
        userText.setBounds(100,20,350,50);
        userText.setEditable(false);
        panel.add(userText);

        // 下拉框的label
        JLabel passwordLabel = new JLabel("输出目录:");
        passwordLabel.setBounds(10,100,100,50);
        panel.add(passwordLabel);

        FileSystemView fsv = FileSystemView.getFileSystemView(); //注意了，这里重要的一句
        String homeDir = fsv.getHomeDirectory().getAbsolutePath();

        JTextField outDirText = new JTextField(null, homeDir, 20);
        outDirText.setBounds(100,100,350,50);
        outDirText.setEditable(false);
        panel.add(outDirText);


        // 创建登录按钮
        JButton loginButton = new JButton("生成文档");
        loginButton.setBounds(30, 180, 400, 60);
        loginButton.addActionListener(new SubmitActionListener(userText, outDirText, e,panel, frame));//添加监听
        panel.add(loginButton);
    }
}
