package com.akshat.weewear.model;

public class DataModel {
    String url, title, subtitle;

    public DataModel() {
    }

    public DataModel(String url, String subtitle, String title) {
        this.url = url;
        this.subtitle = subtitle;
        this.title = title;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
