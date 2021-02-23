package main.java.com.jd.coco.entity;

import java.util.List;

/*
*
* DOC的 module模块
* */
public class DocModule {
    private String moduleName;
    private List<DocClass> docClassList;

    public DocModule() {
    }

    public DocModule(String moduleName, List<DocClass> docClassList) {
        this.moduleName = moduleName;
        this.docClassList = docClassList;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<DocClass> getDocClassList() {
        return docClassList;
    }

    public void setDocClassList(List<DocClass> docClassList) {
        this.docClassList = docClassList;
    }
}
