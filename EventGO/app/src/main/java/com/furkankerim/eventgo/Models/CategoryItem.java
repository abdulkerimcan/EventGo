package com.furkankerim.eventgo.Models;


import java.io.Serializable;

public class CategoryItem implements Serializable {
    private String title,location,date,url,info,hour,postID,latitude,longitude,price,count,organizerID,starCount;


    public CategoryItem(String title, String location, String date, String url, String info, String hour,String postID,String latitude,String longitude,String price,String count,String organizerID,String starCount) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.url = url;
        this.info = info;
        this.hour = hour;
        this.postID = postID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.count = count;
        this.organizerID = organizerID;
        this.starCount = starCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public String getStarCount() {
        return starCount;
    }

    public void setStarCount(String starCount) {
        this.starCount = starCount;
    }
}