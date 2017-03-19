package com.example.n5050.demoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class GpsAlarm extends AppCompatActivity {

    private int off;
    Intent intent;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_alarm);
        if(!((LocationManager) this.getSystemService(Context.LOCATION_SERVICE))
            .isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSAlert();

        }





    }
    private void showGPSAlert(){
        AlertDialog alertDialog=new AlertDialog.Builder(GpsAlarm.this).create();
        alertDialog.setTitle("GPS Turned Off!");
        alertDialog.setMessage("Switch it on?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
