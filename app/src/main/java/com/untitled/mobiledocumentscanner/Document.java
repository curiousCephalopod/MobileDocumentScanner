package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * Class designed and implemented by Joshua (eeu67d).
 * Data structure to hold the details of a single document.
 */

public class Document implements Serializable{
    // Document ID, as is stored in database
    private int id;
    // Document details
    private String title;
    private String date;
    private int pages;
    // Byte array of cover bitmap
    private byte[] cover;

    /**
     * Retrieve document properties from parameters.
     *
     * @param id    Document ID
     * @param title Document title
     * @param date  Date of creation
     * @param pages Number of pages
     * @param cover Cover page of document
     */
    public Document(int id, String title, String date, int pages, byte[] cover) {
        // Retrieve variables from parameters
        this.id = id;
        this.title = title;
        this.date = date;
        this.pages = pages;
        this.cover = cover;
    }

    /**
     * Returns document ID.
     * @return ID
     */
    public int getID() {
        return id;
    }

    /**
     * Set the ID of document, used in creation.
     *
     * @param id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Returns document title, used in display.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the document, used in creation.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns date of created, used in display.
     * @return Date of creation
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the date of the document, used in creation.
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the number of pages, used in display.
     * @return Number of Pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * Returns the cover page, used in display.
     * @return Cover page byte array
     */
    public Bitmap getCover() {
        return BitmapUtil.getImage(cover);
    }
}