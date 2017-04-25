package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity{
    private Document doc;
    ArrayList<Page> pages;
    ArrayList<String[]> tags;
    JSONParser jParser = new JSONParser();
    TagAdapter tagAdapter;

    private static String urlTags = "http://10.0.2.2/DocumentScanner/find_tags.php";
    private static String urlTag = "http://10.0.2.2/DocumentScanner/find_tag.php";
    private static String urlCreateTag = "http://10.0.2.2/DocumentScanner/create_tag.php";
    private static String urlApplyTag = "http://10.0.2.2/DocumentScanner/apply_tag.php";
    private static String urlPages = "http://10.0.2.2/DocumentScanner/retrieve_pages.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        this.doc = (Document)bundle.get("document");

        TextView viewTitle = (TextView)findViewById(R.id.imageName);
        viewTitle.setText(doc.getTitle());

        TextView viewDate = (TextView)findViewById(R.id.imageDate);
        viewDate.setText(doc.getDate());

        TextView viewPages = (TextView)findViewById(R.id.imagePages);
        viewPages.setText(doc.getPages() + " pages");

        tags = new ArrayList<>();
        new retrieveTags().execute();

        new retrievePages().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        new retrievePages().execute();
    }

    public void addTag(View v) {
        EditText newTag = (EditText) findViewById(R.id.newTag);
        String tag = newTag.getText().toString().toLowerCase();
        new addTag().execute(tag);
    }

    public static void start(Context context, Document document) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("document", document);

        context.startActivity(intent);
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
                    tags.add(tag);
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
                        Bitmap image = BitmapUtil.getImage(Base64.decode(pageObject.getString("image"), Base64.DEFAULT));
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
                    ViewPagerAdapter viewAdapter = new ViewPagerAdapter(getApplicationContext(), pages, false, doc.getID());
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

                        tags.add(tag);
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
                    tagAdapter = new TagAdapter(tags, doc.getID(), getApplicationContext());
                    ListView listView = (ListView) findViewById(R.id.tagsView);
                    listView.setAdapter(tagAdapter);
                }
            });
        }
    }
}
