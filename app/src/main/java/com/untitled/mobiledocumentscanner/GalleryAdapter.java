package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Josh on 12-Apr-17.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private ArrayList<Document> galleryList;
    private Context context;
    private String userID;
    private String ip;

    private String urlCreateDoc;

    public GalleryAdapter(Context context, String userID, ArrayList<Document> galleryList, String ip) {
        this.galleryList = galleryList;
        this.userID = userID;
        this.context = context;
        this.ip = ip;
        urlCreateDoc = "http://" + ip + "/DocumentScanner/create_document.php";
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.title.setText(galleryList.get(i).getTitle());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageBitmap(galleryList.get(i).getCover());
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryList.get(i).getID() != -1) {
                    // Open Details with document
                    DetailActivity.start(context, galleryList.get(i), ip);
                } else {
                    // Create new document
                    final EditText textDocTitle = new EditText(context);
                    textDocTitle.setHint("Document Title");

                    new AlertDialog.Builder(context, R.style.dialogTheme)
                            .setTitle("Create new Document")
                            .setMessage("Please enter a title.")
                            .setView(textDocTitle)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String docTitle = textDocTitle.getText().toString();
                                    new addDocument().execute(docTitle, i + "");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }

    class addDocument extends AsyncTask<String, String, String> {
        int i;

        @Override
        protected String doInBackground(String... params) {
            i = Integer.parseInt(params[1]);
            JSONParser jParser = new JSONParser();
            List<NameValuePair> newTagParams = new ArrayList<NameValuePair>();
            newTagParams.add(new BasicNameValuePair("docTitle", params[0]));
            newTagParams.add(new BasicNameValuePair("userID", userID));
            // Retrieve JSON
            JSONObject newDoc = jParser.makeHttpRequest(urlCreateDoc, "POST", newTagParams);
            Log.d("INFO", newDoc.toString());

            try {
                // Check for success
                int newTagSuccess = 0;
                newTagSuccess = newDoc.getInt("success");

                if (newTagSuccess == 1) {
                    int docID = newDoc.getInt("docID");
                    galleryList.get(i).setID(docID);
                    galleryList.get(i).setTitle(params[0]);
                    String date = DateFormat.getDateInstance().format(new Date());
                    galleryList.get(i).setDate(date);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String fileURL) {
            DetailActivity.start(context, galleryList.get(i), ip);
        }
    }
}