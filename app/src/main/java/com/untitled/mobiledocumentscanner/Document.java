package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;


/**
 * Created by J on 24-Apr-17.
 */

public class Document implements Serializable{
    private int id;
    private String title;
    private Date date;
    private int pages;
    private Bitmap cover;

    public Document(int id, String title, Date date, int pages, Bitmap cover){
        this.id = id;
        this.title = title;
        this.date = date;
        this.pages = pages;
        this.cover = cover;
    }

    public ArrayList<Bitmap> populatePages(){
        ArrayList<Bitmap> list = new ArrayList<>();

        return list;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public int getPages() {
        return pages;
    }

    public Bitmap getCover() {
        return cover;
    }
}