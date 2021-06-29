package com.agamilabs.smartshop.VolleyActivity;

import java.lang.reflect.Field;

public class ProjectModel {

    String projectno, projectid, projecttitle, referenceid, pstatusno;

    public ProjectModel(String projectno, String projectid, String projecttitle, String referenceid, String pstatusno) {
        this.projectno = projectno;
        this.projectid = projectid;
        this.projecttitle = projecttitle;
        this.referenceid = referenceid;
        this.pstatusno = pstatusno;
    }

    public ProjectModel() {
    }

    public Field[] getAllFields() {
        return this.getClass().getDeclaredFields();
    }


    public String getProjectno() {
        return projectno;
    }

    public void setProjectno(String projectno) {
        this.projectno = projectno;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjecttitle() {
        return projecttitle;
    }

    public void setProjecttitle(String projecttitle) {
        this.projecttitle = projecttitle;
    }

    public String getReferenceid() {
        return referenceid;
    }

    public void setReferenceid(String referenceid) {
        this.referenceid = referenceid;
    }

    public String getPstatusno() {
        return pstatusno;
    }

    public void setPstatusno(String pstatusno) {
        this.pstatusno = pstatusno;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "projectno='" + projectno + '\'' +
                ", projectid='" + projectid + '\'' +
                ", projecttitle='" + projecttitle + '\'' +
                '}';
    }

}
