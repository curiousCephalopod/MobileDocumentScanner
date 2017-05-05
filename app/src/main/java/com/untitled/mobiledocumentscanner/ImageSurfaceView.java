package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Class designed by Amber (eeu68b) and implemented by Joshua (eeu67d).
 * View for a camera preview.
 */

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    // A camera object
    private Camera camera;
    // Preview holder
    private SurfaceHolder surfaceHolder;

    /**
     * Retrieve parameters.
     * @param context Application context
     * @param camera Camera is use
     */
    public ImageSurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    /**
     * On creation, start the preview.
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.camera.setPreviewDisplay(holder);
            this.camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Release the camera on destruction.
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.camera.stopPreview();
        this.camera.release();
    }
}
