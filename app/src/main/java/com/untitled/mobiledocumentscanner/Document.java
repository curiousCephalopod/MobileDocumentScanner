package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by J on 24-Apr-17.
 */

public class Document implements Serializable{
    private int id;
    private String title;
    private String date;
    private int pages;
    private byte[] cover;


    public Document(int id, String title, String date, int pages, Bitmap cover) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.pages = pages;
        this.cover = BitmapUtil.getBytes(cover);
    }

    public ArrayList<Bitmap> populatePages(){
        ArrayList<Bitmap> list = new ArrayList<>();

        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String[]> populateTags() {
        ArrayList<String[]> tags = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            PreparedStatement findTags = conn.prepareStatement("SELECT t.tagID, t.tag FROM Tag t, TagApplication a " +
                    "WHERE t.tagID = a.tagID AND a.docID = ?" +
                    "GROUP BY t.tagID");

            findTags.setInt(1, getID());
            ResultSet rs = findTags.executeQuery();
            while (rs.next()) {
                String[] tag = new String[2];
                tag[0] = rs.getInt("tagID") + "";
                tag[1] = rs.getString("tag");

                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
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