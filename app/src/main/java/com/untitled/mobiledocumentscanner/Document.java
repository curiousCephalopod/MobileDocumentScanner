package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * Created by J on 24-Apr-17.
 */

public class Document implements Serializable{
    private int id;
    private String title;
    private String date;
    private int pages;
    private byte[] cover;


    public Document(int id, String title, String date, int pages, byte[] cover) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.pages = pages;
        this.cover = cover;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setCover(Bitmap cover) {
        this.cover = BitmapUtil.getBytes(cover);
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getPages() {
        return pages;
    }

    public Bitmap getCover() {
        return BitmapUtil.getImage(cover);
    }
}