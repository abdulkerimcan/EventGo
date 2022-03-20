package com.furkankerim.eventgo.Models;

public class User {
    private String email,username;
    private Boolean isOrganizer;
    private String downloadUrl = "default";

    public User(String email, String username, String downloadUrl, Boolean isOrganizer) {
        this.email = email;
        this.username = username;
        this.downloadUrl = downloadUrl;
        this.isOrganizer = isOrganizer;
    }

    public String getEmail() {
        return email;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Boolean getOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(Boolean organizer) {
        isOrganizer = organizer;
    }
}
