package com.example.n5050.demoapp;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by n5050 on 3/16/2017.
 */
public class Receiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){


        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE|PowerManager.FULL_WAKE_LOCK,"Wake");
        wl.acquire();
        Vibrator v=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(2000);
        Intent i=new Intent(context,TurnOff.class);
        i.putExtra("reqCode",intent.getIntExtra("reqCode",0));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Toast.makeText(context,"Alarm Activated", Toast.LENGTH_LONG).show();
        wl.release();
    }
}
