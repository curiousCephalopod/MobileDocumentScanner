package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

/**
 * Created by Josh on 12-Apr-17.
 */

public class CreateList {
    private String imageTitle;
    private Bitmap image;

    public String getImageTitle(){
        return imageTitle;
    }

    public void setImageTitle(String title){
        this.imageTitle = title;
    }

    public Bitmap getImage(){
        return image;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }


}
