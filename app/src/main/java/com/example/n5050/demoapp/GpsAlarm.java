package com.example.n5050.demoapp;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GpsAlarm extends AppCompatActivity {

    private int off;
    Intent intent;
    NotificationManager notificationManager;
    String stationName;
    double longitude;
    double latitude;
    TextView name;
    TextView coordinates;
    Spinner alarmDistance;
    int position;
    int distanceValue;
    SharedPreferences sharedPreferences;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_alarm);
        name=(TextView) findViewById(R.id.stationName);
        coordinates=(TextView) findViewById(R.id.coordinates);
        sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            stationName=extras.getString("stationName");
            longitude=extras.getDouble("longitude");
            latitude=extras.getDouble("latitude");
        }
        name.setText(stationName);
        coordinates.setText(""+longitude+","+latitude);
        alarmDistance=(Spinner) findViewById(R.id.alarmDistance);

    }




    public void startGPS(View view) {
        position=alarmDistance.getSelectedItemPosition();
        String[] distance=getResources().getStringArray(R.array.distance);
        distanceValue=Integer.valueOf(distance[position]);

        Intent i=new Intent(this,FetchLocation.class);
        i.putExtra("stationName",stationName);
        i.putExtra("longitude",longitude);
        i.putExtra("latitude",latitude);
        i.putExtra("distance",distanceValue);
        startService(i);
        //showAlert();

        Toast.makeText(this,"Alarm will go off "+distanceValue+"m from destination",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,SetGpsAlarm.class);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("stationName",stationName);
        editor.commit();
        startActivity(intent);

    }

    public void showAlert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(GpsAlarm.this).create();
        alertDialog.setTitle("Alarm Scheduled for "+stationName);
        alertDialog.setMessage("Alarm will go off "+distanceValue+"m before destination");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
