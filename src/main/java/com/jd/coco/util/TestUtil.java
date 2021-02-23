package main.java.com.jd.coco.util;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.LineNumberReader;


public class TestUtil extends AnAction{

        //private final MyToolWin myToolWin = new MyToolWin();

        //root node
        private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("检查结果");
        //行node
        private final DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode("行数超过80");
        //注释node
        private final DefaultMutableTreeNode commentNode = new DefaultMutableTreeNode("注释不规范");
        //if node
        private final DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("if语句不规范");

        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 流程列表
         * @param  anActionEvent:目录ID
         * @return
         */
        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            rootNode.removeAllChildren();
            lineNode.removeAllChildren();
            commentNode.removeAllChildren();
            ifNode.removeAllChildren();
            // 获取当前的project对象
            Project project = anActionEvent.getProject();
            // 获取当前文件对象
            Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
            String fileName = psiFile.getName();
            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(fileName);
            // 遍历当前对象的所有属性
            for (PsiElement psiElement : psiFile.getChildren()) {
                System.out.println(psiElement);

                if (psiElement instanceof PsiClass){
                    PsiClass psiClass = (PsiClass) psiElement;

                    // 获取注释
                    checkClassComment(document,psiClass);
                    // 方法列表
                    PsiMethod[] methods = psiClass.getMethods();
                    for (PsiMethod psiMethod : methods) {
                        // 获取备注
                        checkMethodComment(document,psiMethod);
                        // 获取大括号里的内容
                        PsiCodeBlock psiCodeBlock = psiMethod.getBody();
                        String codeText = psiCodeBlock.getText();
                        long lineCount = getLineNumberByIo(codeText);
                        System.out.println("行数:" + lineCount);
                        // 行数大于80了,发出警告
                        if (lineCount > 80){
                            // 行号
                            int lineNumbers = document.getLineNumber(psiMethod.getTextOffset());
                            String text = psiMethod.getName()+"方法超过80行(line "+(lineNumbers+1)+")";
                            DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                            lineNode.add(tmpTreeNode);
                        }
                        // 检查if语句是否合格
                        checkIfStatement(document,psiCodeBlock);
                    }
                }
            }
            // 显示输出内容
            rootNode.add(lineNode);
            rootNode.add(commentNode);
            rootNode.add(ifNode);
            fileNode.add(rootNode);
            //myToolWin.addLineNode(fileNode);
           // myToolWin.showToolWin(project);

        }

        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 检查If语句规则
         * @param  psiElement:元素
         * @return
         */
        private void checkIfStatement(Document document,PsiElement psiElement){
            PsiElement[] psiElements =  psiElement.getChildren();
            for (PsiElement element : psiElements){
                // 如果是if语句
                if (element instanceof PsiIfStatement){
                    PsiIfStatement psiIfStatement = (PsiIfStatement)element;
                    PsiStatement thenSt = psiIfStatement.getThenBranch();
                    String thenText = thenSt.getText();
                    // 没有大括号
                    if (!thenText.startsWith("{")){
                        int lineNumbers = document.getLineNumber(thenSt.getTextOffset());
                        String text = "if后面没有使用大括号(line "+(lineNumbers)+")";
                        DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                        ifNode.add(tmpTreeNode);
                    }
                    // else语句
                    PsiStatement elseSt = psiIfStatement.getElseBranch();
                    if (elseSt != null){
                        // else语句
                        if (elseSt instanceof PsiExpressionStatement){
                            if (!elseSt.getText().startsWith("{")){
                                int lineNumbers = document.getLineNumber(elseSt.getTextOffset());
                                String text = "else 后面没有使用大括号(line "+(lineNumbers)+")";
                                DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                                ifNode.add(tmpTreeNode);
                            }
                        }
                        // else if语句
                        if (elseSt instanceof PsiIfStatement){
                            PsiIfStatement elseIfSt = (PsiIfStatement)elseSt;
                            thenSt = elseIfSt.getThenBranch();
                            thenText = thenSt.getText();
                            // 没有大括号
                            if (!thenText.startsWith("{")){
                                int lineNumbers = document.getLineNumber(thenSt.getTextOffset());
                                String text = "if后面没有使用大括号(line "+(lineNumbers)+")";
                                DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                                ifNode.add(tmpTreeNode);
                            }
                        }
                        // 递归else内容
                        checkIfStatement(document,elseSt);
                    }

                    // 递归if语句内容
                    checkIfStatement(document,thenSt);
                }
            }
        }

        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 检查类的注释规则
         * @param  psiClass:元素
         * @return
         */
        private void checkClassComment(Document document, PsiClass psiClass){
            PsiComment classComment = null;
            for (PsiElement tmpEle : psiClass.getChildren()) {
                if (tmpEle instanceof PsiComment){
                    classComment = (PsiComment) tmpEle;
                    int lineNumbers = document.getLineNumber(classComment.getTextOffset());
                    // 注释的内容
                    String tmpText = classComment.getText();
                    if (tmpText.indexOf("* @author") < 0){
                        // 没有找到作者
                        String text = psiClass.getName()+"类没有找到作者(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                    if (tmpText.indexOf("* @date") < 0 &&tmpText.indexOf("* @time") < 0){
                        // 没有找到日期
                        String text = psiClass.getName()+"类没有找到日期(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                    if (tmpText.indexOf("* @des") < 0 &&tmpText.indexOf("* @describe") < 0){
                        // 没有找到描述
                        String text = psiClass.getName()+"类没有找到描述(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                }
            }
            if (classComment == null){
                // 没有注释
                int lineNumbers = document.getLineNumber(psiClass.getTextOffset());
                String text = psiClass.getName()+"类没有注释(line "+(lineNumbers+1)+")";
                DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                commentNode.add(tmpTreeNode);
            }
        }

        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 检查方法的注释规则
         * @param  psiMethod:元素
         * @return
         */
        private void checkMethodComment(Document document, PsiMethod psiMethod){
            PsiComment classComment = null;
            for (PsiElement tmpEle : psiMethod.getChildren()) {
                if (tmpEle instanceof PsiComment){
                    classComment = (PsiComment) tmpEle;
                    int lineNumbers = document.getLineNumber(classComment.getTextOffset());
                    // 注释的内容
                    String tmpText = classComment.getText();
                    if (tmpText.indexOf("* @author") < 0){
                        // 没有找到作者
                        String text = psiMethod.getName()+"方法没有找到作者(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                    if (tmpText.indexOf("* @date") < 0 &&tmpText.indexOf("* @time") < 0){
                        // 没有找到日期
                        String text = psiMethod.getName()+"方法没有找到日期(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                    if (tmpText.indexOf("* @des") < 0 &&tmpText.indexOf("* @describe") < 0){
                        // 没有找到描述
                        String text = psiMethod.getName()+"方法没有找到描述(line "+(lineNumbers+1)+")";
                        addCommentNode(text);
                    }
                }
            }
            if (classComment == null){
                // 没有注释
                int lineNumbers = document.getLineNumber(psiMethod.getTextOffset());
                String text = psiMethod.getName()+"方法没有注释(line "+(lineNumbers+1)+")";
                DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
                commentNode.add(tmpTreeNode);
            }
        }
        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 添加注释node
         * @param  text:标题
         * @return
         */
        private void addCommentNode(String text){
            DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(text);
            commentNode.add(tmpTreeNode);
        }

        /**
         * @author gaojindan
         * @date 2019/3/11 0011 17:08
         * @des 流程列表
         * @param  str:目录ID
         * @return 行数
         */
        public long getLineNumberByIo(String str){
            LineNumberReader lnr = new LineNumberReader(new CharArrayReader(str.toCharArray()));
            try {
                lnr.skip(Long.MAX_VALUE);
                lnr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lnr.getLineNumber() + 1;
        }

    }
