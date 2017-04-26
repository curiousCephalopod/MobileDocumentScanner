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

public class ImageActivity extends AppCompatActivity {
    ArrayList<Page> pages;
    int docID;
    ViewPager viewPager;
    String ip;

    ViewPagerAdapter viewAdapter;
    JSONParser jParser = new JSONParser();

    private String urlRemovePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle bundle = getIntent().getExtras();
        this.pages = (ArrayList<Page>) bundle.get("pages");
        this.docID = bundle.getInt("docID");
        this.ip = bundle.getString("ip");

        urlRemovePage = "http://" + ip + "/DocumentScanner/remove_page.php";


        viewAdapter = new ViewPagerAdapter(getApplicationContext(), pages, true, docID, ip);
        viewPager = (ViewPager) findViewById(R.id.largeImagePreview);
        viewPager.setAdapter(viewAdapter);
    }

    public void addPage(View v) {
        CameraActivity.start(getApplicationContext(), docID, pages.size() + 1, ip);
    }

    public void deletePage(View v) {
        new deletePage().execute((viewPager.getCurrentItem() + 1) + "");
    }

    public static void start(Context context, ArrayList<Page> pages, int docID, String ip) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("pages", pages);
        intent.putExtra("docID", docID);
        intent.putExtra("ip", ip);

        context.startActivity(intent);
    }

    class deletePage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Build params
            List<NameValuePair> pageParams = new ArrayList<NameValuePair>();
            pageParams.add(new BasicNameValuePair("docID", docID + ""));
            pageParams.add(new BasicNameValuePair("pageNo", params[0]));
            // Retrieve JSON
            JSONObject pageJson = jParser.makeHttpRequest(urlRemovePage, "POST", pageParams);

            Log.d("INFO", pageParams.toString());
            Log.d("INFO", pageJson.toString());
            try {
                // Check for Success
                int tagSuccess = pageJson.getInt("success");

                if (tagSuccess == 1) {
                    pages.remove(Integer.parseInt(params[0]) - 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
