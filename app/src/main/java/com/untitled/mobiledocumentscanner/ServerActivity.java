package com.untitled.mobiledocumentscanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }

    public void serverSubmit(View v) {
        EditText text = (EditText) findViewById(R.id.serverText);
        String server = text.getText().toString();

        GalleryActivity.start(getApplicationContext(), server);
    }
}
