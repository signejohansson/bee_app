package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class activity_3 extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView textViewAccelerometer;

    private SensorManager lightSensorManager;
    private Sensor ambientLightSensor;
    private TextView lightLevelTextView;

    private ImageView backgroundImage;

    float x;
    float y;
    float z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        textViewAccelerometer = findViewById(R.id.textView3);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        lightSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ambientLightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (ambientLightSensor == null) {
            //if light sensor is not available on the device
            Toast.makeText(this, "Light sensor not available on this device", Toast.LENGTH_SHORT).show();
            return;
        }
        lightLevelTextView = findViewById(R.id.lightLevelTextView);

        backgroundImage = findViewById(R.id.backgroundImageView);

        //a click listener for the background image
        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundImage.setImageResource(R.drawable.flowers);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //register sensor listeners
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lightSensorManager.registerListener(this, ambientLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister sensor listeners (saves power)
        mSensorManager.unregisterListener(this);
        lightSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //update x, y, and z values (accelerometer)
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            //update the TextView with the accelerometer values
            String accelerometerText = "x: " + x + ", y: " + y + ", z: " + z;
            textViewAccelerometer.setText(accelerometerText);
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            lightLevelTextView.setText(getString(R.string.light_level_format, lightLevel));
        }
    }
}
