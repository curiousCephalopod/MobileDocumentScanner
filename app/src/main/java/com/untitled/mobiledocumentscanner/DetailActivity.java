package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Displays the details of a single document, and allows modification.
 */
public class DetailActivity extends AppCompatActivity{
    // Tesseract API parameters
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";
    private static final int PERMISSION_REQUEST_CODE = 1;
    // Structure to hold the document
    private Document doc;
    // List of pages in the document
    private ArrayList<Page> pages;
    // List of tags in the document
    private ArrayList<String[]> currentTags;
    // Parser object to read JSON response
    private JSONParser jParser;
    // Tag adapter to control tag display
    private TagAdapter tagAdapter;
    // IP address of the server
    private String ip;
    // URLs to access PHP scripts
    private String urlTags;
    private String urlTag;
    private String urlCreateTag;
    private String urlApplyTag;
    private String urlPages;
    private String urlAllTags;

    /**
     * Start the activity and retrieve parameters
     *
     * @param context  Application context
     * @param document Document to view
     * @param ip       Server address
     */
    public static void start(Context context, Document document, String ip) {
        // Set up an intent and pass the variables
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("document", document);
        intent.putExtra("ip", ip);

        // Start the activity
        context.startActivity(intent);
    }

    /**
     * Initialise parameters from the bundle and set up views.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialise the parser
        jParser = new JSONParser();
        // Retrieve variables from the stored bundle
        Bundle bundle = getIntent().getExtras();
        this.doc = (Document) bundle.get("document");
        ip = bundle.getString("ip");

        // Initialise the PHP URLs
        urlTags = "http://" + ip + "/DocumentScanner/find_tags.php";
        urlTag = "http://" + ip + "/DocumentScanner/find_tag.php";
        urlCreateTag = "http://" + ip + "/DocumentScanner/create_tag.php";
        urlApplyTag = "http://" + ip + "/DocumentScanner/apply_tag.php";
        urlPages = "http://" + ip + "/DocumentScanner/retrieve_pages.php";
        urlAllTags = "http://" + ip + "/DocumentScanner/find_all_tags.php";

        // Request permission to access storage
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        // Display the document title
        TextView viewTitle = (TextView)findViewById(R.id.imageName);
        viewTitle.setText(doc.getTitle());

        // Display the date of creation
        TextView viewDate = (TextView)findViewById(R.id.imageDate);
        viewDate.setText(doc.getDate());

        // Display the number of pages
        TextView viewPages = (TextView)findViewById(R.id.imagePages);
        viewPages.setText(doc.getPages() + " pages");

        // Retrieve the document tags
        currentTags = new ArrayList<>();
        new retrieveTags().execute();
        // Retrieve the document page
        new retrievePages().execute();
    }

    /**
     * If the activity is resumed, update the pages
     */
    @Override
    public void onResume() {
        super.onResume();

        new retrievePages().execute();
    }

    /**
     * Check if we have permission to access the storage.
     * @return
     */
    private boolean checkPermission() {
        // Check for write permissions
        int result = ContextCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Request permission to write to the storage.
     */
    private void requestPermission() {
        // Request permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DetailActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback for the permission request.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Check for permissions
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

    /**
     * Prepare the tesseract file storage.
     */
    private void prepareTesseract() {
        // Create a new directory if it doesn't exist already
        File dir = new File(DATA_PATH + TESSDATA);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("INFO", "ERROR: Creation of directory " + DATA_PATH + TESSDATA + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i("INFO", "Created directory " + DATA_PATH + TESSDATA);
        }

        try {
            // Retrieve the tesseract assets
            String fileList[] = getAssets().list(TESSDATA);

            // For each asset
            for (String fileName : fileList) {
                String pathToDataFile = DATA_PATH + TESSDATA + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(TESSDATA + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer it to the storage
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

    /**
     * Prepare tesseract and scan for tags.
     * @param v
     */
    public void scanTags(View v) {
        if (checkPermission()) {
            // Prepare the file storage
            prepareTesseract();

            // Scan for tags
            new scanTags().execute();
        }
    }

    /**
     * Callback for add tag button.
     * @param v
     */
    public void addTag(View v) {
        // Retrieve the edit text
        EditText newTag = (EditText) findViewById(R.id.newTag);
        // Lowercase the input
        String tag = newTag.getText().toString().toLowerCase();
        // Add it to the tag list
        applyTag(tag);
    }

    /**
     * Executes an ASyncTask to add a tag.
     * @param tag
     */
    private void applyTag(String tag) {
        new addTag().execute(tag);
    }

    /**
     * ASyncTask to scan for tags and add them.
     */
    class scanTags extends AsyncTask<String, String, String> {

        /**
         * Perform the scan in the background.
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            // Initialise the Tesseract API
            TessBaseAPI tessBaseAPI = new TessBaseAPI();
            tessBaseAPI.init(DATA_PATH, "eng");

            // Scan the pages for text
            String totalResult = "";
            for (Page page : pages) {
                tessBaseAPI.setImage(page.getImage());
                totalResult += "\n" + tessBaseAPI.getUTF8Text();
            }
            tessBaseAPI.end();
            // Convert the text to lowercase
            totalResult = totalResult.toLowerCase();

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
                    // Loop for each tag
                    for (int i = 0; i < tagJson.length(); i++) {
                        // Retrieve the tag properties
                        JSONObject pageObject = tagJson.getJSONObject(i);
                        String[] tag = new String[2];
                        tag[0] = pageObject.getString("tagID");
                        tag[1] = pageObject.getString("tag");

                        // Add the tag
                        allTags.add(tag);
                    }

                    ArrayList<String[]> foundTags = new ArrayList<>();
                    // Search for tags
                    for (String[] tag : allTags) {
                        // If the tag is found, add it
                        if (totalResult.contains(tag[1])) {
                            foundTags.add(tag);
                        }
                    }

                    // Apply all found tags
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

    /**
     * ASyncTask to add a new tag off the main thread.
     */
    class addTag extends AsyncTask<String, String, String> {

        /**
         * Do the task in the background.
         * @param params
         * @return
         */
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

    /**
     * ASyncTask to retrieve the pages off the main thread.
     */
    class retrievePages extends AsyncTask<String, String, String> {

        /**
         * Do the task in the background.
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            // Reset the pages
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

                    // Loop for each page
                    for (int i = 0; i < pageJson.length(); i++) {
                        JSONObject pageObject = pageJson.getJSONObject(i);
                        // Retrieve the page properties
                        int id = pageObject.getInt("imageID");
                        byte[] image = Base64.decode(pageObject.getString("image"), Base64.DEFAULT);
                        String encryptionKey = pageObject.getString("encryptionKey");
                        int pageNo = i + 1;

                        // Create and add the page
                        Page page = new Page(id, image, encryptionKey, pageNo);
                        pages.add(page);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After execution, set up the view.
         * @param fileURL
         */
        protected void onPostExecute(String fileURL) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // If there is no pages, add a placeholder
                    if (pages.size() < 1) {
                        pages.add(new Page(-1, BitmapUtil.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.imageadd)), "", 1));
                    }
                    // Add the adapter to the view
                    ViewPagerAdapter viewAdapter = new ViewPagerAdapter(getApplicationContext(), pages, false, doc.getID(), ip);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.imagePreview);
                    viewPager.setAdapter(viewAdapter);
                }
            });
        }
    }

    /**
     * Retrieve tags of a document.
     */
    class retrieveTags extends AsyncTask<String, String, String> {

        /**
         * Do the task in the background
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> tagsParams = new ArrayList<NameValuePair>();
            tagsParams.add(new BasicNameValuePair("docID", doc.getID() + ""));
            // Retrieve JSON
            JSONObject tagsJson = jParser.makeHttpRequest(urlTags, "POST", tagsParams);

            try {
                // Check for Success
                int tagsSuccess = tagsJson.getInt("success");

                if (tagsSuccess == 1) {
                    JSONArray tagJson = tagsJson.getJSONArray("tags");

                    // Loop for each tag
                    for (int i = 0; i < tagJson.length(); i++) {
                        JSONObject tagObject = tagJson.getJSONObject(i);
                        String[] tag = new String[2];

                        // Store the tag details
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

        /**
         * After Execution, set up the tag adapter
         * @param fileURL
         */
        protected void onPostExecute(String fileURL) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // Initialise the tag adapter
                    tagAdapter = new TagAdapter(currentTags, doc.getID(), getApplicationContext(), ip);
                    ListView listView = (ListView) findViewById(R.id.tagsView);
                    listView.setAdapter(tagAdapter);
                }
            });
        }
    }
}
