package com.tecrt.rowest.splodemedia;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tecrt.rowest.tecrtmedialib.MiscUtils;
import com.tecrt.rowest.tecrtmedialib.VideoGLView;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaAudioEncoder;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaEncoder;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaMuxerWrapper;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaVideoEncoder;
import com.tecrt.rowest.tecrtmedialib.tecrtData;

import java.io.File;
import java.io.IOException;

public class VideoActivity extends AppCompatActivity implements OnItemSelectedListener{

    private static final boolean DEBUG = false;	// TODO set false on release
    private static final String TAG = "CameraFragment";

    /**
     * for camera preview display
     */
    private VideoGLView mVideoView;
    /**
     * for scale mode display
     */
    private TextView mScaleModeView;
    /**
     * muxer for audio/video recording
     */
    private MediaMuxerWrapper mMuxer;
    // Common observer for all View Buttons
    private final ViewObserver mView_OnClickListener = new ViewObserver();
    // Common observer for all Adjustment Buttons
    private final AdjustmentObserver mAdjustment_OnClickListener = new AdjustmentObserver();
    // Common observer for all Filters Buttons
    private final FiltersObserver mFilters_OnClickListener = new FiltersObserver();
    // Common observer for all SeekBars.
    private final SeekBarObserver mObserverSeekBar = new SeekBarObserver();
    // Application shared preferences instance.
    private SharedPreferences mPreferences;
    // Shared data instance.
    private tecrtData mSharedData = new tecrtData();
    File mVideoFolder;
    File mVideoFile;
    private ArrayAdapter<String> mMovieFiles;
    private int mSelectedMovie;

    /**
     * update for shared data.
     */
    public void updateSharedData(tecrtData sharedData) {
        mVideoView.updateSharedData(sharedData);
    }

    public VideoActivity() {
        // need default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Typeface face= Typeface.createFromAsset(getAssets(), "font/Gotham-Medium.ttf");
        TextView tv1=(TextView)findViewById(R.id.text_none);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_none);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_bnw);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Ansel);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Sepia);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Retro);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Georgia);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Sahara);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_Polaroid);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_VHS);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.text_CRTtv);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.textView1);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));
        tv1=(TextView)findViewById(R.id.scalemode_textview);
        tv1.setTypeface(face);
        tv1.setTextColor(Color.parseColor("#bc9e66"));

        mVideoView = (VideoGLView)findViewById(R.id.cameraView);
        mVideoView.setVideoSize(mSharedData.videoWidth, mSharedData.videoHeight);
        mVideoView.setOnClickListener(mOnClickListener);
        mScaleModeView = (TextView)findViewById(R.id.scalemode_textview);
        updateScaleModeText();
//        mRecordButton = (ImageButton)findViewById(R.id.button_stop_rec);
//        mRecordButton.setOnClickListener(mOnClickListener);

        // Set Vew Button OnClickListeners.
        findViewById(R.id.button_camera_video).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_stop_rec).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_add).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_reset).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_play_pause).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_upload).setOnClickListener(mView_OnClickListener);

        // Set Adjustment Button OnClickListeners.
        findViewById(R.id.button_adjust).setOnClickListener(mAdjustment_OnClickListener);
        findViewById(R.id.brightnessButton).setOnClickListener(mAdjustment_OnClickListener);
        findViewById(R.id.contrastButton).setOnClickListener(mAdjustment_OnClickListener);
        findViewById(R.id.saturationButton).setOnClickListener(mAdjustment_OnClickListener);
        findViewById(R.id.vinyetButton).setOnClickListener(mAdjustment_OnClickListener);

        // Get preferences instance.
        mPreferences = getPreferences(MODE_PRIVATE);
        // Set Filter Button OnClickListeners.
        findViewById(R.id.button_filters).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_none).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_bnw).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Ansel).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Sepia).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Retro).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Georgia).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Sahara).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_Polaroid).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_VHS).setOnClickListener(mFilters_OnClickListener);
        findViewById(R.id.filter_CRTtv).setOnClickListener(mFilters_OnClickListener);

        // SeekBar ids as triples { SeekBar id, key id, default value }.
        final int SEEKBAR_IDS[][] = {
                { R.id.seekBarBrightness, R.string.key_brightness, 5 },
                { R.id.seekBarContrast, R.string.key_contrast, 5 },
                { R.id.seekBarSaturation, R.string.key_saturation, 8 },
                { R.id.seekBarVinyet, R.string.key_corner_radius, 3 } };
        // Set SeekBar OnSeekBarChangeListeners and default progress.
        for (int ids[] : SEEKBAR_IDS) {
            SeekBar seekBar = (SeekBar) findViewById(ids[0]);
            assert seekBar != null;
            seekBar.setOnSeekBarChangeListener(mObserverSeekBar);
            seekBar.setProgress(mPreferences.getInt(getString(ids[1]), ids[2]));
            // SeekBar.setProgress triggers observer only in case its value
            // changes. And we're relying on this trigger to happen.
            if (seekBar.getProgress() == 0) {
                seekBar.setProgress(1);
                seekBar.setProgress(0);
            }
        }

        try {
            //Get Gallery Folder or create it if doesnt exist
            mVideoFolder = MiscUtils.createVideoFolder("Splode");
            mVideoFile = MiscUtils.createVideoFileName(mVideoFolder,"fbo-gl-recording", ".mp4");//new File(getFilesDir(), "fbo-gl-recording.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mVideoFile = new File(getFilesDir(), "fbo-gl-recording.mp4");


        // Need to create one of these fancy ArrayAdapter thingies, and specify the generic layout
        // for the widget itself.
        try {

            //TODO Priority: hide item list in cam view
            ArrayAdapter<String> adapter;
            //Get asset file for first time user.
            AssetManager man = getAssets();
            //TODO Priority: add Splode Gallery file to list without crashing
            String[] mMovieString = prepend(MiscUtils.getFiles(mVideoFolder, "*.mp4"),"");//MiscUtils.getFiles(getFilesDir(), "*.mp4");
            if(mMovieString.length > 1) {
                mMovieFiles = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mMovieString);
                mVideoView.useAssets = false;
            }
            else {
                //mMovieString = prepend(man.list("samples"),"samples/");//man.list("samples");
                mMovieString = prepend(man.list("empty"),"/");//man.list("samples");
                mMovieFiles = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mMovieString);
                mVideoView.useAssets = true;
            }
            //mMovieFiles.addAll(mMovieString);

            mSelectedMovie = -1;
            mMovieFiles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Populate file-selection spinner.
            Spinner spinner = (Spinner) findViewById(R.id.playMovieFile_spinner);
            // Apply the adapter to the spinner.
            spinner.setAdapter(mMovieFiles);
            spinner.setOnItemSelectedListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVideoView.seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        mVideoView.seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mVideoView.seekTo(progress);
                int maxprogress = mVideoView.seek_bar.getMax();
                if(progress > maxprogress - 500) {
                    ImageButton toggleButton = (ImageButton) findViewById(R.id.button_play_pause);
                    toggleButton.setImageResource(R.mipmap.ic_pause);
                    PauseVideo();
                    stopRecording();
                    togglePlay = false;
                    showToast("stop");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Pause on load activity to let everything load
        new CountDownTimer(500, 100) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {
                findViewById(R.id.layout_menu).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_menuVideo).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_filters).setVisibility(View.GONE);
                findViewById(R.id.layout_adjustment).setVisibility(View.GONE);
                findViewById(R.id.layout_adjustButton).setVisibility(View.VISIBLE);

//                updateSharedData(mSharedData);
            }

        }.start();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mVideoView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        stopRecording();
        mVideoView.onPause();
        super.onPause();
    }


    void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.LEFT, 0, 0);
        toast.show();
    }

    boolean toggleMenu = false;
    boolean toggleAdjustment = false;
    boolean toggleFilters = false;
    boolean toggleRec = true;
    boolean togglePlay = false;

    /*
     * Called when the movie Spinner gets touched.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Spinner spinner = (Spinner) parent;
        mSelectedMovie = spinner.getSelectedItemPosition();
        if(mSelectedMovie >0) {
            mVideoView.VideoUpdate = true;
            StopVideo();
            if (!mVideoView.useAssets) {
                String Galfile = "/" + mMovieFiles.getItem(mSelectedMovie);
                mVideoView.galAddr = Uri.parse(mVideoFolder.getAbsolutePath() + Galfile);
                recordervideo = mVideoView.galAddr.toString();
            } else {
                mVideoView.galAddr = null;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * View Buttons
     */
    private final class ViewObserver implements View.OnClickListener {

        ImageButton toggleButton;
        public void onClick(final View v) {
            int i = v.getId();
            if (i == R.id.button_stop_rec) {
                if (toggleRec && mMuxer == null) {
                    toggleRec = false;
                    showToast("Record Start");
                    startRecording();
                } else {
                    toggleRec = true;
                    showToast("Record Stop");
                    stopRecording();
                }
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_stop_rec));

                //Video Editor Buttons
            } else if (i == R.id.button_add) {
                onPause();
                selectVideo();
                showToast("Gallery Video");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_add));

            } else if (i == R.id.button_reset) {
                StopVideo();
                toggleButton = (ImageButton) findViewById(R.id.button_play_pause);
                toggleButton.setImageResource(R.mipmap.ic_pause);
                togglePlay = false;
                //TODO reset view to show start of video
                showToast("Reset");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_reset));

            } else if (i == R.id.button_play_pause) {
                toggleButton = (ImageButton) findViewById(R.id.button_play_pause);
                if (mVideoView.galAddr != null || mVideoView.galSearch != null) {
                    if (togglePlay) {
                        toggleButton.setImageResource(R.mipmap.ic_pause);
                        PauseVideo();
                        togglePlay = false;
                        showToast("Stop");
                    } else {
                        toggleButton.setImageResource(R.mipmap.ic_play);
                        StartVideo();
                        togglePlay = true;
                        showToast("Play");
                    }
                }
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_play_pause));

            }
            else if (i == R.id.button_upload) {
                StopVideo();
                Intent inten = new Intent(VideoActivity.this, UploadActivity.class);
                inten.putExtra("RecordAddress", recordervideo);
                startActivity(inten);
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_upload));

            }
        }
    };

    /**
     * Filter Buttons
     */
    private final class FiltersObserver implements View.OnClickListener {

        public void onClick(final View v) {
            int i = v.getId();
            if (i == R.id.button_filters) {
                if (toggleFilters) {
                    hideUIElement(Techniques.FadeOutLeft, 700, findViewById(R.id.layout_filters));
                    showUIElement(Techniques.FadeIn, 700, findViewById(R.id.seek_bar));
                    toggleFilters = false;
                } else {
                    showUIElement(Techniques.FadeInRight, 700, findViewById(R.id.layout_filters));
                    hideUIElement(Techniques.FadeOut, 700, findViewById(R.id.seek_bar));
                    toggleFilters = true;
                    showToast("Choose a Filter");
                }
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.layout_adjustment));
                toggleAdjustment = false;
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_filters));

                // None Filter button
            } else if (i == R.id.filter_none) {
                mSharedData.mFilter = 0;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_none));

                // BnW Filter button
            } else if (i == R.id.filter_bnw) {
                mSharedData.mFilter = 1;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_bnw));

                // Ansel Filter button
            } else if (i == R.id.filter_Ansel) {
                mSharedData.mFilter = 2;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Ansel));

                // Sepia Filter button
            } else if (i == R.id.filter_Sepia) {
                mSharedData.mFilter = 3;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Sepia));

                // Retro Filter button
            } else if (i == R.id.filter_Retro) {
                mSharedData.mFilter = 4;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Retro));

                // Georgia Filter button
            } else if (i == R.id.filter_Georgia) {
                mSharedData.mFilter = 5;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Georgia));

                // Sahara Filter button
            } else if (i == R.id.filter_Sahara) {
                mSharedData.mFilter = 6;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Sahara));

                // Polaroid Filter button
            } else if (i == R.id.filter_Polaroid) {
                mSharedData.mFilter = 7;
                YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.filter_Polaroid));

                // CRTtv Filter button
            } else if (i == R.id.filter_CRTtv) {
                mSharedData.mFilter = 8;

                // VHS Filter button
            } else if (i == R.id.filter_VHS) {
                mSharedData.mFilter = 9;

            }
            if (mVideoView != null)updateSharedData(mSharedData);
        }
    };

    /**
     * Adjustment Buttons
     */
    private final class AdjustmentObserver implements View.OnClickListener  {

        public void onClick(final View v) {
            int i = v.getId();
            if (i == R.id.button_adjust) {
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_brightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarBrightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_contrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarContrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_saturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarSaturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_vinyet));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarVinyet));
                if (toggleAdjustment) {
                    hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.layout_adjustment));
                    showUIElement(Techniques.FadeIn, 700, findViewById(R.id.seek_bar));
                    toggleAdjustment = false;
                } else {
                    showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.layout_adjustment));
                    hideUIElement(Techniques.FadeOut, 700, findViewById(R.id.seek_bar));
                    toggleAdjustment = true;
                    showToast("Adjust you video");
                }
                hideUIElement(Techniques.FadeOutLeft, 700, findViewById(R.id.layout_filters));
                toggleFilters = false;
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_adjust));

            } else if (i == R.id.brightnessButton) {//hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_brightness));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarBrightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_contrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarContrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_saturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarSaturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_vinyet));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarVinyet));

                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.text_brightness));
                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.seekBarBrightness));
                showToast("Brightness");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.brightnessButton));

            } else if (i == R.id.contrastButton) {
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_brightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarBrightness));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_contrast));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarContrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_saturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarSaturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_vinyet));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarVinyet));

                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.text_contrast));
                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.seekBarContrast));

                showToast("Contrast");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.contrastButton));

            } else if (i == R.id.saturationButton) {
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_brightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarBrightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_contrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarContrast));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_saturation));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarSaturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_vinyet));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarVinyet));

                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.text_saturation));
                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.seekBarSaturation));

                showToast("Saturation");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.saturationButton));

            } else if (i == R.id.vinyetButton) {
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_brightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarBrightness));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_contrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarContrast));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_saturation));
                hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarSaturation));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.text_vinyet));
                //hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.seekBarVinyet));

                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.text_vinyet));
                showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.seekBarVinyet));

                showToast("Vinyet");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.vinyetButton));

            }
            if (mVideoView != null)updateSharedData(mSharedData);
        }
    };

    /**
     * Class for implementing SeekBar related callbacks.
     */
    private final class SeekBarObserver implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            int i = seekBar.getId();
            TextView textView;
            if (i == R.id.seekBarBrightness) {
                mPreferences.edit()
                        .putInt(getString(R.string.key_brightness), progress)
                        .apply();
                mSharedData.mBrightness = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_brightness);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_brightness,
                        progress - 5));
                // On contrast recalculate shared value and update preferences.
                mPreferences.edit()
                        .putInt(getString(R.string.key_contrast), progress)
                        .apply();
                mSharedData.mContrast = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_contrast);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_contrast,
                        progress - 5));
                // On saturation recalculate shared value and update preferences.
                mPreferences.edit()
                        .putInt(getString(R.string.key_saturation), progress)
                        .apply();
                mSharedData.mSaturation = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_saturation);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_saturation,
                        progress - 5));
                // On radius recalculate shared value and update preferences.
                mPreferences
                        .edit()
                        .putInt(getString(R.string.key_corner_radius), progress)
                        .apply();
                mSharedData.mCornerRadius = progress / 10f;
                textView = (TextView) findViewById(R.id.text_vinyet);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_corner_radius,
                        -progress));
            } else if (i == R.id.seekBarContrast) {
                mPreferences.edit()
                        .putInt(getString(R.string.key_contrast), progress)
                        .apply();
                mSharedData.mContrast = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_contrast);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_contrast,
                        progress - 5));
                // On saturation recalculate shared value and update preferences.
                mPreferences.edit()
                        .putInt(getString(R.string.key_saturation), progress)
                        .apply();
                mSharedData.mSaturation = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_saturation);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_saturation,
                        progress - 5));
                // On radius recalculate shared value and update preferences.
                mPreferences
                        .edit()
                        .putInt(getString(R.string.key_corner_radius), progress)
                        .apply();
                mSharedData.mCornerRadius = progress / 10f;
                textView = (TextView) findViewById(R.id.text_vinyet);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_corner_radius,
                        -progress));
            } else if (i == R.id.seekBarSaturation) {
                mPreferences.edit()
                        .putInt(getString(R.string.key_saturation), progress)
                        .apply();
                mSharedData.mSaturation = (progress - 5) / 10f;
                textView = (TextView) findViewById(R.id.text_saturation);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_saturation,
                        progress - 5));
                // On radius recalculate shared value and update preferences.
                mPreferences
                        .edit()
                        .putInt(getString(R.string.key_corner_radius), progress)
                        .apply();
                mSharedData.mCornerRadius = progress / 10f;
                textView = (TextView) findViewById(R.id.text_vinyet);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_corner_radius,
                        -progress));
            } else if (i == R.id.seekBarVinyet) {
                mPreferences
                        .edit()
                        .putInt(getString(R.string.key_corner_radius), progress)
                        .apply();
                mSharedData.mCornerRadius = progress / 10f;
                textView = (TextView) findViewById(R.id.text_vinyet);
                assert textView != null;
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.seekbar_corner_radius,
                        -progress));
            }
//            mRenderer.requestRender();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            findViewById(R.id.text_brightness).setVisibility(View.GONE);
            findViewById(R.id.text_contrast).setVisibility(View.GONE);
            findViewById(R.id.text_saturation).setVisibility(View.GONE);
            findViewById(R.id.text_vinyet).setVisibility(View.GONE);
        }
    }

    private void updateScaleModeText() {
        final int scale_mode = mVideoView.getScaleMode();
        mScaleModeView.setText(
                scale_mode == 0 ? "scale to fit"
                        : (scale_mode == 1 ? "keep aspect(viewport)"
                        : (scale_mode == 2 ? "keep aspect(matrix)"
                        : (scale_mode == 3 ? "keep aspect(crop center)" : ""))));
    }

    /**
     * onClick handler for "play" button.
     */
    public void StartVideo() {

        if ( mVideoView.VideoUpdate ) {
            //Get address of video to be played
            mVideoView.useAssets = false;
            if (mVideoView.galAddr == null) {
                if (mVideoView.useAssets) {
                    String Galfile = "/" + mMovieFiles.getItem(mSelectedMovie);
                    mVideoView.galSearch = Galfile;
                }
            }

            //Check if VideoView is present else create it
            mVideoView.startPreview(mSharedData.videoWidth, mSharedData.videoHeight);
            new CountDownTimer(1000, 100) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    mVideoView.seekUpdation();
                    //TODO apply video height and width
                    mSharedData.videoWidth = mVideoView.getMediaHeight();
                    mSharedData.videoHeight = mVideoView.getMediaWidth();
                    mVideoView.setVideoSize(mSharedData.videoWidth, mSharedData.videoHeight);
                    recordervideo = mVideoView.galAddr.toString();
                    updateSharedData(mSharedData);
                }

            }.start();
            //TODO Need to find a better way then accessing VideoGLView mediaplayer directly
//        mVideoView.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                ImageButton toggleButton = (ImageButton) findViewById(R.id.button_play_pause);
//                toggleButton.setImageResource(R.mipmap.ic_pause);
//                //player.pause();
//                PauseVideo();
//                togglePlay = false;
//                showToast("Stop");
//            }
//        });
        }
        else {
            mVideoView.PlayVideo();
        }

    }

    /**
     * "pause" Video.
     */
    public void PauseVideo() {
        if(mVideoView != null) {
            mVideoView.PauseVideo();
        }
    }

    /**
     * "stop" Video.
     */
    public void StopVideo() {
        if(mVideoView != null) {
            ImageButton toggleButton;
            toggleButton = (ImageButton) findViewById(R.id.button_play_pause);
            toggleButton.setImageResource(R.mipmap.ic_pause);
            togglePlay = false;
            mVideoView.StopVideo();
            mVideoView.VideoUpdate = true;
        }
    }

    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        StopVideo();
        new CountDownTimer(1000, 100) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {
                ImageButton toggleButton = (ImageButton) findViewById(R.id.button_stop_rec);
                try {
                    StartVideo();
                    toggleButton.setImageResource(R.mipmap.ic_rec);
                    toggleButton.setColorFilter(0xffff0000);	// turn red
                    mMuxer = new MediaMuxerWrapper(".mp4");	// if you record audio only, ".m4a" is also OK.
                    if (true) {
                        // for video capturing
                        new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mVideoView.getVideoWidth(), mVideoView.getVideoHeight(),getResources(), mSharedData);
                    }
                    if (true) {
                        // for audio capturing
                        new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
                    }
                    mMuxer.prepare();
                    mMuxer.startRecording();
                } catch (final IOException e) {
                    toggleButton.setImageResource(R.mipmap.ic_save);
                    toggleButton.setColorFilter(0);
                    Log.e(TAG, "startCapture:", e);
                }
            }

        }.start();
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        ImageButton toggleButton = (ImageButton) findViewById(R.id.button_stop_rec);
        toggleButton.setImageResource(R.mipmap.ic_save);
        toggleButton.setColorFilter(0);	// return to default color
        if (mMuxer != null) {
            mMuxer.stopRecording();
            recordervideo = mMuxer.getOutputPath();
            mMuxer = null;
            // you should not wait here
        }
    }

    String recordervideo;
    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mVideoView.setVideoEncoder((MediaVideoEncoder)encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mVideoView.setVideoEncoder(null);
        }
    };

    /**
     * functions to get video from gallery
     */
    //Alert options
    private String userChoosenTask;
    private int SELECT_FILE = 1;

    private void selectVideo() {
        onPause();
        new CountDownTimer(2000, 100) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {

//        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
//                "Cancel" };

                final CharSequence[] items = { "Choose from Gallery",
                        "Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
                builder.setTitle("Add Video!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result= Utility.checkPermission(VideoActivity.this);

//                if (items[item].equals("Take Photo")) {
//                    userChoosenTask ="Take Photo";
//                    if(result)
//                        cameraIntent();
//
//                } else
                        if (items[item].equals("Choose from Gallery")) {
                            userChoosenTask ="Choose from Gallery";
                            if(result)
                                galleryIntent();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }

        }.start();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("video/*");
//        image/jpeg
//        audio/mpeg4-generic
//        text/html
//        audio/mpeg
//        audio/aac
//        audio/wav
//        audio/ogg
//        audio/midi
//        audio/x-ms-wma
//        video/mp4
//        video/x-msvideo
//        video/x-ms-wmv
//        image/png
//        image/jpeg
//        image/gif
//                .xml ->text/xml
//            .txt -> text/plain
//            .cfg -> text/plain
//            .csv -> text/plain
//            .conf -> text/plain
//            .rc -> text/plain
//            .htm -> text/html
//            .html -> text/html
//            .pdf -> application/pdf
//            .apk -> application/vnd.android.package-archive
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            mVideoView.galAddr = data.getData();
            mVideoView.VideoUpdate = true;
            recordervideo = mVideoView.galAddr.toString();
            new CountDownTimer(1000, 100) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    mVideoView.seekUpdation();
                }

            }.start();

        }
        else {
            mVideoView.galAddr = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if(userChoosenTask.equals("Take Photo"))
//                        cameraIntent();
//                    else
                    if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context)
        {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if(currentAPIVersion>= Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        android.support.v7.app.AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    /**
     * use to give a pause to show animation before hiding UI element
     * @param animType
     * @param Duration
     * @param hideUIbyId
     */
    private static void hideUIElement(Techniques animType, int Duration, final View hideUIbyId) {

        YoYo.with(animType).duration(Duration).playOn(hideUIbyId);

        new CountDownTimer(700, 10) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {
                hideUIbyId.setVisibility(View.GONE);
            }
        }.start();
    }

    /**
     * use to give a pause to show animation on showing UI element
     * @param animType
     * @param Duration
     * @param hideUIbyId
     */
    private static void showUIElement(Techniques animType, int Duration, final View hideUIbyId) {

        hideUIbyId.setVisibility(View.VISIBLE);
        YoYo.with(animType).duration(Duration).playOn(hideUIbyId);
    }

    public String[] prepend(String[] input, String prepend) {
        String[] output = new String[input.length + 1];
        output[0] = "" + prepend + "None";
        for (int index = 0; index < input.length; index++) {
            output[index+1] = "" + prepend + input[index];
        }
        return output;
    }

    /**
     * Global On click listener for all views in activity
     * Handle animation of buttons and views here
     * method when touch record button
     */
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.cameraView:
                    final int scale_mode = (mVideoView.getScaleMode() + 1) % 4;
//                    mVideoView.setScaleMode(scale_mode);
                    updateScaleModeText();
                    if (toggleMenu) {//TODO Priority: Reduce number of clicks to open GLview
                        hideUIElement(Techniques.FadeOutUp, 700, findViewById(R.id.layout_menu));
                        hideUIElement(Techniques.FadeOutRight, 700, findViewById(R.id.layout_menuVideo));
                        hideUIElement(Techniques.FadeOutLeft, 700, findViewById(R.id.layout_filters));
                        hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.layout_adjustment));
                        hideUIElement(Techniques.FadeOut, 700, findViewById(R.id.seek_bar));
                        toggleMenu = false;
                        //toggleButton.setImageResource(R.mipmap.ic_rec);
                    } else {
                        showUIElement(Techniques.FadeInDown, 700, findViewById(R.id.layout_menu));
                        showUIElement(Techniques.FadeInRight, 700, findViewById(R.id.layout_menuVideo));
                        showUIElement(Techniques.FadeIn, 700, findViewById(R.id.seek_bar));
                        toggleMenu = true;
                    }
                    YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_menu));

                    break;
            }
        }
    };


}
