package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity{
    private Document doc;
    ArrayList<Page> pages;
    ArrayList<String[]> currentTags;
    JSONParser jParser = new JSONParser();
    TagAdapter tagAdapter;
    private String ip;

    private String urlTags;
    private String urlTag;
    private String urlCreateTag;
    private String urlApplyTag;
    private String urlPages;
    private String urlAllTags;

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        this.doc = (Document) bundle.get("document");
        ip = bundle.getString("ip");

        urlTags = "http://" + ip + "/DocumentScanner/find_tags.php";
        urlTag = "http://" + ip + "/DocumentScanner/find_tag.php";
        urlCreateTag = "http://" + ip + "/DocumentScanner/create_tag.php";
        urlApplyTag = "http://" + ip + "/DocumentScanner/apply_tag.php";
        urlPages = "http://" + ip + "/DocumentScanner/retrieve_pages.php";
        urlAllTags = "http://" + ip + "/DocumentScanner/find_all_tags.php";

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        TextView viewTitle = (TextView)findViewById(R.id.imageName);
        viewTitle.setText(doc.getTitle());

        TextView viewDate = (TextView)findViewById(R.id.imageDate);
        viewDate.setText(doc.getDate());

        TextView viewPages = (TextView)findViewById(R.id.imagePages);
        viewPages.setText(doc.getPages() + " pages");

        currentTags = new ArrayList<>();
        new retrieveTags().execute();
        new retrievePages().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        new retrievePages().execute();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DetailActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void prepareTesseract() {
        File dir = new File(DATA_PATH + TESSDATA);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("INFO", "ERROR: Creation of directory " + DATA_PATH + TESSDATA + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i("INFO", "Created directory " + DATA_PATH + TESSDATA);
        }

        try {
            String fileList[] = getAssets().list(TESSDATA);

            for (String fileName : fileList) {
                String pathToDataFile = DATA_PATH + TESSDATA + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(TESSDATA + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();

                    Log.d("INFO", "Copied " + fileName + " to tessdata");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanTags(View v) {
        if (checkPermission()) {
            prepareTesseract();

            new scanTags().execute();
        }
    }

    public void addTag(View v) {
        EditText newTag = (EditText) findViewById(R.id.newTag);
        String tag = newTag.getText().toString().toLowerCase();
        applyTag(tag);
    }

    private void applyTag(String tag) {
        new addTag().execute(tag);
    }

    public static void start(Context context, Document document, String ip) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("document", document);
        intent.putExtra("ip", ip);

        context.startActivity(intent);
    }

    class scanTags extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            TessBaseAPI tessBaseAPI = new TessBaseAPI();
            tessBaseAPI.init(DATA_PATH, "eng");

            String totalResult = "";
            for (Page page : pages) {
                tessBaseAPI.setImage(page.getImage());
                totalResult += "\n" + tessBaseAPI.getUTF8Text();
            }
            tessBaseAPI.end();
            totalResult = totalResult.toLowerCase();

            Log.d("INFO", totalResult);

            // Build params
            List<NameValuePair> tagParams = new ArrayList<NameValuePair>();
            // Retrieve JSON
            JSONObject tagsJson = jParser.makeHttpRequest(urlAllTags, "POST", tagParams);

            try {
                // Check for success
                int tagSuccess = tagsJson.getInt("success");

                if (tagSuccess == 1) {
                    JSONArray tagJson = tagsJson.getJSONArray("tags");

                    ArrayList<String[]> allTags = new ArrayList<>();
                    // Loop
                    for (int i = 0; i < tagJson.length(); i++) {
                        JSONObject pageObject = tagJson.getJSONObject(i);
                        String[] tag = new String[2];
                        tag[0] = pageObject.getString("tagID");
                        tag[1] = pageObject.getString("tag");

                        allTags.add(tag);
                    }

                    ArrayList<String[]> foundTags = new ArrayList<>();
                    // Search for tags
                    for (String[] tag : allTags) {
                        if (totalResult.contains(tag[1])) {
                            foundTags.add(tag);
                        }
                    }

                    for (String[] tag : foundTags) {
                        Log.d("INFO", "Tag: " + tag[1] + " added.");
                        applyTag(tag[1]);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class addTag extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Build params
            List<NameValuePair> tagParams = new ArrayList<NameValuePair>();
            tagParams.add(new BasicNameValuePair("tag", params[0]));
            // Retrieve JSON
            JSONObject tagJson = jParser.makeHttpRequest(urlTag, "POST", tagParams);

            String[] tag = new String[2];

            try {
                // Check for Success
                int tagSuccess = tagJson.getInt("success");

                if (tagSuccess == 1) {
                    // Tag already exists
                    tag[0] = tagJson.getString("tagID");
                    tag[1] = params[0];
                } else {
                    // Create tag
                    tag[0] = "DEFAULT";
                    tag[1] = params[0];

                    List<NameValuePair> newTagParams = new ArrayList<NameValuePair>();
                    newTagParams.add(new BasicNameValuePair("tag", params[0]));
                    // Retrieve JSON
                    JSONObject newTag = jParser.makeHttpRequest(urlCreateTag, "POST", newTagParams);

                    // Check for success
                    int newTagSuccess = newTag.getInt("success");

                    if (newTagSuccess == 1) {
                        // Tag created
                        tag[0] = newTag.getString("LAST_INSERT_ID()");
                    }
                }

                // Apply tag
                // Build params
                List<NameValuePair> applyTagParams = new ArrayList<NameValuePair>();
                tagParams.add(new BasicNameValuePair("tagID", tag[0]));
                tagParams.add(new BasicNameValuePair("docID", doc.getID() + ""));
                // Retrieve JSON
                JSONObject applyTagJson = jParser.makeHttpRequest(urlApplyTag, "POST", tagParams);

                int applyTagSuccess = applyTagJson.getInt("success");

                if (applyTagSuccess == 1) {
                    // Tag applied
                    currentTags.add(tag);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tagAdapter.notifyDataSetChanged();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class retrievePages extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            pages = new ArrayList<>();
            // Build params
            List<NameValuePair> pageParams = new ArrayList<NameValuePair>();
            pageParams.add(new BasicNameValuePair("docID", doc.getID() + ""));
            // Retrieve JSON
            JSONObject pagesJson = jParser.makeHttpRequest(urlPages, "POST", pageParams);

            try {
                // Check for Success
                int pagesSuccess = pagesJson.getInt("success");

                if (pagesSuccess == 1) {
                    JSONArray pageJson = pagesJson.getJSONArray("pages");

                    // Loop
                    for (int i = 0; i < pageJson.length(); i++) {
                        JSONObject pageObject = pageJson.getJSONObject(i);
                        int id = pageObject.getInt("imageID");
                        byte[] image = (Base64.decode(pageObject.getString("image"), Base64.DEFAULT));
                        String encryptionKey = pageObject.getString("encryptionKey");
                        int pageNo = i + 1;

                        Page page = new Page(id, image, encryptionKey, pageNo);
                        pages.add(page);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pages.size() < 1) {
                        pages.add(new Page(-1, BitmapUtil.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.imageadd)), "", 1));
                    }
                    ViewPagerAdapter viewAdapter = new ViewPagerAdapter(getApplicationContext(), pages, false, doc.getID(), ip);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.imagePreview);
                    viewPager.setAdapter(viewAdapter);
                }
            });
        }
    }

    class retrieveTags extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> tagsParams = new ArrayList<NameValuePair>();
            tagsParams.add(new BasicNameValuePair("docID", doc.getID() + ""));
            // Retrieve JSON
            JSONObject tagsJson = jParser.makeHttpRequest(urlTags, "POST", tagsParams);

            Log.d("INFO", tagsJson.toString());
            try {
                // Check for Success
                int tagsSuccess = tagsJson.getInt("success");

                if (tagsSuccess == 1) {
                    JSONArray tagJson = tagsJson.getJSONArray("tags");

                    // Loop for each tag
                    for (int i = 0; i < tagJson.length(); i++) {
                        JSONObject tagObject = tagJson.getJSONObject(i);
                        String[] tag = new String[2];

                        tag[0] = tagObject.getString("tagID");
                        tag[1] = tagObject.getString("tag");

                        currentTags.add(tag);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {
            runOnUiThread(new Runnable() {
                public void run() {
                    tagAdapter = new TagAdapter(currentTags, doc.getID(), getApplicationContext(), ip);
                    ListView listView = (ListView) findViewById(R.id.tagsView);
                    listView.setAdapter(tagAdapter);
                }
            });
        }
    }
}
