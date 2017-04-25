package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this,mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    /**
     * Helper method to access camera returns null if it cannot get camera or doesn't
     * exist
     * @return
     */
    private Camera getCameraInstance()
    {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {

        }
        return camera;
    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            }catch (IOException e){

            }
        }
    };

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DocumentScanner");
        if (!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.d("DocumentScanner", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath()+ File.separator
        + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
//    //make sure back facing camera
//    private int findBackFacingCamera() {
//        int cameraID = -1;
//        int numberOfCamera = Camera.getNumberOfCameras();
//        for (int i = 0; i < numberOfCamera; i++) {
//            CameraInfo info = new CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
//                cameraID = i;
//                break;
//            }
//        }
//        return cameraID;
//    }
//
//    //init camera
//    public void getCamera() {
//        int cameraID = findBackFacingCamera();
//        if (cameraID >= 0) {
//            //open backfacingcamera set picture callback, refresh prev
//            mCamera = Camera.open(cameraID);
//            mPicture = getPictureCallback();
//            mPreview.refreshCamera(mCamera);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //when on Pause, release camera in order to be used from other applications
//        releaseCamera();
//    }
//
//    private boolean hasCamera(Context context) {
//        //check if has camera
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private PictureCallback getPictureCallback() {
//        PictureCallback picture = new PictureCallback() {
//
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                //make a new picture file
//                File pictureFile = getOutputMediaFile();
//
//                if (pictureFile == null) {
//                    return;
//                }
//                try {
//                    //write the file
//                    FileOutputStream fos = new FileOutputStream(pictureFile);
//                    fos.write(data);
//                    fos.close();
//
//                } catch (FileNotFoundException e) {
//                } catch (IOException e) {
//                }
//
//                //refresh camera to continue preview
//                mPreview.refreshCamera(mCamera);
//            }
//        };
//        return picture;
//    }
//
//    OnClickListener captrureListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            mCamera.takePicture(null, null, mPicture);
//        }
//    };
//
//    //make picture and save to a folder
//    private static File getOutputMediaFile() {
//        //make a new file directory inside the "sdcard" folder
//        File mediaStorageDir = new File("/sdcard/", "JCG Camera");
//
//        //if this "JCGCamera folder does not exist
//        if (!mediaStorageDir.exists()) {
//            //if you cannot make this folder return
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//
//        //take the current timeStamp
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        //and make a media file:
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//
//        return mediaFile;
//    }
//
//    private void releaseCamera() {
//        // stop and release camera
//        if (mCamera != null) {
//            mCamera.release();
//            mCamera = null;
//        }
//    }
}