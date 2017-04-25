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
 * Created by J on 25-Apr-17.
 */

public class TagAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String[]> tags;
    private int docID;
    private Context context;
    JSONParser jParser = new JSONParser();

    private static String urlTags = "http://10.0.2.2/DocumentScanner/remove_tag.php";

    public TagAdapter(ArrayList<String[]> tags, int docID, Context context) {
        this.tags = tags;
        this.docID = docID;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tag_cell, null);
        }

        TextView tagText = (TextView) view.findViewById(R.id.tagString);
        tagText.setText(tags.get(position)[1]);

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

    class removeTag extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> tagParams = new ArrayList<NameValuePair>();
            tagParams.add(new BasicNameValuePair("docID", docID + ""));

            tagParams.add(new BasicNameValuePair("tagID", tags.get(Integer.parseInt(args[0]))[0]));
            Log.d("INFO", args[0]);
            // Retrieve JSON
            JSONObject tagsJson = jParser.makeHttpRequest(urlTags, "POST", tagParams);

            Log.d("INFO", tagsJson.toString());
            try {
                // Check for Success
                int tagsSuccess = tagsJson.getInt("success");

                if (tagsSuccess == 1) {
                    tags.remove(Integer.parseInt(args[0]));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {
            notifyDataSetChanged();
        }
    }
}
