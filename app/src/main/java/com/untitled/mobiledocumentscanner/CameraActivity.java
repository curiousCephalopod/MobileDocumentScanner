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

public class CameraActivity extends AppCompatActivity {
    private ImageSurfaceView imageSurfaceView;
    private Camera camera;

    private FrameLayout cameraPreviewLayout;

    private String docID;
    private String pageNo;
    String ip;

    private String urlCreatePage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle bundle = getIntent().getExtras();
        docID = bundle.getString("docID");
        pageNo = bundle.getString("pageNo");
        ip = bundle.getString("ip");

        Log.d("TEST", ip);
        urlCreatePage = "http://" + ip + "/DocumentScanner/create_page.php";

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Toast.makeText(context, "This device does not have a front camera.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public static void start(Context context, int docID, int pageNo, String ip) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra("docID", docID + "");
        intent.putExtra("pageNo", pageNo + "");
        intent.putExtra("ip", ip);

        context.startActivity(intent);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this, Manifest.permission.CAMERA)) {
            Toast.makeText(CameraActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use the camera.");
                    cameraPreviewLayout = (FrameLayout) findViewById(R.id.cameraPreview);

                    camera = Camera.open();
                    camera.setDisplayOrientation(90);
                    imageSurfaceView = new ImageSurfaceView(CameraActivity.this, camera);
                    cameraPreviewLayout.addView(imageSurfaceView);

                    final PictureCallback pictureCallback = new PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (data == null) {
                                Log.d("INFO", "Captured image is empty.");
                                return;
                            }

                            Log.d("TEST", Base64.encodeToString(BitmapUtil.getBytes(image), Base64.DEFAULT));
                            new addPage().execute(Base64.encodeToString(BitmapUtil.getBytes(image), Base64.DEFAULT));
                            finish();
                        }
                    };

                    Button captureButton = (Button) findViewById(R.id.cameraCapture);
                    captureButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            camera.takePicture(null, null, pictureCallback);
                        }
                    });

                } else {
                    Log.e("value", "Permission Denied, You cannot use the camera.");
                    finish();
                }
                break;
        }
    }

    class addPage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            JSONParser jParser = new JSONParser();
            List<NameValuePair> pageParams = new ArrayList<NameValuePair>();
            pageParams.add(new BasicNameValuePair("docID", docID));
            pageParams.add(new BasicNameValuePair("image", params[0]));
            pageParams.add(new BasicNameValuePair("encryptionKey", ""));
            pageParams.add(new BasicNameValuePair("pageNo", pageNo));

            // Retrieve JSON
            JSONObject newPage = jParser.makeHttpRequest(urlCreatePage, "POST", pageParams);

            Log.d("INFO", newPage.toString());
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