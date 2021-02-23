package main.java.com.jd.coco.entity;

import java.util.List;

/*
* DOC 项目
 */
public class DocProject {

    private String projectName;
    private List<DocModule> docModuleList;

    public DocProject() {
    }

    public DocProject(String projectName, List<DocModule> docModuleList) {
        this.projectName = projectName;
        this.docModuleList = docModuleList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<DocModule> getDocModuleList() {
        return docModuleList;
    }

    public void setDocModuleList(List<DocModule> docModuleList) {
        this.docModuleList = docModuleList;
    }
}
