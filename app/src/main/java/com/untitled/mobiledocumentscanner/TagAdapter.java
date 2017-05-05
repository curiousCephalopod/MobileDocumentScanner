package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class designed and implemented by Joshua (eeu67d).
 * Manages the list of tags.
 */

public class TagAdapter extends BaseAdapter implements ListAdapter {
    // List of tags
    private ArrayList<String[]> tags;
    // Document ID
    private int docID;
    // Application context
    private Context context;
    // Parse JSON object
    private JSONParser jParser;
    // IP address of the server
    private String ip;

    // URL to access PHP script
    private String urlTags;

    /**
     * Retrieve parameters and initialise parser.
     * @param tags List of tags
     * @param docID Document ID
     * @param context Application Context
     * @param ip IP of server
     */
    public TagAdapter(ArrayList<String[]> tags, int docID, Context context, String ip) {
        this.tags = tags;
        this.docID = docID;
        this.context = context;
        this.ip = ip;
        // Build the URL
        urlTags = "http://" + ip + "/DocumentScanner/remove_tag.php";
        // Initialise the parser
        jParser = new JSONParser();
    }

    /**
     * Retrieve count of displayed tags.
     * @return
     */
    @Override
    public int getCount() {
        return tags.size();
    }

    /**
     * Return a single tag.
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    /**
     * Retrieves an item ID
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Retrieves a view.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tag_cell, null);
        }

        // Set the tag text
        TextView tagText = (TextView) view.findViewById(R.id.tagString);
        tagText.setText(tags.get(position)[1]);

        // Set up the delete button
        Button tagDelete = (Button) view.findViewById(R.id.tagDelete);
        tagDelete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                new removeTag().execute(position + "");
            }
        });

        return view;
    }

    /**
     * ASyncTask to remove a tag off the main thread,
     */
    class removeTag extends AsyncTask<String, String, String> {

        /**
         * Perform task in background.
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> tagParams = new ArrayList<NameValuePair>();
            tagParams.add(new BasicNameValuePair("docID", docID + ""));
            tagParams.add(new BasicNameValuePair("tagID", tags.get(Integer.parseInt(args[0]))[0]));
            // Retrieve JSON
            JSONObject tagsJson = jParser.makeHttpRequest(urlTags, "POST", tagParams);

            Log.d("INFO", tagsJson.toString());
            try {
                // Check for Success
                int tagsSuccess = tagsJson.getInt("success");

                // If the tag is removed from the database, remove it from the list
                if (tagsSuccess == 1) {
                    tags.remove(Integer.parseInt(args[0]));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After execution, reset the list
         * @param fileURL
         */
        protected void onPostExecute(String fileURL) {
            notifyDataSetChanged();
        }
    }
}
