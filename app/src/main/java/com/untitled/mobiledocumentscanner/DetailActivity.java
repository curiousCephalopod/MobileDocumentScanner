package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity{
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Document doc;
    ArrayList<Bitmap> images;
    ArrayList<String[]> tags;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        ViewPagerAdapter viewAdapter = new ViewPagerAdapter(this, images);
        viewPager.setAdapter(viewAdapter);

        retrieveTags();
        TagAdapter tagAdapter = new TagAdapter(tags, doc.getID(), this);
        ListView listView = (ListView) findViewById(R.id.tagsView);
        listView.setAdapter(tagAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void retrieveTags() {
        tags = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            PreparedStatement findTags = conn.prepareStatement("SELECT t.tagID, t.tag FROM Tag t, TagApplication a " +
                    "WHERE t.tagID = a.tagID AND a.docID = ?" +
                    "GROUP BY t.tagID");

            findTags.setInt(1, doc.getID());
            ResultSet rs = findTags.executeQuery();
            while (rs.next()) {
                String[] tag = new String[2];
                tag[0] = rs.getInt("tagID") + "";
                tag[1] = rs.getString("tag");

                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addTag(View v) {
        EditText newTag = (EditText) findViewById(R.id.newTag);
        String tag = newTag.getText().toString().toLowerCase();
        int tagID;

        try (Connection conn = DataSource.getConnection()) {
            PreparedStatement findTag = conn.prepareStatement("SELECT * FROM Tag WHERE tag = ?");

            findTag.setString(1, tag);
            ResultSet rs = findTag.executeQuery();
            if (rs.next())
                // Tag exists
                tagID = rs.getInt("tagID");
            else {
                // Create tag
                PreparedStatement createTag = conn.prepareStatement("INSERT INTO Tag VALUES (DEFAULT, ?)");

                createTag.setString(1, tag);
                Statement st = conn.createStatement();
                ResultSet rsID = st.executeQuery("SELECT LAST_INSERT_ID()");
                rsID.next();
                tagID = rsID.getInt("LAST_INSERT_ID()");
                createTag.close();
            }
            findTag.close();
            // Apply tag

            PreparedStatement applyTag = conn.prepareStatement("INSERT INTO TagApplication VALUES (?, ?)");

            applyTag.setInt(1, tagID);
            applyTag.setInt(2, doc.getID());
            applyTag.executeUpdate();
            applyTag.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update tag list
    }

    public static void start(Context context, Document document){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("document", document);

        context.startActivity(intent);
    }
}
