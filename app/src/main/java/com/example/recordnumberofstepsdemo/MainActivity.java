package com.example.recordnumberofstepsdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView showsteptv;
    private Button getstepbtn;
    private Sensor mystepcounter;//单次步伐传感器
    private static int stepSensor = -1;
    private SensorEventListener stepCounterListener;//步伐总数传感器事件监听器

    //传感器
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showsteptv = (TextView) findViewById(R.id.showsteptv);
        getstepbtn = (Button) findViewById(R.id.btn);
        initPermission();
        judgementVersion();
//        getstepbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private void judgementVersion() {
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            initSensorManager();
        } else {
            Log.e("测试", "VERSION_CODES < 19");

        }
    }


    private void initSensorManager() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器系统服务

        mystepcounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器
        initListener();

    }


    protected void initListener() {
        Log.e("注册传感器事件", "注册传感器事件");

        stepCounterListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.e("Counter-SensorChanged", event.values[0] + "---" + event.accuracy + "---" + event.timestamp);
                Log.e("onSensorChanged", "运行啦");

                showsteptv.setText("" + event.values[0]);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.e("Counter-Accuracy", sensor.getName() + "---" + accuracy);

            }
        };
    }

    private void registerSensor() {
        //注册传感器事件监听器
        Log.e("注册传感器事件监听器", "注册传感器事件监听器");

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) &&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            mSensorManager.registerListener(stepCounterListener, mystepcounter, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void unregisterSensor() {
        //解注册传感器事件监听器
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) &&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            mSensorManager.unregisterListener(stepCounterListener);
        }
    }

    //动态申请权限
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.BODY_SENSORS,
        };
        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterSensor();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensor();
    }


}

