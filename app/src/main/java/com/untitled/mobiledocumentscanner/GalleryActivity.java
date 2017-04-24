package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<Document> documents;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        documents = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<CreateList> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void retrieveImages(){

        try(Connection conn = DataSource.getConnection()){
            PreparedStatement ps = conn.prepareStatement("STATEMENT");
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String title = rs.getString("title");
                Date date = rs.getDate("date");
                int pages = 0; // get pages
                Bitmap cover = BitmapFactory.decodeResource(getResources(), R.drawable.imagepreview); // get cover

                Document document = new Document(id, title, date, pages, cover);
                documents.add(document);
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
            createList.setImage(documents.get(i).getCover());
            theimage.add(createList);
        }
        return theimage;
    }
}
