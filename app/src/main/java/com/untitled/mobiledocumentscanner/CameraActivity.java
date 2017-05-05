package com.untitled.mobiledocumentscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class designed by Amber (eeu68b) and implemented by Joshua (eeu67d).
 * Activity to take a photo and upload it.
 */
public class CameraActivity extends AppCompatActivity {
    // IP address of the server
    String ip;
    // Surface view for camera preview
    private ImageSurfaceView imageSurfaceView;
    // Actual camera representation
    private Camera camera;
    // Layout to hold the surface view
    private FrameLayout cameraPreviewLayout;
    // Current document that is being edited
    private String docID;
    // Current page to take
    private String pageNo;
    // Server address for the php file
    private String urlCreatePage;

    /**
     * Initialises and starts the activity.
     *
     * @param context Application
     * @param docID   Document to edit
     * @param pageNo  Page number to add
     * @param ip      IP address of server
     */
    public static void start(Context context, int docID, int pageNo, String ip) {
        // Set up an intent for the new activity
        Intent intent = new Intent(context, CameraActivity.class);
        // Store the variables
        intent.putExtra("docID", docID + "");
        intent.putExtra("pageNo", pageNo + "");
        intent.putExtra("ip", ip);

        context.startActivity(intent);
    }

    /**
     * Initialise and retrieve environment variables.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Retrieve stored properties
        Bundle bundle = getIntent().getExtras();
        // Populate variables
        docID = bundle.getString("docID");
        pageNo = bundle.getString("pageNo");
        ip = bundle.getString("ip");

        // Create the URL from the address
        urlCreatePage = "http://" + ip + "/DocumentScanner/create_page.php";

        // Request permissions to use the camera
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        // If the camera is not accessible, return to the previous page
        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Toast.makeText(context, "This device does not have a front camera.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Request permission to access the camera.
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this, Manifest.permission.CAMERA)) {
            Toast.makeText(CameraActivity.this, "Camera permission is necessary to take pictures.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    /**
     * Called after the permission request, prepares the camera.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Request code is the response
        switch (requestCode) {
            case 1:
                // Permission granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use the camera.");
                    cameraPreviewLayout = (FrameLayout) findViewById(R.id.cameraPreview);

                    // Activate the camera and set orientation
                    camera = Camera.open();
                    camera.setDisplayOrientation(90);
                    // Initialise the preview
                    imageSurfaceView = new ImageSurfaceView(CameraActivity.this, camera);
                    cameraPreviewLayout.addView(imageSurfaceView);

                    // Prepare the callback after taking a photo
                    final PictureCallback pictureCallback = new PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            // Convert the byte array into a bitmap
                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                            // Check for contents
                            if (data == null) {
                                Log.d("INFO", "Captured image is empty.");
                                return;
                            }

                            // Compress the bitmap into a png-based byte array, and encode it into a string to add
                            new addPage().execute(Base64.encodeToString(BitmapUtil.getBytes(image), Base64.DEFAULT));
                            // Return to the details page
                            finish();
                        }
                    };

                    // Set the capture button to take a picture
                    Button captureButton = (Button) findViewById(R.id.cameraCapture);
                    captureButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            camera.takePicture(null, null, pictureCallback);
                        }
                    });

                } else {
                    // If the camera is denied, return to the details page
                    Log.e("value", "Permission Denied, You cannot use the camera.");
                    finish();
                }
                break;
        }
    }

    /**
     * AsyncTask to add a new page without stopping the main thread.
     */
    class addPage extends AsyncTask<String, String, String> {

        /**
         * Executes the HTTP request in the background.
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            // Create a parser to read the response
            JSONParser jParser = new JSONParser();
            // Store the parameters for the MySQL query
            List<NameValuePair> pageParams = new ArrayList<NameValuePair>();
            pageParams.add(new BasicNameValuePair("docID", docID));
            pageParams.add(new BasicNameValuePair("image", params[0]));
            pageParams.add(new BasicNameValuePair("encryptionKey", ""));
            pageParams.add(new BasicNameValuePair("pageNo", pageNo));

            // Retrieve JSON from HTTP
            JSONObject newPage = jParser.makeHttpRequest(urlCreatePage, "POST", pageParams);

            try {
                // Check for success
                int newPageSuccess = newPage.getInt("success");

                if (newPageSuccess == 1) {
                    // Page Added
                } else {
                    Log.d("INFO", "failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}