package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity{
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Document doc;
    ArrayList<Bitmap> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        this.doc = (Document)bundle.get("document");

        TextView viewTitle = (TextView)findViewById(R.id.imageName);
        viewTitle.setText(doc.getTitle());

        TextView viewDate = (TextView)findViewById(R.id.imageDate);
        viewDate.setText(dateFormat.format(doc.getDate()));

        TextView viewPages = (TextView)findViewById(R.id.imagePages);
        viewPages.setText(doc.getPages() + " pages");

        this.images = doc.populatePages();
        ViewPager viewPager = (ViewPager)findViewById(R.id.imagePreview);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
        viewPager.setAdapter(adapter);
    }

    public static void start(Context context, Document document){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("document", document);

        context.startActivity(intent);
    }
}
