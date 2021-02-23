package main.java.com.jd.coco.helper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import main.java.com.jd.coco.exception.KnownException;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/*
* 公用方法类
* */
public class ActionHelper {



    //校验选中的目录不能为空
    public static void  checkBlankSelectedPath(JFrame frame, String originalPath, String outDirPath) {
        if (StringUtils.isBlank(originalPath) || StringUtils.isBlank(outDirPath)) {
            frame.dispose();
            throw new KnownException("选定目录和输出目录不能为空");
        }
    }

    //若选中的是方法，则返回当前选中的方法
    public static  PsiMethod getSelectedPsiMethod(AnActionEvent e, PsiFile psiFile) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiMethod currentPsiMethod = null;
        if(null != editor && null != editor.getSelectionModel()){
            PsiElement elementAt = psiFile.findElementAt(editor.getSelectionModel().getLeadSelectionOffset());
            PsiElement parent = elementAt.getParent();

            if(null != parent && parent instanceof PsiMethod){
                currentPsiMethod = (PsiMethod) parent;
            }
        }
        return currentPsiMethod;
    }

}
