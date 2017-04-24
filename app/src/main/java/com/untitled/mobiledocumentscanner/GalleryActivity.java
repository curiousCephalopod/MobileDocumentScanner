package com.untitled.mobiledocumentscanner;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private Document documents[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<CreateList> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }

    private void retrieveImages(){
        //retrieve document titles

    }

    private ArrayList<CreateList> prepareData() {
        ArrayList<CreateList> theimage = new ArrayList<>();
        for(int i = 0; i < documents.length; i++){
            CreateList createList = new CreateList();
            createList.setImageTitle(documents[i].getTitle());
            createList.setImage(documents[i].getCover());
            theimage.add(createList);
        }
        return theimage;
    }
}
