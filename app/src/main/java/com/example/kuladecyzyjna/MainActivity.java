package com.example.kuladecyzyjna;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static public SensorManager mSensorManager;
    static List<Sensor> SensorList;
    static final public String SENSOR_TYPE = "sensorType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        setContentView(R.layout.activity_main);

        final SensorFragment sensFrag = (SensorFragment) getSupportFragmentManager().findFragmentById(R.id.sensorList);
        sensFrag.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSensor(parent,position);
            }
        });
    }

    private void showSensor(AdapterView<?> parent, int position) {
        Intent sensIntent = new Intent(this,SensorActivity.class);
        Sensor currentSensor = (Sensor) parent.getItemAtPosition(position);
        sensIntent.putExtra(SENSOR_TYPE, currentSensor.getType());
        startActivity(sensIntent);
    }
}
