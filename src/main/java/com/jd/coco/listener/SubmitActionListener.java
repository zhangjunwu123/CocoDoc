package main.java.com.jd.coco.listener;

import com.alibaba.fastjson.JSONObject;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import main.java.com.jd.coco.entity.*;
import main.java.com.jd.coco.exception.KnownException;
import main.java.com.jd.coco.helper.ActionHelper;
import main.java.com.jd.coco.helper.DisplayHelper;
import main.java.com.jd.coco.mask.InfiniteProgressPanel;
import main.java.com.jd.coco.util.CustomClassUtil;
import main.java.com.jd.coco.util.FreeMarkerUtil;
import main.java.com.jd.coco.util.MappingWayResult;
import main.java.com.jd.coco.util.ToJsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import static main.java.com.jd.coco.constant.BasicTypes.BASIC_TYPE_LIST;
import static main.java.com.jd.coco.constant.ExcludeClass.EXCLUDE_CLASS_LIST;
import static main.java.com.jd.coco.constant.ExcludeFields.EXCLUDE_FIELD_LIST;

/*
* 生成按钮的监听
* */
public class SubmitActionListener extends JFrame implements ActionListener {

    private JTextField jTextField;
    private JTextField outDirTextField;
    private AnActionEvent anActionEvent;
    private JPanel jPanel;
    private JFrame frame;

    public SubmitActionListener(){}
    public SubmitActionListener(JTextField jTextField, JTextField outDirTextField,
                                AnActionEvent anActionEvent, JPanel jPanel,JFrame frame){
        this.jTextField = jTextField;
        this.outDirTextField = outDirTextField;
        this.anActionEvent = anActionEvent;
        this.jPanel = jPanel;
        this.frame = frame;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        try{
            DisplayHelper.openMask(frame);

            String dirPath = jTextField.getText();
            String outDirPath = outDirTextField.getText();
            ActionHelper.checkBlankSelectedPath(frame,dirPath, outDirPath);

            Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
            DocProject  docProject = new DocProject();
            docProject.setProjectName(project.getName());

            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);

            PsiMethod currentPsiMethod = ActionHelper.getSelectedPsiMethod(anActionEvent, psiFile);

            Map<String, Set<PsiFile>> psiFileMap = CustomClassUtil.getModuleFilesMap(psiFile, virtualFile, project);

            List<DocModule>  docModuleList = new ArrayList<>();
            if(MapUtils.isNotEmpty(psiFileMap)){

                for (Map.Entry entry : psiFileMap.entrySet()) {
                    Set<PsiFile> psiFileSet = (Set<PsiFile>) entry.getValue();
                    boolean isRestModule = false;
                    List<DocClass> docClassList = new ArrayList<>();
                    for (PsiFile pFile : psiFileSet) {
                        DocClass docClass = new DocClass();
                        boolean isRestClass = false;
                        PsiClass psiClass = null;
                        for (PsiElement psiElement : pFile.getChildren()) {
                            if (psiElement instanceof PsiClass) {
                                psiClass = (PsiClass) psiElement;
                                //2.获取类注解，判断是否是rest，是则添加到DocClass
                                PsiAnnotation[] psiAnnotations = psiClass.getAnnotations();
                                for (PsiAnnotation psiAnnotation : psiAnnotations) {
                                    handleClassMappingPath(docClass, psiAnnotation);

                                    if(psiAnnotation.getText().contains("@RestController") ||psiAnnotation.getText().contains("@Controller")){
                                        isRestModule = true;//确认是rest模块
                                        isRestClass = true;
                                        //3.处理方法Method
                                        PsiMethod[] methods = getPsiMethods(currentPsiMethod, psiClass);
                                        List<DocMethod> docMethodList = new ArrayList<>();
                                        for (PsiMethod psiMethod : methods) {
                                            DocMethod docMethod =  new DocMethod();
                                            boolean isDocMethod = false;
                                            boolean isDocMethodFirst = false;//判断是否是restMethod第一步

                                            isDocMethodFirst = checkIfDocMethodFirst(psiMethod, isDocMethodFirst);

                                            boolean isGetMethod = false;
                                            for (PsiAnnotation annotation : psiMethod.getAnnotations()) {
                                                if(annotation.getText().contains("@GetMapping") ||annotation.getText().contains("@PostMapping")||
                                                        annotation.getText().contains("@RequestMapping")){

                                                    isDocMethod = checkIfDocMethod(psiAnnotation, isDocMethod, isDocMethodFirst);

                                                    if(isDocMethod){
                                                        isGetMethod = handleMethodMappingWayAndPath(docMethod, isGetMethod, annotation);
                                                    }
                                                }
                                            }

                                            if(isDocMethod){
                                                handleDocMethodNameParamComment(psiMethod, docMethod, isGetMethod);
                                                docMethodList.add(docMethod);
                                            }

                                        }
                                        docClass.setDocMethodList(docMethodList);
                                    }
                                }
                            }

                        }
                        if(isRestClass){
                            docClass.setClassName(psiClass.getName());
                            docClass.setPackageName(psiClass.getQualifiedName());
                            // 获取类注释
                            String classComment = handleSpecialCharacterForFreemarker(getClassComment(psiClass));
                            docClass.setClassComment(classComment);
                            docClassList.add(docClass);
                        }

                    }
                    //确认是Rest模块就添加DocClass与ModuleName
                    if(isRestModule){
                        DocModule docModule = new DocModule();
                        docModule.setModuleName(entry.getKey().toString());
                        docModule.setDocClassList(docClassList);
                        docModuleList.add(docModule);
                    }

                }

            }
            if(CollectionUtils.isNotEmpty(docModuleList)){
                docProject.setDocModuleList(docModuleList);
            }else{
                Messages.showWarningDialog("选定目录下未找到Rest接口", "警告");
                frame.dispose();
                return;
            }

            //生成word文档
            //jPanel.setEnabled(false);
            Map data = JSONObject.parseObject(JSONObject.toJSONString(docProject), Map.class);
            String outFileName = outDirPath+"/CocoDoc接口文档"+System.currentTimeMillis()+".doc";
            File file = FreeMarkerUtil.createFile(data,
                    "CocoDoc接口文档.fpl", outFileName);

            if(null != file){
                JComponent comp = (JComponent) e.getSource();
                Window win = SwingUtilities.getWindowAncestor(comp);
                win.dispose();
            }

        }catch (KnownException ex) {
            ex.printStackTrace();
            Messages.showWarningDialog(ex.getLocalizedMessage(), "警告");
            frame.dispose();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.showWarningDialog(ex.getLocalizedMessage(), "警告");
            frame.dispose();
            return;
        }

    }

    //处理DocMethod的名称+参数+注释
    private void handleDocMethodNameParamComment(PsiMethod psiMethod, DocMethod docMethod, boolean isGetMethod) {
        docMethod.setMethodName(psiMethod.getName());
        List<DocParam> docInParamList = handleMethodInParam(psiMethod,isGetMethod,docMethod);
        docMethod.setDocInParamList(docInParamList);

        List<DocParam> docOutParamList = handleDocOutParam(psiMethod);
        docMethod.setDocOutParamList(docOutParamList);

        // 3.获取方法的注释
        String methodComment = handleSpecialCharacterForFreemarker(getMethodComment(psiMethod));
        docMethod.setMethodComment(methodComment);
    }

    //处理方法请求路径和请求方式
    private boolean handleMethodMappingWayAndPath(DocMethod docMethod, boolean isGetMethod, PsiAnnotation annotation) {
        if(annotation.getText().contains("@GetMapping")){
            isGetMethod = true;
            docMethod.setMethodMappingWay("GET");
            docMethod.setMethodMappingPath(getMappingPath(annotation, 13));
        }
        if(annotation.getText().contains("@PostMapping")){
            docMethod.setMethodMappingWay("POST");
            docMethod.setMethodMappingPath(getMappingPath(annotation, 14));
        }
        if(annotation.getText().contains("@RequestMapping")){

            //docMethod.setMethodMappingWay("GET&&POST");
            MappingWayResult result = handleRequestMappingWay(annotation, isGetMethod);
            docMethod.setMethodMappingWay(result.getMappingWay());
            isGetMethod = result.isGetMethod();
            docMethod.setMethodMappingPath(getMappingPath(annotation, 17));
        }
        return isGetMethod;
    }

    //检查是否是rest的方法
    private boolean checkIfDocMethod(PsiAnnotation psiAnnotation, boolean isDocMethod, boolean isDocMethodFirst) {
        //只有类上@RestController或者（类上@Controller+方法@ResponseBody）的时候才生成Doc
        if(psiAnnotation.getText().contains("@RestController")){
            isDocMethod = true;
        }
        if(isDocMethodFirst && psiAnnotation.getText().contains("@Controller")){
            isDocMethod = true;
        }
        return isDocMethod;
    }

    //判断是否是rest Method的第一步
    private boolean checkIfDocMethodFirst(PsiMethod psiMethod, boolean isDocMethodFirst) {
        PsiElement[] psiMethodChildren = psiMethod.getChildren();
        for (PsiElement psiMethodChild : psiMethodChildren) {//1.方法中含有responsebody
            if (psiMethodChild.getText().contains("@ResponseBody")) {
                isDocMethodFirst = true;
            }
        }
        for (PsiAnnotation annotation : psiMethod.getAnnotations()) {//2.注释中含有responseBody
            if(annotation.getText().contains("@ResponseBody")){
                isDocMethodFirst = true;
            }
        }
        return isDocMethodFirst;
    }

    @NotNull
    private PsiMethod[] getPsiMethods(PsiMethod currentPsiMethod, PsiClass psiClass) {
        PsiMethod[] methods = null;
        if(null != currentPsiMethod){//只选定一个方法，就只添加一次
            methods = new PsiMethod[]{currentPsiMethod};
        }else{
            methods = psiClass.getMethods();
        }
        return methods;
    }

    //获取类访问路径
    private void handleClassMappingPath(DocClass docClass, PsiAnnotation psiAnnotation) {
        //类请求路径
        if(psiAnnotation.getText().contains("@RequestMapping")){
            if(StringUtils.isNotBlank(psiAnnotation.getText())){
                docClass.setClassMappingPath(getMappingPath(psiAnnotation, 17));
            }else{
                docClass.setClassMappingPath("/");
            }
        }else{
            docClass.setClassMappingPath("/");
        }
    }


    //处理RequestMapping方式的请求方法和路径
    private MappingWayResult handleRequestMappingWay(PsiAnnotation annotation, boolean isGetMethod) {
        String requestMappingWay = "GET&&POST";
        MappingWayResult result = new MappingWayResult();
        String annotaionText = annotation.getText();
        if(annotaionText.contains("=") && annotaionText.contains("method")){
            String[] split = annotaionText.split(",");
            for (String v : split) {
                if(v.contains("method")){
                    String mappingWay = v.split("=")[1].trim().replace("\"","").replace(")","");
                    if(mappingWay.equals("RequestMethod.GET")){
                        requestMappingWay = "GET";
                        isGetMethod = true;
                    }
                    if(mappingWay.equals("RequestMethod.POST")){
                        requestMappingWay = "POST";
                    }
                    if(mappingWay.equals("RequestMethod.HEAD")){
                        requestMappingWay = "HEAD";
                    }
                    if(mappingWay.equals("RequestMethod.PUT")){
                        requestMappingWay = "PUT";
                    }
                    if(mappingWay.equals("RequestMethod.PATCH")){
                        requestMappingWay = "PATCH";
                    }
                    if(mappingWay.equals("RequestMethod.DELETE")){
                        requestMappingWay = "DELETE";
                    }
                    if(mappingWay.equals("RequestMethod.OPTIONS")){
                        requestMappingWay = "OPTIONS";
                    }
                    if(mappingWay.equals("RequestMethod.TRACE")){
                        requestMappingWay = "TRACE";
                    }


                }
            }
        }
        result.setMappingWay(requestMappingWay);
        result.setGetMethod(isGetMethod);
        return result;
    }

    //通用适用于类请求路径
    private String getMappingPath(PsiAnnotation annotation, int beginIndex) {
        String mappingPath = "/";
        String text = annotation.getText();
        if(annotation.getText().contains("=")){
            String[] splitArray = text.split(",");
            for (String v : splitArray) {
                if(v.contains("value")){
                    String[] valueStr = v.split("=");
                    mappingPath = valueStr[1].replace("\"","");
                }
            }
        }else{
            if(text.contains("\"")){
                mappingPath = text.substring(beginIndex,text.length()-2);
            }else{//没有写路径
                mappingPath="/";
            }

        }

        return mappingPath.replaceAll("\\)", "");
    }

    //处理出参
    @NotNull
    private List<DocParam> handleDocOutParam(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        List<DocParam> docOutParamList = new ArrayList<>();
        if(BASIC_TYPE_LIST.contains(returnType.toString())){//若是基本类型
            DocParam docOutParam = new DocParam();
            docOutParam.setOutParamType(handleSpecialCharacterForFreemarker(returnType.toString().split(":")[1]));
            docOutParam.setOutParamName(handleSpecialCharacterForFreemarker(returnType.toString().split(":")[1]));
            docOutParam.setParamJson(docOutParam.getOutParamName());
            docOutParamList.add(docOutParam);
        }else {//非基本类型，即包装类
            PsiElement[] childrens = psiMethod.getReturnTypeElement().getChildren();
            for (PsiElement child : childrens) {
                if(null != child.findElementAt(1).getParent().getReference()){
                    PsiClass psiChildClass= (PsiClass) child.findElementAt(1).getParent().getReference().resolve();
                    DocParam docOutParam = new DocParam();
                    docOutParam.setOutParamType(handleSpecialCharacterForFreemarker(returnType.toString().split(":")[1]));
                    docOutParam.setOutParamName(handleSpecialCharacterForFreemarker(returnType.toString().split(":")[1]));
                    PsiClass psiInnerClass = null;
                    int tIndex = child.getText().indexOf("<")+1;
                    if (tIndex != 0) {//代表有泛型
                        psiInnerClass = (PsiClass) child.findElementAt(tIndex).getParent().getReference().resolve();
                    }
                    List<DocParamField> docParamFieldList = handleDocParamFieldList(psiChildClass, psiInnerClass);
                    docOutParam.setDocParamFieldList(docParamFieldList);

                    //构建出参报文
                    String paramJson = ToJsonUtil.parseJsonFromPsiClass(psiChildClass, psiInnerClass);
                    docOutParam.setParamJson(paramJson);

                    docOutParamList.add(docOutParam);
                }

            }
        }
        return docOutParamList;
    }

    //开启遮罩
    private void openMask(InfiniteProgressPanel glasspane) {


        //frame.setGlassPane(glassPane);
        //glassPane.setVisible(true);
//        jFrame.setBackground(Color.GRAY);
//        Component[] comptentes = jFrame.getComponents();
//        for (int i = 0; i < comptentes.length; i++) {
//            comptentes[i].setEnabled(false);
//        }

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        glasspane.setBounds(100, 100, (dimension.width) / 2, (dimension.height) / 2);
        frame.setGlassPane(glasspane);
        glasspane.start();//开始动画加载效果
        //frame.setVisible(true);
    }
    //关闭遮罩
    private void closeMask( InfiniteProgressPanel glasspane, JFrame masrkFrame) {
        //glassPane.setVisible(false);
        //jFrame.setVisible(false);
//        jFrame.setBackground(Color.white);
//        Component[] comptentes = jFrame.getComponents();
//        for (int i = 0; i < comptentes.length; i++) {
//            comptentes[i].setEnabled(true);
//        }
        glasspane.stop();
        masrkFrame.dispose();
    }

    //处理入参
    @NotNull
    private List<DocParam> handleMethodInParam(PsiMethod psiMethod, boolean isGetMethod, DocMethod docMethod) {
        JvmParameter[] parameters = psiMethod.getParameters();
        List<DocParam> docInParamList = new ArrayList<>();
        //Get方式请求需要拼接URL字符串
        StringBuilder getMethodInParamJson = new StringBuilder();
        if(docMethod.getMethodMappingPath().contains("{")){//rest风格的请求路径/student/{id}/xxx
            isGetMethod = true;//这里设置下，只要是有路径参数的，都设置为true，后面进行参数拼接
            getMethodInParamJson = new StringBuilder(docMethod.getMethodMappingPath());
        }else{
            getMethodInParamJson = new StringBuilder(docMethod.getMethodMappingPath()+"?");
        }

        for (JvmParameter parameter : parameters) {

            if(BASIC_TYPE_LIST.contains(parameter.getType().toString())){//若是基本类型

                DocParam docInParam = new DocParam();
                docInParam.setInParamType(handleSpecialCharacterForFreemarker(parameter.getType().toString().split(":")[1]));
                docInParam.setInParamName(handleSpecialCharacterForFreemarker(parameter.getName()));
                docInParamList.add(docInParam);
                if(isGetMethod){
                    if(docMethod.getMethodMappingPath().contains("{")){
                        getMethodInParamJson = new StringBuilder(getMethodInParamJson.toString().replace("{"+docInParam.getInParamName()+"}", "xxx"));
                    }else{
                        getMethodInParamJson.append(docInParam.getInParamName()).append("=xxx&");
                    }

                }
            }else{//非基本类型，即包装类
                PsiElement[] childrens = parameter.getSourceElement().getChildren();
                for (PsiElement child : childrens) {
                    if(child.getFirstChild() != null){
                        if(child.getFirstChild().getReference() != null){
                            PsiElement paramPsiElement = child.getFirstChild().getReference().resolve();
                            PsiClass paramPsiClass =(PsiClass)paramPsiElement;
                            if(!EXCLUDE_CLASS_LIST.contains(paramPsiClass.getName())){
                                DocParam docInParam = new DocParam();
                                docInParam.setInParamType(handleSpecialCharacterForFreemarker(parameter.getType().toString().split(":")[1]));
                                docInParam.setInParamName(handleSpecialCharacterForFreemarker(parameter.getName()));
                                PsiClass psiInnerClass = null;
                                int tIndex = child.getText().indexOf("<")+1;
                                if (tIndex != 0) {//代表有泛型
                                    psiInnerClass = (PsiClass) child.findElementAt(tIndex).getParent().getReference().resolve();
                                }

                                List<DocParamField> docParamFieldList = handleDocParamFieldList(paramPsiClass, psiInnerClass);
                                docInParam.setDocParamFieldList(docParamFieldList);

                                //构建入参报文
                                String paramJson = ToJsonUtil.parseJsonFromPsiClass(paramPsiClass, psiInnerClass);
                                docInParam.setParamJson(paramJson);

                                docInParamList.add(docInParam);
                            }


                        }
                    }

                }

            }

        }
        if(isGetMethod){
            if(getMethodInParamJson.toString().contains("&")){
                docMethod.setParamJson(getMethodInParamJson.substring(0,getMethodInParamJson.toString().length()-2));
            }else{
                docMethod.setParamJson(getMethodInParamJson.toString());
            }
        }
        return docInParamList;
    }

    //设置psiclass的内部filed参数
    private List<DocParamField>  handleDocParamFieldList(PsiClass paramPsiClass, PsiClass psiInnerClass) {
        if(!EXCLUDE_CLASS_LIST.contains(paramPsiClass.getName())) {
            if (null != paramPsiClass) {
                List<DocParamField> docParamFieldList = new ArrayList<>();
                //System.out.println(paramPsiClass.getName());
                PsiField[] fields = paramPsiClass.getAllFields();
                for (PsiField field : fields) {
                    if (!field.getModifierList().hasModifierProperty(PsiModifier.STATIC) &&
                            !field.getModifierList().hasModifierProperty(PsiModifier.FINAL)) {
                        if(!EXCLUDE_FIELD_LIST.contains(field.getType().toString())) {
                            DocParamField docParamField = new DocParamField();

                            docParamField.setFieldName(field.getName());

                            String fieldComment = handleSpecialCharacterForFreemarker(getFieldComment(field));
                            docParamField.setFieldComment(fieldComment);
                            String fieldType = field.getType().toString();
                            docParamField.setFieldType(fieldType.split(":")[1].replaceAll("<", "-")
                                    .replaceAll(">", ""));//这里进行转换是防止freemark格式混乱
                            //若字段是非基础类型的，则递归查找字段
                            if (field.getType().getPresentableText().indexOf("List") != -1) {//list类型字段
                                DocParam docParam = handleListDocInnerParams(field);
                                docParamField.setFieldValue(docParam);
                            }else if((field.getType().getPresentableText().indexOf("T") !=-1)){//泛型字段
                                if(null != psiInnerClass){
                                    DocParam docParam = handleTDocInnerParams(psiInnerClass);
                                    docParamField.setFieldValue(docParam);
                                }

                            }else if(!BASIC_TYPE_LIST.contains(field.getType().toString())){//证明是对象，非集合
                                DocParam docParam = handleObjectDocInnerParams(field);
                                docParamField.setFieldValue(docParam);
                            }
                            docParamFieldList.add(docParamField);
                        }
                    }

                }
                return docParamFieldList;
            }
        }
        return  null;
    }

    //处理对象类型的字段
    private DocParam handleObjectDocInnerParams(PsiField field) {
        DocParam docParam = null;
       // System.out.println(field.getName());
        PsiElement[] psiElements = field.getChildren();
        for (PsiElement psiElement : psiElements) {
            if(psiElement instanceof PsiTypeElement){
                if(psiElement.getFirstChild() !=null && psiElement.getFirstChild().getReference() != null &&
                        psiElement.getFirstChild().getReference().resolve() != null){

                    PsiElement psiElementChild = psiElement.getFirstChild().getReference().resolve();
                    if(psiElementChild instanceof PsiClass){
                        PsiClass psiClass = (PsiClass) psiElementChild;
                        docParam = new DocParam();
                        docParam.setInParamType(handleSpecialCharacterForFreemarker(psiClass.getName()));
                        docParam.setInParamName(handleSpecialCharacterForFreemarker(psiClass.getName()));
                        List<DocParamField> docParamFieldList = handleDocParamFieldList(psiClass, null);
                        docParam.setDocParamFieldList(docParamFieldList);
                    }
                }

            }
        }
        return docParam;
    }


    //处理泛型类型字段
    private DocParam handleTDocInnerParams(PsiClass psiInnerClass) {
        DocParam docParam = null;
        docParam = new DocParam();
        docParam.setInParamType(handleSpecialCharacterForFreemarker(psiInnerClass.getName()));
        docParam.setInParamName(handleSpecialCharacterForFreemarker(psiInnerClass.getName()));
        List<DocParamField> docParamFieldList = handleDocParamFieldList(psiInnerClass, null);
        docParam.setDocParamFieldList(docParamFieldList);

        return docParam;
    }

    //去除字段中的<>字符，防止freemark转义错误
    private String handleSpecialCharacterForFreemarker(String original){
        if(StringUtils.isNotBlank(original)){
            return original.replaceAll("<","-").replaceAll(">","");
        }
        return original;
    }

    //递归查找字段集合类型赋值
    private DocParam handleListDocInnerParams(PsiField field) {
        DocParam docParam = null;

        PsiElement[] psiElements = field.getChildren();
        for (PsiElement psiElement : psiElements) {
                if(psiElement instanceof PsiTypeElement){
                    PsiElement psiElementChild = psiElement.findElementAt(5).getParent().getReference().resolve();
                    if(psiElementChild instanceof PsiClass){
                        PsiClass psiClass = (PsiClass) psiElement.findElementAt(5).getParent().getReference().resolve();
                        docParam = new DocParam();
                        docParam.setInParamType(handleSpecialCharacterForFreemarker(psiClass.getName()));
                        docParam.setInParamName(handleSpecialCharacterForFreemarker(psiClass.getName()));
                        List<DocParamField> docParamFieldList = handleDocParamFieldList(psiClass, null);
                        docParam.setDocParamFieldList(docParamFieldList);
                    }
                }
        }
        return docParam;

    }

    /**
     * @author zhangjunwu3
     * @des 检查类的注释规则
     * @param  psiClass:元素
     * @return
     */
    private String getClassComment(PsiClass psiClass){
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiClass.getChildren()) {
            if (tmpEle instanceof PsiComment){
                classComment = (PsiComment) tmpEle;
                //int lineNumbers = document.getLineNumber(classComment.getTextOffset());
                // 注释的内容
                String tmpText = classComment.getText();
                return tmpText;

            }
        }

        return null;
    }


    /**
     * @author zhangjunwu3
     * @des 检查方法的注释规则
     * @param  psiMethod:元素
     * @return
     */
    private String getMethodComment(PsiMethod psiMethod){
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiMethod.getChildren()) {
            if (tmpEle instanceof PsiComment){
                classComment = (PsiComment) tmpEle;

                // 注释的内容
                String tmpText = classComment.getText();
                return tmpText;

            }
        }

        return null;
    }
    /**
     * @author zhangjunwu3
     * @des 获取字段的注释
     * @param  psiField:字段
     * @return
     */
    private String getFieldComment(PsiField psiField){
        PsiComment fieldComment = null;
        for (PsiElement tmpEle : psiField.getChildren()) {
            if (tmpEle instanceof PsiComment){
                fieldComment = (PsiComment) tmpEle;
                // 注释的内容
                String tmpText = fieldComment.getText();
                return tmpText;

            }
        }

        return null;
    }






}
