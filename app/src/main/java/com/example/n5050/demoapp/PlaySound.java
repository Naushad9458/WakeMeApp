package com.example.n5050.demoapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by n5050 on 3/17/2017.
 */
public class PlaySound extends Service {
    MediaPlayer mp;
    public IBinder onBind(Intent intent){
        return null;
    }
    public void onCreate(){
        mp=MediaPlayer.create(this,R.raw.ring);
        mp.setLooping(true);

    }
    public void onDestroy(){
        mp.stop();

    }
    public int onStartCommand(Intent intent,int flag,int startId){
        mp.start();
        return Service.START_STICKY;

    }
}
