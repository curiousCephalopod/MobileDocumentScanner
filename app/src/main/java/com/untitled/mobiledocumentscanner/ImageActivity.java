package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Displays a large form of the page image.
 */
public class ImageActivity extends AppCompatActivity {
    // List of pages in this document
    private ArrayList<Page> pages;
    // ID of this document
    private int docID;
    // ViewPager to display the images
    private ViewPager viewPager;
    // IP address of the server
    private String ip;

    // Adapter to display the images
    private ViewPagerAdapter viewAdapter;
    // Parser for JSON
    private JSONParser jParser = new JSONParser();

    // URL to access PHP
    private String urlRemovePage;

    /**
     * Retrieve the parameters and set up the view,
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Retrieve the bundle and variables
        Bundle bundle = getIntent().getExtras();
        this.pages = (ArrayList<Page>) bundle.get("pages");
        this.docID = bundle.getInt("docID");
        this.ip = bundle.getString("ip");

        // Build the URL
        urlRemovePage = "http://" + ip + "/DocumentScanner/remove_page.php";

        // Set up the adapter
        viewAdapter = new ViewPagerAdapter(getApplicationContext(), pages, true, docID, ip);
        viewPager = (ViewPager) findViewById(R.id.largeImagePreview);
        viewPager.setAdapter(viewAdapter);
    }

    /**
     * Start the camera activity to add a new page.
     * @param v
     */
    public void addPage(View v) {
        CameraActivity.start(getApplicationContext(), docID, pages.size() + 1, ip);
    }

    /**
     * Start a task to delete a page
     * @param v
     */
    public void deletePage(View v) {
        new deletePage().execute((viewPager.getCurrentItem() + 1) + "");
    }

    /**
     * Start the activity and store parameter.
     * @param context Application context
     * @param pages List of pages
     * @param docID Document ID
     * @param ip Server IP
     */
    public static void start(Context context, ArrayList<Page> pages, int docID, String ip) {
        // Start a new intent and store the parameters
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("pages", pages);
        intent.putExtra("docID", docID);
        intent.putExtra("ip", ip);

        // Start the new activity
        context.startActivity(intent);
    }

    /**
     * ASyncTask to delete a page off the main thread,
     */
    class deletePage extends AsyncTask<String, String, String> {

        /**
         * Perform in the background
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            // Build params
            List<NameValuePair> pageParams = new ArrayList<NameValuePair>();
            pageParams.add(new BasicNameValuePair("docID", docID + ""));
            pageParams.add(new BasicNameValuePair("pageNo", params[0]));
            // Retrieve JSON
            JSONObject pageJson = jParser.makeHttpRequest(urlRemovePage, "POST", pageParams);

            try {
                // Check for Success
                int tagSuccess = pageJson.getInt("success");

                if (tagSuccess == 1) {
                    pages.remove(Integer.parseInt(params[0]) - 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Reset the adapter
                            viewPager.setAdapter(viewAdapter);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
