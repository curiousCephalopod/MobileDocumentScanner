package com.untitled.mobiledocumentscanner;
// using https://examples.javacodegeeks.com/android/core/hardware/camera-hardware/android-camera-example/

//attempt with https://developer.android.com/training/camera/cameradirect.html
import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    @SuppressWarnings("deprecations")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch (IOException e)
        {
            //left blank
        }
    }

//    public void refreshCamera(Camera camera) {
//        if (mHolder.getSurface() == null) {
//            // preview surface does not exist
//            return;
//        }
//        // stop preview before making changes
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e) {
//            // ignore: tried to stop a non-existent preview
//        }
//        // set preview size and make any resize, rotate or
//        // reformatting changes here
//        // start preview with new settings
//        setCamera(camera);
//        try {
//            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
//        } catch (Exception e) {
//            Log.d(VIEW_LOG_TAG, "Error starting camera: " + e.getMessage());
//        }
//    }
//
//
//
//    public void setCamera(Camera camera) {
//        //method to set a camera instance
//        mCamera = camera;
//    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch (Exception e){
            //left blank
        }
   }
}
