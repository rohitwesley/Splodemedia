package com.tecrt.rowest.splodemedia;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tecrt.rowest.tecrtmedialib.CameraGLView;
import com.tecrt.rowest.tecrtmedialib.MiscUtils;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaAudioEncoder;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaEncoder;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaMuxerWrapper;
import com.tecrt.rowest.tecrtmedialib.encoder.MediaVideoEncoder;
import com.tecrt.rowest.tecrtmedialib.tecrtData;

import java.io.IOException;

public class CamActivity extends AppCompatActivity {

    private static final boolean DEBUG = false;	// TODO set false on release
    private static final String TAG = "CameraFragment";

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;
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
    /**
     * update for shared data.
     */
    public void updateSharedData(tecrtData sharedData) {
        mCameraView.updateSharedData(sharedData);
    }

    public CamActivity() {
        // need default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        //Get Gallery Folder or create it if doesnt exist
        mSharedData.mVideoFolder = MiscUtils.createVideoFolder("Splode");

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

        if(checkCamera()) {
            mCameraView = (CameraGLView) findViewById(R.id.cameraView);
            mCameraView.setVideoSize(mSharedData.videoWidth, mSharedData.videoHeight);
            mCameraView.setOnClickListener(mOnClickListener);
        }
        mScaleModeView = (TextView)findViewById(R.id.scalemode_textview);
        updateScaleModeText();
//        mRecordButton = (ImageButton)findViewById(R.id.button_stop_rec);
//        mRecordButton.setOnClickListener(mOnClickListener);

        // Set Vew Button OnClickListeners.
        findViewById(R.id.button_camera_video).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_camrotate).setOnClickListener(mView_OnClickListener);
        findViewById(R.id.button_stop_rec).setOnClickListener(mView_OnClickListener);

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
        //Get Gallery Folder or create it if doesnt exist
        mSharedData.mVideoFolder = MiscUtils.createVideoFolder("Splode");

        //Pause on load activity to let everything load
        new CountDownTimer(500, 100) {

            public void onTick(long millisUntilFinished) {
                // You don't need anything here
            }

            public void onFinish() {
                findViewById(R.id.layout_menu).setVisibility(View.GONE);
                findViewById(R.id.layout_menuCamera).setVisibility(View.GONE);
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
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        stopRecording();
        mCameraView.onPause();
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

    /**
     * View Buttons
     */
    private final class ViewObserver implements View.OnClickListener {

        public void onClick(final View v) {
            int i = v.getId();
            if (i == R.id.button_camrotate) {
                showToast("Camera Changed");
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_camrotate));

            } else if (i == R.id.button_stop_rec) {
                if (toggleRec && mMuxer == null && checkWriteStoragePermission()) {
                    toggleRec = false;
                    showToast("Record Start");
                    startRecording();
                } else {
                    toggleRec = true;
                    showToast("Record Stop");
                    stopRecording();
                }
                YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_stop_rec));

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
                    toggleFilters = false;
                } else {
                    showUIElement(Techniques.FadeInRight, 700, findViewById(R.id.layout_filters));
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
            if (mCameraView != null)updateSharedData(mSharedData);
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
                    toggleAdjustment = false;
                } else {
                    showUIElement(Techniques.FadeInUp, 700, findViewById(R.id.layout_adjustment));
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
            if (mCameraView != null)updateSharedData(mSharedData);
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
        final int scale_mode = mCameraView.getScaleMode();
        mScaleModeView.setText(
                scale_mode == 0 ? "scale to fit"
                        : (scale_mode == 1 ? "keep aspect(viewport)"
                        : (scale_mode == 2 ? "keep aspect(matrix)"
                        : (scale_mode == 3 ? "keep aspect(crop center)" : ""))));
    }

    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");

        ImageButton toggleButton = (ImageButton) findViewById(R.id.button_stop_rec);
        try {
            toggleButton.setImageResource(R.mipmap.ic_rec);
            toggleButton.setColorFilter(0xffff0000);	// turn red
            mMuxer = new MediaMuxerWrapper("Splode",".mp4");	// if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight(),getResources(), mSharedData);
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            toggleButton.setImageResource(R.mipmap.ic_stop);
            toggleButton.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        ImageButton toggleButton = (ImageButton) findViewById(R.id.button_stop_rec);
        toggleButton.setImageResource(R.mipmap.ic_stop);
        toggleButton.setColorFilter(0);	// return to default color
        if (mMuxer != null) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mMuxer.getOutputPath())));
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder)encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
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
                    final int scale_mode = (mCameraView.getScaleMode() + 1) % 4;
//                    mCameraView.setScaleMode(scale_mode);
                    updateScaleModeText();
                    if (toggleMenu) {//TODO Priority: Reduce number of clicks to open GLview
                        hideUIElement(Techniques.FadeOutUp, 700, findViewById(R.id.layout_menu));
                        hideUIElement(Techniques.FadeOutRight, 700, findViewById(R.id.layout_menuCamera));
                        hideUIElement(Techniques.FadeOutLeft, 700, findViewById(R.id.layout_filters));
                        hideUIElement(Techniques.FadeOutDown, 700, findViewById(R.id.layout_adjustment));
                        toggleMenu = false;
                        //toggleButton.setImageResource(R.mipmap.ic_rec);
                    } else {
                        showUIElement(Techniques.FadeInDown, 700, findViewById(R.id.layout_menu));
                        showUIElement(Techniques.FadeInRight, 700, findViewById(R.id.layout_menuCamera));
                        toggleMenu = true;
                    }
                    YoYo.with(Techniques.RotateIn).duration(700).playOn(findViewById(R.id.button_menu));

                    break;
            }
        }
    };


}
