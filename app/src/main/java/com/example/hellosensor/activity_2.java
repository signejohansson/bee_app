package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


public class activity_2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x;
    private float last_y;
    private float last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private ImageView imageView;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    final float alpha = 0.8f;
    private boolean isAnimationRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.bee); //sound effect
        imageView = findViewById(R.id.imageView); //bee image
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //move bee without using animations
            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            //update position
            float tiltX = linear_acceleration[0];
            float tiltY = linear_acceleration[1];
            float tiltZ = linear_acceleration[2];

            //move image as phone is tilted
            float tiltThreshold = 1.5f;
            if (Math.abs(tiltX) > tiltThreshold || Math.abs(tiltY) > tiltThreshold || Math.abs(tiltZ) > tiltThreshold) {
                float multiplier = 20f;
                //translation values
                float translationX = -tiltX * multiplier;
                float translationY = tiltY * multiplier;
                float translationZ = tiltZ * multiplier;

                //new position
                float newX = imageView.getX() + translationX;
                float newY = imageView.getY() + translationY;
                float newZ = imageView.getZ() + translationZ;

                //update position of imageview
                imageView.setX(newX);
                imageView.setY(newY);
                imageView.setZ(newZ);
            }

            //vibrate and play sound if phone is shaken
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    //vibrate for 500 milliseconds
                    vibrator.vibrate(500);
                    //play the sound effect
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }

            /*
            //move bee using animations
            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float tiltX = linear_acceleration[0];
            float tiltY = linear_acceleration[1];
            float tiltZ = linear_acceleration[2];

            //start animation when phone is tilted
            float tiltThreshold = 3.0f;
            if (Math.abs(tiltX) > tiltThreshold) {
                if(tiltX < 0) {
                    startAnimation(R.anim.move_right);
                }
                else if (tiltX > 0) {
                    startAnimation(R.anim.move_left);
                }
            }
            if(Math.abs(tiltY) > tiltThreshold) {
                if(tiltY < 0) {
                    startAnimation(R.anim.move_up);
                }
                else if(tiltY > 0) {
                    startAnimation(R.anim.move_down);
                }
            }
            if(Math.abs(tiltZ) > tiltThreshold) {
                if(tiltZ < 0) {
                    startAnimation(R.anim.move_down);
                }
                else if(tiltZ > 0) {
                    startAnimation(R.anim.move_up);
                }
            } */
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*
    private void startAnimation(int animationResource) {
        if (!isAnimationRunning) {
            isAnimationRunning = true;
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), animationResource);
            imageView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAnimationRunning = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }*/
}

