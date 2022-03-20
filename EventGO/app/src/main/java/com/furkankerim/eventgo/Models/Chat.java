package com.furkankerim.eventgo.Models;

public class Chat {
    private String channelID,userID,userImg,username;

    public Chat(String channelID, String userID, String userImg, String username) {
        this.channelID = channelID;
        this.userID = userID;
        this.userImg = userImg;
        this.username = username;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
