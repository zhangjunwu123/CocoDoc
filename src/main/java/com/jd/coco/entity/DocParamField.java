package main.java.com.jd.coco.entity;

/*
* 入参字段
* */
public class DocParamField {
    //字段名
    private String fieldName;
    //字段注释
    private String fieldComment;
    //字段类型
    private String fieldType;
    //若字段是集合类型的则赋值--1.0只支持list
    private DocParam fieldValue;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public DocParam getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(DocParam fieldValue) {
        this.fieldValue = fieldValue;
    }
}
