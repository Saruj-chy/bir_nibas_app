package com.agamilabs.smartshop.VolleyActivity;

import java.lang.reflect.Field;


public class ProjectUpdateModel {

    String fileno, projectno, wpno, statetime, statedate, userno, roleno, title, description, media, thumbnail, colorcode;
    String image_list = "";
    String thumb_list = "";

    public Field[] getAllFields() {
        return this.getClass().getDeclaredFields();
    }

    public ProjectUpdateModel() {
    }

    public String getFileno() {
        return fileno;
    }

    public void setFileno(String fileno) {
        this.fileno = fileno;
    }

    public String getProjectno() {
        return projectno;
    }

    public void setProjectno(String projectno) {
        this.projectno = projectno;
    }

    public String getWpno() {
        return wpno;
    }

    public void setWpno(String wpno) {
        this.wpno = wpno;
    }

    public String getStatetime() {
        return statetime;
    }

    public void setStatetime(String statetime) {
        this.statetime = statetime;
    }

    public String getStatedate() {
        return statedate;
    }

    public void setStatedate(String statedate) {
        this.statedate = statedate;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getRoleno() {
        return roleno;
    }

    public void setRoleno(String roleno) {
        this.roleno = roleno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }

    public String getImage_list() {
        return image_list;
    }

    public void setImage_list(String image_list) {
        this.image_list = image_list;
    }

    public String getThumb_list() {
        return thumb_list;
    }

    public void setThumb_list(String thumb_list) {
        this.thumb_list = thumb_list;
    }

    @Override
    public String toString() {
        return "UpdateProjectModel{" +
                "fileno='" + fileno + '\'' +
                ", projectno='" + projectno + '\'' +
                ", title='" + title + '\'' +
                ", image_list='" + image_list + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
