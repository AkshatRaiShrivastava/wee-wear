package com.akshat.weewear.model;

import java.util.List;

public class PostModel {
    String title, subtitle, username, email, userId, user_image_url, postUrl, postId;

    public PostModel() {
    }


    public PostModel(String title, String subtitle, String username, String email,String postId, String userId, String user_image_url, String postUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.postUrl = postUrl;
        this.user_image_url = user_image_url;
        this.postId = postId;
    }


    public String getUser_image_url() {
        return user_image_url;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserImageUrl(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getEmail() {
        return email;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
