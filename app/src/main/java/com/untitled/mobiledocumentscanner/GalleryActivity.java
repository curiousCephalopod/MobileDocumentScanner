package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Retrieves and displays all documents.
 */
public class GalleryActivity extends AppCompatActivity {
    // Parser to read JSON
    private JSONParser jParser;
    // List of retrieved documents
    private ArrayList<Document> documents;
    // Default user ID, in leu of login
    private int userID = 1;
    // URLs
    private String urlDocuments;
    private String urlCover;

    private String ip;

    public static void start(Context context, String ip) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("ip", ip);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        jParser = new JSONParser();

        Bundle bundle = getIntent().getExtras();
        //ip = bundle.getString("ip");
        ip = "lrhvbzvdud.localtunnel.me";

        urlDocuments = "http://" + ip + "/DocumentScanner/retrieve_documents.php";
        urlCover = "http://" + ip + "/DocumentScanner/retrieve_cover.php";

        new retrieveImages().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        new retrieveImages().execute();
    }

    class retrieveImages extends AsyncTask<String, String, String> {
        ArrayList<Document> temp;

        @Override
        protected String doInBackground(String... args) {
            temp = new ArrayList<>();
            // Building Parameters
            List<NameValuePair> docParams = new ArrayList<NameValuePair>();
            docParams.add(new BasicNameValuePair("userID", userID + ""));
            // Retrieve JSON
            JSONObject docJson = jParser.makeHttpRequest(urlDocuments, "POST", docParams);

            try {
                // Check for Success
                int docSuccess = docJson.getInt("success");

                if (docSuccess == 1) {
                    // Documents
                    JSONArray documentsJSON = docJson.getJSONArray("documents");

                    // Loop for each doc
                    for (int i = 0; i < documentsJSON.length(); i++) {
                        JSONObject docObject = documentsJSON.getJSONObject(i);
                        int id = docObject.getInt("docID");
                        String title = docObject.getString("docTitle");
                        String date = docObject.getString("dateCreated");
                        int pages = docObject.getInt("noPages");

                        byte[] cover;
                        // Retrieve cover
                        List<NameValuePair> coverParams = new ArrayList<NameValuePair>();
                        coverParams.add(new BasicNameValuePair("docID", id + ""));
                        // Retrieve JSON
                        JSONObject coverJson = jParser.makeHttpRequest(urlCover, "POST", coverParams);

                        // Check for Success
                        int coverSuccess = coverJson.getInt("success");
                        if (coverSuccess == 1) {
                            cover = Base64.decode(coverJson.getString("image"), Base64.DEFAULT);
                        } else {
                            cover = BitmapUtil.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.imagepreview));
                        }

                        // Create and add document
                        Document doc = new Document(id, title, date, pages, cover);
                        temp.add(doc);
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
                    temp.add(new Document(-1, "New Doc", "", 0, BitmapUtil.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.imageadd))));
                    documents = temp;

                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
                    recyclerView.setHasFixedSize(true);

                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recyclerView.setLayoutManager(layoutManager);

                    GalleryAdapter adapter = new GalleryAdapter(GalleryActivity.this, userID + "", documents, ip);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }
}
