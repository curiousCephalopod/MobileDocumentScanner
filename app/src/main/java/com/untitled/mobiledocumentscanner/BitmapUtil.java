package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Code sourced from StackOverflow, for quick conversion.
 */

public class BitmapUtil {

    /**
     * Convert a bitmap to bytes for storage.
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

        return stream.toByteArray();
    }

    /**
     * Convert bytes to a bitmap to display.
     * @param image
     * @return
     */
    public static Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
