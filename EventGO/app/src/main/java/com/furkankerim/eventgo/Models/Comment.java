package com.furkankerim.eventgo.Models;

public class Comment {
    private String comment,date,username,img;
    private String rate;

    public Comment(String comment, String rate, String date, String username, String img) {
        this.comment = comment;
        this.rate = rate;
        this.date = date;
        this.username = username;
        this.img = img;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
