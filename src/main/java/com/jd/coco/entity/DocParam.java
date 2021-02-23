package main.java.com.jd.coco.entity;

import java.util.List;

/*
* 入参
* */
public class DocParam {

    //入参名
    private String inParamName;
    //入参类型
    private String inParamType;
    //入参包名
    private String inParamPackageName;
    //入参内部字段
    private List<DocParamField> docParamFieldList;

    //出参名
    private String outParamName;
    //出参类型
    private String outParamType;
    //出参包名
    private String outParamPackageName;
    //参数json报文
    private String paramJson;

    public String getInParamName() {
        return inParamName;
    }

    public void setInParamName(String inParamName) {
        this.inParamName = inParamName;
    }

    public String getInParamType() {
        return inParamType;
    }

    public void setInParamType(String inParamType) {
        this.inParamType = inParamType;
    }

    public String getInParamPackageName() {
        return inParamPackageName;
    }

    public void setInParamPackageName(String inParamPackageName) {
        this.inParamPackageName = inParamPackageName;
    }

    public List<DocParamField> getDocParamFieldList() {
        return docParamFieldList;
    }

    public void setDocParamFieldList(List<DocParamField> docParamFieldList) {
        this.docParamFieldList = docParamFieldList;
    }

    public String getOutParamName() {
        return outParamName;
    }

    public void setOutParamName(String outParamName) {
        this.outParamName = outParamName;
    }

    public String getOutParamType() {
        return outParamType;
    }

    public void setOutParamType(String outParamType) {
        this.outParamType = outParamType;
    }

    public String getOutParamPackageName() {
        return outParamPackageName;
    }

    public void setOutParamPackageName(String outParamPackageName) {
        this.outParamPackageName = outParamPackageName;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
