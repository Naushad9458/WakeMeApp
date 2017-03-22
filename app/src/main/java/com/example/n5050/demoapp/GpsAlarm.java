package com.example.n5050.demoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;

public class GpsAlarm extends AppCompatActivity {

    private int off;
    Intent intent;
    NotificationManager notificationManager;
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

    public void startGPS(View view) {
        Intent i=new Intent(this,FetchLocation.class);
        startService(i);
        showNotification();


    }
    public void showNotification(){
        NotificationCompat.Builder notificationBuilder= (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentTitle("GPS Service Activated")
                .setContentText("Running in Background")
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("GPS!"))
                .setAutoCancel(true);

        notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(87123,notificationBuilder.build());

    }

    public void stopGps(View view) {
        Intent intent=new Intent(this,FetchLocation.class);
        stopService(intent);
    }
}
