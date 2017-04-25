package com.untitled.mobiledocumentscanner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by J on 25-Apr-17.
 */

public class TagAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String[]> tags;
    private int docID;
    private Context context;

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
                try (Connection conn = DataSource.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM TagApplication WHERE tagID = ? AND docID = ?");

                    ps.setString(1, tags.get(position)[0]);
                    ps.setInt(2, docID);
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                tags.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
