package com.jikexueyuan.evernote.model;

import android.graphics.Bitmap;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by admin on 2016/12/12.
 */

public class Entity extends BmobObject{

    private String title;
    private String date;
    private String content;
    private String username;
    private int id;
    private List<Bitmap> bitmaps;
    private List<String> imgTags;

    public Entity(String title, String content, String date, String username) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.username = username;
    }

    public Entity(String title, String content, String date, int id) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.id = id;
    }

    public Entity(int uid, List<Bitmap> bitmaps, List<String> imgTags) {
        this.id = uid;
        this.bitmaps = bitmaps;
        this.imgTags = imgTags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public List<String> getImgTags() {
        return imgTags;
    }

    public void setImgTags(List<String> imgTags) {
        this.imgTags = imgTags;
    }
}
