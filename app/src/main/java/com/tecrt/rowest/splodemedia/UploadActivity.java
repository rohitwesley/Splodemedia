package com.tecrt.rowest.splodemedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("RecordAddress");
        TextView textbox = (TextView)findViewById(R.id.videoAddr);
        textbox.setText(msg);
    }
}
