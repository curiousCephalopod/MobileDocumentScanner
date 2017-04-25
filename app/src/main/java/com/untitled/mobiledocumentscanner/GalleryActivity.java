package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<Document> documents;
    private int userID = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        documents = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        Log.d("CREATION", "Retrieving images");
        retrieveImages();
        ArrayList<CreateList> createLists = prepareData();
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void retrieveImages(){
        try(Connection conn = DataSource.getConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT docID, docTitle, noPages, dateCreated FROM Document WHERE userID = ?");
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){

                // Retrieve document properties
                int id = rs.getInt("docID");
                String title = rs.getString("docTitle");
                Date date = rs.getDate("dateCreated");
                int pages = rs.getInt("noPages");

                Log.d("INFO", id + " " + title + " " + pages);

                // Retrieve cover image
                PreparedStatement coverSt = conn.prepareStatement("SELECT imageID, image, EncryptionKey FROM ImageStore"
                        + "INNER JOIN ImagePage ON ImagePage.imageID = ImageStore.imageID"
                        + "WHERE docID = ? AND pageNo = 1");

                coverSt.setInt(1, id);
                rs = coverSt.executeQuery();

                Blob blob = rs.getBlob("image");
                Bitmap cover = BitmapUtil.getImage(blob.getBytes(1, (int) blob.length()));

                // Create and add document
                Document doc = new Document(id, title, date, pages, cover);
                documents.add(doc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<CreateList> prepareData() {
        ArrayList<CreateList> theimage = new ArrayList<>();
        for(int i = 0; i < documents.size(); i++){
            CreateList createList = new CreateList();
            createList.setImageTitle(documents.get(i).getTitle());
            Bitmap cover = documents.get(i).getCover();
            if (cover == null)
                createList.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.imagepreview));
            else
                createList.setImage(documents.get(i).getCover());
            theimage.add(createList);
        }
        return theimage;
    }
}
