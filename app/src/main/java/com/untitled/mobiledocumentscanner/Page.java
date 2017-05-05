package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Represents a single page in a document.
 */

public class Page implements Serializable {
    // Image ID, as in database
    private int imageID;
    // Byte array for cover bitmap
    private byte[] image;
    // Encryption key of image
    private String encryptionKey;
    // Image number
    private int pageNo;

    /**
     * Retrieve parameters.
     * @param imageID Image ID
     * @param image Image byte array
     * @param encryptionKey Image encryption key
     * @param pageNo Paeg number
     */
    public Page(int imageID, byte[] image, String encryptionKey, int pageNo) {
        this.imageID = imageID;
        this.image = image;
        this.encryptionKey = encryptionKey;
        this.pageNo = pageNo;
    }

    /**
     * Return image ID.
     * @return
     */
    public int getImageID() {
        return imageID;
    }

    /**
     * Convert and return image bitmap.
     * @return
     */
    public Bitmap getImage() {
        return BitmapUtil.getImage(image);
    }
}
