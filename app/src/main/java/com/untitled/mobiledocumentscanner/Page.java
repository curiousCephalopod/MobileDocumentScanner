package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by J on 25-Apr-17.
 */

public class Page implements Serializable {
    private int imageID;
    private byte[] image;
    private String encryptionKey;
    private int pageNo;

    public Page(int imageID, Bitmap image, String encryptionKey, int pageNo) {
        this.imageID = imageID;
        this.image = BitmapUtil.getBytes(image);
        this.encryptionKey = encryptionKey;
        this.pageNo = pageNo;
    }

    public int getImageID() {
        return imageID;
    }

    public Bitmap getImage() {
        return BitmapUtil.getImage(image);
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public int getPageNo() {
        return pageNo;
    }
}
