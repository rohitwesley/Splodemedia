package com.tecrt.rowest.splodemedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton mCamButton = (ImageButton) findViewById(R.id.buttonCam);
        mCamButton.setOnClickListener(mOnClickListener);

        ImageButton mVideoButton = (ImageButton) findViewById(R.id.buttonMedia);
        mVideoButton.setOnClickListener(mOnClickListener);
        checkWriteStoragePermission();
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
                    if(checkCamera())startActivity(i);
                    break;
                case R.id.buttonMedia:
                    i = new Intent(MainActivity.this, VideoActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
            if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not have audio on record", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if(mIsRecording || mIsTimelapse) {
//                    mIsRecording = true;
//                    mRecordImageButton.setImageResource(R.mipmap.btn_video_busy);
//                }
                Toast.makeText(this,
                        "Permission successfully granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkCamera() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
//                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                return true;
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this,
                            "Video app required access to camera", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA_PERMISSION_RESULT);
                return false;
            }

        } else {
//                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            return true;
        }
    }

    private boolean checkWriteStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
                return false;
            }
        } else {
            return true;
        }
    }

}
