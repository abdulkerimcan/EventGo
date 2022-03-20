package com.furkankerim.eventgo.Models;

public class News {
    private String title,date, info;
    private String img;
    private String circleImg;

    public News() {
    }

    public News(String circleImg,String date,String img,String info,String title) {
        this.title = title;
        this.date = date;
        this.info = info;
        this.img = img;
        this.circleImg = circleImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCircleImg() {
        return circleImg;
    }

    public void setCircleImg(String circleImg) {
        this.circleImg = circleImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


}

