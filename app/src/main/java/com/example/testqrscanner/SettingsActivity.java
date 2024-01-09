package com.example.testqrscanner;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class SettingsActivity extends Fragment {

    private EditText editText_ApiKey;
    private SensorManager sm;
    private float acelVal,acelLast,shake;
    private int cntShake;
    private Button btn_drop;

    SettingsActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        btn_drop = (Button) view.findViewById(R.id.btn_drop);
        btn_drop.setEnabled(false);
        Button btn_saveApiKey = (Button) view.findViewById(R.id.btn_saveApiKey);
        editText_ApiKey = (EditText) view.findViewById(R.id.editText_ApiKey);
        editText_ApiKey.setText(DataBaseController.getUserApiKey(getActivity()));
        sm=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        acelVal=SensorManager.GRAVITY_EARTH;
        acelLast=SensorManager.GRAVITY_EARTH;
        shake=0.00f;

        btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseController.dropDB(getActivity());
            }
        });

        btn_saveApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataBaseController.setUserApiKey(getActivity(), editText_ApiKey.getText().toString())) {
                    ToastMessage.show("Api-ключ сохранен");
                }
                else ToastMessage.show("Произошла ошибка!");
            }
        });


        return view;
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            acelLast=acelVal;
            acelVal=(float) Math.sqrt((double) (x * x)+(y * y)+(z * z));
            float delta= acelVal - acelLast;
            shake =shake * 0.9f + delta;


            if(shake > 12){
                cntShake++;
            }

            if(cntShake > 2){
                btn_drop.setEnabled(true);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
