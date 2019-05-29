package com.example.kuladecyzyjna;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.hardware.Camera;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import static java.lang.Math.min;
import static java.lang.StrictMath.abs;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor mSensor;
    private int sensorType;
    private long lastUpdate = -1;
    private TextView textViewDecision;
    private ImageView startImgView;
    private ImageView endImgView;
    private int screenWidth;
    private int screenHeight;
    private int imgEdgeSize;
    private boolean layoutReady;
    private ConstraintLayout mainContainer;
    private Path upPath;
    private Path downPath;
    private boolean animFlag =  false;
    private boolean isFlashOn = false;
    private boolean hasFlash = false;
    private int idStart=0;
    long start,end;
    private int id;
    public String[] decision = {"Jest pewne.", "Tak jak to widzę, tak.", "Odpowiedz mgliste, spróbuj ponownie.", "Nie licz na to.", "Jest zdecydowanie tak.",
            "Najprawdopodobniej.", "Pytaj ponownie później.", "Moja odpowiedź brzmi nie", "Bez wątpienia.", "Outlook dobry.", "Lepiej nie mów teraz.",
            "Moje źródła mówią nie.", "Tak - zdecydowanie.", "Tak.", "Nie można przewidzieć teraz.", "Outlook nie tak dobry.", "Możesz na tym polegać.",
            "Znaki wskazują na tak.", "Skoncentruj się i zapytaj ponownie.", "Bardzo wątpliwe."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        textViewDecision= findViewById(R.id.textDecision);
        startImgView = findViewById(R.id.startImageView);
        endImgView = findViewById(R.id.endImageView);
        final Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            sensorType = receivedIntent.getIntExtra(MainActivity.SENSOR_TYPE, -1);
            if (sensorType != -1) {
                mSensor = MainActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                startImgView.setVisibility(View.VISIBLE);
                endImgView.setVisibility(View.INVISIBLE);
                textViewDecision.setVisibility(View.INVISIBLE);
                    textViewDecision.setText("");
                    if (sensorType == Sensor.TYPE_PROXIMITY) {
                        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                        if (!hasFlash)
                            Toast.makeText(this, "No flashlight!", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "Wrong sensor type ", Toast.LENGTH_SHORT).show();
            }
                layoutReady = false;
                mainContainer = findViewById(R.id.sensor_container);
                mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imgEdgeSize = startImgView.getWidth();
                        screenWidth = mainContainer.getWidth();
                        screenHeight = mainContainer.getHeight();
                        float rectY = (screenHeight - imgEdgeSize) / 2f;
                        float rectHeight = screenHeight - rectY - imgEdgeSize;
                        float rectWidth = min(screenWidth - imgEdgeSize, rectHeight);
                        float rectX = (screenWidth - rectWidth - imgEdgeSize) / 2;
                        RectF animRect = new RectF(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
                        upPath = new Path();
                        downPath = new Path();
                        upPath.arcTo(animRect, 90f, -180f,true);
                        downPath.arcTo(animRect, 270f, -180f, true);
                        mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        layoutReady = true;
                    }
                });
            }
    private void lightSensorAnimation(boolean showMoon) {
        ObjectAnimator startAnimator;
        ObjectAnimator endAnimator;
        ObjectAnimator endFadeAnimator;
        ObjectAnimator startFadeAnimator;
        ObjectAnimator startTextAnimator;
        ObjectAnimator endTextAnimator;

        float endFromAlpha = showMoon ? 0f : 1f;
        float endToAlpha = showMoon ? 1f : 0f;
        float startFromAlpha = showMoon ? 1f : 0f;
        float startToAlpha = showMoon ? 0f : 1f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startImgView.setVisibility(View.VISIBLE);
            endImgView.setVisibility(View.VISIBLE);
            textViewDecision.setVisibility(View.VISIBLE);

            endAnimator = ObjectAnimator.ofFloat(endImgView, View.X, View.Y, downPath);
            endAnimator.setDuration(2200);
            endAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            startTextAnimator = ObjectAnimator.ofFloat(textViewDecision, View.X, View.Y, upPath);
            startTextAnimator.setDuration(2200);
            startTextAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            endFadeAnimator = ObjectAnimator.ofFloat(endImgView, "alpha", endFromAlpha, endToAlpha);
            endFadeAnimator.setInterpolator(new AccelerateInterpolator());
            endFadeAnimator.setDuration(2200);

            endTextAnimator = ObjectAnimator.ofFloat(textViewDecision, "alpha", endFromAlpha, endToAlpha);
            endTextAnimator.setInterpolator(new AccelerateInterpolator());
            endTextAnimator.setDuration(2200);

            startAnimator = ObjectAnimator.ofFloat(startImgView, View.X, View.Y, downPath);
            startAnimator.setDuration(2200);
            startAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            startFadeAnimator = ObjectAnimator.ofFloat(startImgView, "alpha", startFromAlpha, startToAlpha);
            startFadeAnimator.setInterpolator(new AccelerateInterpolator());
            startFadeAnimator.setDuration(2200);
        } else {
            float startEndY = screenHeight - imgEdgeSize;

            endAnimator = ObjectAnimator.ofFloat(endImgView, "y", startEndY, startEndY);
            endAnimator.setInterpolator(new AccelerateInterpolator());
            endAnimator.setDuration(2000);

            startTextAnimator = ObjectAnimator.ofFloat(textViewDecision, "y", startEndY, startEndY);
            startTextAnimator.setInterpolator(new AccelerateInterpolator());
            startTextAnimator.setDuration(2000);

            startAnimator = ObjectAnimator.ofFloat(startImgView, "y", startEndY, startEndY);
            startAnimator.setInterpolator(new AccelerateInterpolator());
            startAnimator.setDuration(2000);

            endFadeAnimator = ObjectAnimator.ofFloat(endImgView, "alpha", endFromAlpha, endToAlpha);
            endFadeAnimator.setInterpolator(new AccelerateInterpolator());
            endFadeAnimator.setDuration(2200);

            endTextAnimator = ObjectAnimator.ofFloat(textViewDecision, "alpha", endFromAlpha, endToAlpha);
            endTextAnimator.setInterpolator(new AccelerateInterpolator());
            endTextAnimator.setDuration(2200);

            startFadeAnimator = ObjectAnimator.ofFloat(startImgView, "alpha", startFromAlpha, startToAlpha);
            startFadeAnimator.setInterpolator(new AccelerateInterpolator());
            startFadeAnimator.setDuration(2200);
        }
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(startAnimator).with(endAnimator).with(startTextAnimator).with(startFadeAnimator).with(endFadeAnimator).with(endTextAnimator);
        animSet.start();
    }

    private void handleProximitySensor(float sensorValue) {
        if(!animFlag && (sensorValue == 0 ) && idStart!=0) {
            animFlag = true;
            id=1;
            lightSensorAnimation(false);
            toogleFlash(true);
        }else if(animFlag && (sensorValue>0)) {
            animFlag = false;
            idStart=1;
            id=2;
            makeDecision();
            lightSensorAnimation(true);
            toogleFlash(false);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        idStart=2;
        long timeMicro;
        if(lastUpdate == -1) {
            lastUpdate = event.timestamp;
            timeMicro =0;
        } else {
            timeMicro = (event.timestamp - lastUpdate ) / 1000L;
            lastUpdate = event.timestamp;
        }
        if(id==1){
            start = timeMicro;
        }
        if(id==2) {
            end = timeMicro;
        }
        if(layoutReady) {
            handleProximitySensor(event.values[0]);
        }
    }
    private void makeDecision() {
        long difference = abs(start-end);
        if (difference < 100000) {
            textViewDecision.setText(decision[0]);
        }
        if (difference > 100000 && difference < 200000) {
            textViewDecision.setText(decision[1]);
        }
        if (difference > 200000 && difference < 300000) {
            textViewDecision.setText(decision[2]);
        }
        if (difference > 300000 && difference < 400000) {
            textViewDecision.setText(decision[3]);
        }
        if ( difference > 400000 && difference < 500000) {
            textViewDecision.setText(decision[4]);
        }
        if ( difference > 500000 && difference < 600000) {
            textViewDecision.setText(decision[5]);
        }
        if ( difference > 600000 && difference < 700000) {
            textViewDecision.setText(decision[6]);
        }
        if (difference > 700000 && difference < 800000) {
            textViewDecision.setText(decision[7]);
        }
        if (difference > 800000 && difference < 900000) {
            textViewDecision.setText(decision[8]);
        }
        if (difference > 900000 && difference < 1000000) {
            textViewDecision.setText(decision[9]);
        }
        if (difference > 1000000 && difference < 1100000) {
            textViewDecision.setText(decision[10]);
        }
        if (difference > 1100000 && difference < 1200000) {
            textViewDecision.setText(decision[11]);
        }
        if (difference > 1200000 && difference < 1300000) {
            textViewDecision.setText(decision[12]);
        }
        if (difference > 1300000 && difference < 1400000) {
            textViewDecision.setText(decision[13]);
        }
        if (difference > 1400000 && difference < 1500000) {
            textViewDecision.setText(decision[14]);
        }
        if (difference > 1500000 && difference < 1600000) {
            textViewDecision.setText(decision[15]);
        }
        if (difference > 1600000 && difference < 1700000) {
            textViewDecision.setText(decision[16]);
        }
        if (difference > 1700000 && difference < 1800000) {
            textViewDecision.setText(decision[17]);
        }
        if (difference > 1800000 && difference < 1900000) {
            textViewDecision.setText(decision[18]);
        }
        if (difference > 2000000 ) {
            textViewDecision.setText(decision[19]);
        }
    }
    private void toogleFlash(boolean on) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraManager.setTorchMode(cameraManager.getCameraIdList()[0],on);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            Camera camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            if(on)
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            else
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            if(on)
                camera.startPreview();
            else {
                camera.stopPreview();
                camera.release();
            }
        }
        isFlashOn = on;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mSensor != null )
            MainActivity.mSensorManager.registerListener(this,mSensor,100000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mSensor != null)
            MainActivity.mSensorManager.unregisterListener(this,mSensor);
        if(isFlashOn)
            toogleFlash(false);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
