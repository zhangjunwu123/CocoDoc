package main.java.com.jd.coco.entity;

import java.util.List;

/*
* Doc 的文件类
* */
public class DocClass {
    //类名
    private String className;
    //包名
    private String packageName;
    //方法的注释
    private String classComment;
    //类请求路径
    private String classMappingPath;

    //方法list
    private List<DocMethod> docMethodList;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<DocMethod> getDocMethodList() {
        return docMethodList;
    }

    public void setDocMethodList(List<DocMethod> docMethodList) {
        this.docMethodList = docMethodList;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public String getClassMappingPath() {
        return classMappingPath;
    }

    public void setClassMappingPath(String classMappingPath) {
        this.classMappingPath = classMappingPath;
    }
}
