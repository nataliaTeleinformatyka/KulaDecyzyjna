package com.example.kuladecyzyjna;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static public SensorManager mSensorManager;
    static final public String SENSOR_TYPE = "sensorType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            Toast.makeText(this, "Istnieje czujnik zblizeniowy", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Nie istnieje czujnik zblizeniowy", Toast.LENGTH_LONG).show();
        }
        final SensorFragment sensFrag = (SensorFragment) getSupportFragmentManager().findFragmentById(R.id.sensors);
        showSensor();
    }

    private void showSensor() {
        Intent sensIntent = new Intent(this,SensorActivity.class);
        int currentSensor =Sensor.TYPE_PROXIMITY;
        sensIntent.putExtra(SENSOR_TYPE, currentSensor);
        startActivity(sensIntent);
    }
}
