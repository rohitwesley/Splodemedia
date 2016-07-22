package com.tecrt.rowest.splodemedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton mCamButton = (ImageButton) findViewById(R.id.buttonCam);
        mCamButton.setOnClickListener(mOnClickListener);

        ImageButton mVideoButton = (ImageButton) findViewById(R.id.buttonMedia);
        mVideoButton.setOnClickListener(mOnClickListener);

    }

    /**
     * method when touch record button
     */
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Intent i;
            switch (view.getId()) {
                case R.id.buttonCam:
                    i = new Intent(MainActivity.this, CamActivity.class);
                    startActivity(i);
                    break;
                case R.id.buttonMedia:
                    i = new Intent(MainActivity.this, VideoActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

}
