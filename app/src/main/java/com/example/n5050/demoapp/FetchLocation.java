package com.example.n5050.demoapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by n5050 on 3/20/2017.
 */
public class FetchLocation extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    protected GoogleApiClient googleApiClient;
    protected Location location;
    protected LocationRequest locationRequest;
    Context context;

    public void onCreate(){
        super.onCreate();
        buildGoogleApiClient();
    }
    public void onDestroy(){
        super.onDestroy();
        googleApiClient.disconnect();
    }


    public int onStartCommand(Intent intent,int flags,int startId){

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

    }
    public void onConnected(Bundle bundle){
        location=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String lat=""+location.getLatitude();
        Toast.makeText(this,lat,Toast.LENGTH_LONG).show();
        createLocRequest();

    }
    public void onLocationChanged(Location loc){

        float distanceInMetres=loc.distanceTo(location);
        String text=""+distanceInMetres;
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
        if(distanceInMetres>20){
            stopSelf();
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

    }
    public void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    public void createLocRequest(){
        locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }
}
