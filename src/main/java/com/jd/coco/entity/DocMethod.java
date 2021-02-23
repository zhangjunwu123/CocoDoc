package main.java.com.jd.coco.entity;

import java.util.List;

public class DocMethod {

    //方法名
    private String methodName;
    //请求方式
    private String methodMappingWay;
    //请求路径
    private String methodMappingPath;
    //入参数组
    private List<DocParam> docInParamList;

    //出参数组
    private List<DocParam> docOutParamList;
    //注释
    private String methodComment;
    //get方式的报文
    private String paramJson;


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodMappingWay() {
        return methodMappingWay;
    }

    public void setMethodMappingWay(String methodMappingWay) {
        this.methodMappingWay = methodMappingWay;
    }

    public String getMethodMappingPath() {
        return methodMappingPath;
    }

    public void setMethodMappingPath(String methodMappingPath) {
        this.methodMappingPath = methodMappingPath;
    }

    public List<DocParam> getDocInParamList() {
        return docInParamList;
    }

    public void setDocInParamList(List<DocParam> docInParamList) {
        this.docInParamList = docInParamList;
    }

    public List<DocParam> getDocOutParamList() {
        return docOutParamList;
    }

    public void setDocOutParamList(List<DocParam> docOutParamList) {
        this.docOutParamList = docOutParamList;
    }

    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
