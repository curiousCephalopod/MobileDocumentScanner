package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Manages page image views,
 */

public class ViewPagerAdapter extends PagerAdapter{
    // Application context
    private Context context;
    // List of images in a document
    private ArrayList<Page> pages;
    // Is the view fullscreen
    private boolean fullscreen;
    // Document we are viewing
    private int docID;
    // IP address of server
    private String ip;

    /**
     * Retrieve parameters
     * @param context Application context
     * @param pages List of pages
     * @param fullscreen Fullscreen boolean
     * @param docID ID of document
     * @param ip IP of server
     */
    public ViewPagerAdapter(Context context, ArrayList<Page> pages, Boolean fullscreen, int docID, String ip) {
        this.context = context;
        this.pages = pages;
        this.fullscreen = fullscreen;
        this.docID = docID;
        this.ip = ip;
    }

    /**
     * Return number of items in the adapter.
     * @return
     */
    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    /**
     * Instantiate a single image.
     * @param container
     * @param i
     * @return
     */
    @Override
    public Object instantiateItem(final ViewGroup container, int i) {
        final ImageView imageView = new ImageView(context);
        // Set the image from the page
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(pages.get(i).getImage());
        if (!fullscreen) {
            if (pages.get(i).getImageID() != -1) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Stare the full screen view
                        ImageActivity.start(context, pages, docID, ip);
                    }
                });
            } else {
                // If its the placeholder
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start the camera view
                        CameraActivity.start(context, docID, 1, ip);
                    }
                });
            }
        }
        container.addView(imageView, 0);

        return imageView;
    }

    /**
     * Remove an item.
     * @param container
     * @param i
     * @param obj
     */
    @Override
    public void destroyItem(ViewGroup container, int i, Object obj){
        container.removeView((ImageView) obj);
    }
}
