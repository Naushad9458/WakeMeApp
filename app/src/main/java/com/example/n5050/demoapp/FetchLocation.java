package com.example.n5050.demoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by n5050 on 3/20/2017.
 */
public class FetchLocation extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    protected GoogleApiClient googleApiClient;
    protected Location location;
    protected LocationRequest locationRequest;
    Context context;
    double destinationLong;
    double destinationLat;
    String stationName;
    protected Location destination;
    int distanceValue;
    long fetchInterval=10000;
    NotificationManager notificationManager;
    String notifyDistance;
    String notifyLastUpdate;
    NotificationCompat.Builder builder;
    SharedPreferences sharedPreferences;
    LocationListener locationListener;

    public void onCreate(){
        super.onCreate();
        buildGoogleApiClient();
    }
    public void onDestroy(){
        super.onDestroy();
        googleApiClient.disconnect();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(87123);
        sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    public int onStartCommand(Intent intent,int flags,int startId){
        destinationLat=intent.getDoubleExtra("latitude",0);
        destinationLong=intent.getDoubleExtra("longitude",0);
        destination=new Location("dest");
        destination.setLatitude(destinationLat);
        destination.setLongitude(destinationLong);
        distanceValue=intent.getIntExtra("distance",0);
        stationName=intent.getStringExtra("stationName");
        showNotification(stationName);

        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }
        return START_NOT_STICKY;

    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onConnectionFailed(ConnectionResult connectionResult){
        Toast.makeText(this,"Could not establish connection",Toast.LENGTH_LONG).show();

    }

    public void onConnected(Bundle bundle){
        /*location=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String lat=""+location.getLatitude();
        Toast.makeText(this,lat,Toast.LENGTH_LONG).show();*/
        createLocRequest(fetchInterval);

    }
    public void onLocationChanged(Location loc){

        float distanceInMetres=loc.distanceTo(destination);
        Calendar c=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");


        notifyDistance=""+String.format("%.2f",distanceInMetres/1000)+" KMs";
        notifyLastUpdate= dateFormat.format(c.getTime());
        builder.setContentText("Distance to go: "+notifyDistance);
        builder.setSubText("Last Updated: "+notifyLastUpdate);

        notificationManager.notify(87123,builder.build());
        String text=""+distanceInMetres;
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
        changeFetchInterval(distanceInMetres);
        if(distanceInMetres<=distanceValue){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
            stopSelf();
            notificationManager.cancel(87123);
            SharedPreferences sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
            PowerManager pm=(PowerManager) this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE|PowerManager.FULL_WAKE_LOCK,"Wake");
            wl.acquire();
            Vibrator v=(Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(2000);
            Intent intent=new Intent(this,TurnOff.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(this,"Alarm Activated",Toast.LENGTH_LONG).show();
            wl.release();

        }


    }

    public void onConnectionSuspended(int i){
        Toast.makeText(this,"Connection Suspended",Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                googleApiClient.connect();

            }
        },1000);

    }
    public void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    public void createLocRequest(long i){
        locationRequest=new LocationRequest();
        locationRequest.setInterval(i);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    public void changeFetchInterval(float d){

        if(d>100000){
            fetchInterval=1*60*60*1000;
        }
        else
        if(d<100000 && d>20000){
            fetchInterval=30*60*1000;

        }
        else
        if(d<20000){
            fetchInterval=30*1000;
        }
        long currentFetchInterval=locationRequest.getInterval();
        if(currentFetchInterval!=fetchInterval){
            createLocRequest(fetchInterval);
        }
    }
    public void showNotification(String name){
        builder= (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentTitle("Alarm Active for "+name)
                .setContentText("Distance to go: Waiting for update..")
                .setSubText("Last Updated: Waiting for update..")
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.icon)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent(this));

        notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(87123,builder.build());

    }
    private static PendingIntent contentIntent(Context context){
        Intent intent=new Intent(context,SetGpsAlarm.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return PendingIntent.getActivity(context,87123,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
