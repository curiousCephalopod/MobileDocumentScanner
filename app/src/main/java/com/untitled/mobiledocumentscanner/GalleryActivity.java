package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
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

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<Document> documents;
    private int userID = 1;
    JSONParser jParser = new JSONParser();

    private static String urlDocuments = "http://10.0.2.2/DocumentScanner/retrieve_documents.php";
    private static String urlCover = "http://10.0.2.2/DocumentScanner/retrieve_cover.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        documents = new ArrayList<>();

        new retrieveImages().execute();
    }

    class retrieveImages extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
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

                        Bitmap cover;
                        // Retrieve cover
                        List<NameValuePair> coverParams = new ArrayList<NameValuePair>();
                        coverParams.add(new BasicNameValuePair("docID", id + ""));
                        // Retrieve JSON
                        JSONObject coverJson = jParser.makeHttpRequest(urlCover, "POST", coverParams);

                        // Check for Success
                        int coverSuccess = coverJson.getInt("success");
                        if (coverSuccess == 1) {

                            cover = BitmapUtil.getImage(Base64.decode(coverJson.getString("image"), Base64.DEFAULT));
                        } else {
                            cover = BitmapFactory.decodeResource(getResources(), R.drawable.imagepreview);
                        }

                        // Create and add document
                        Document doc = new Document(id, title, date, pages, cover);
                        documents.add(doc);
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
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
                    recyclerView.setHasFixedSize(true);

                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recyclerView.setLayoutManager(layoutManager);

                    GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), documents);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }
}
