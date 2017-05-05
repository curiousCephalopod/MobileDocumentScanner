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
    // URLs to access PHP scripts
    private String urlDocuments;
    private String urlCover;

    // IP address of server
    private String ip;

    /**
     * Initialise the activity using parameters.
     * @param context Application context
     * @param ip Server address
     */
    public static void start(Context context, String ip) {
        // Create a new intent and store the parameters
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("ip", ip);

        // Start the new activity
        context.startActivity(intent);
    }

    /**
     * Initialise variables and start ASyncTasks.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Initialise the parser
        jParser = new JSONParser();

        // Retrieve the IP from the bundle
        Bundle bundle = getIntent().getExtras();
        //ip = bundle.getString("ip");
        ip = "lrhvbzvdud.localtunnel.me";

        // Create the URLs from the IP
        urlDocuments = "http://" + ip + "/DocumentScanner/retrieve_documents.php";
        urlCover = "http://" + ip + "/DocumentScanner/retrieve_cover.php";

        // Retrieve cover images
        new retrieveImages().execute();
    }

    /**
     * On resume, update the gallery
     */
    @Override
    public void onResume() {
        super.onResume();

        new retrieveImages().execute();
    }

    /**
     * ASyncTask to retrieve images off the main thread.
     */
    class retrieveImages extends AsyncTask<String, String, String> {
        // Temporary document list
        ArrayList<Document> temp;

        /**
         * Perform the HTTP request in the background.
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {
            // Initialise the array
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
                        // Retrieve the document's properties
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
                            // Decode the cover from a string to a byte array
                            cover = Base64.decode(coverJson.getString("image"), Base64.DEFAULT);
                        } else {
                            // Overwise set a placeholder
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

        /**
         * After execution, set up the adapters and views
         * @param fileURL
         */
        protected void onPostExecute(String fileURL) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // If there is no documents, add a placeholder
                    temp.add(new Document(-1, "New Doc", "", 0, BitmapUtil.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.imageadd))));
                    documents = temp;

                    // Set up the views
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
