package com.example.n5050.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

public class GpsAlarm extends AppCompatActivity {

    private int off;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_alarm);
        try{
        off= Settings.Secure.getInt(getContentResolver(),Settings.Secure.LOCATION_MODE);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if(off==0){
            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

        }

    }
}
