package com.example.n5050.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread timer=new Thread(){

            public void run(){
                try{
                    sleep(6000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();

                }
                finally {
                    Intent intent=new Intent(SplashScreen.this,Home.class);
                    startActivity(intent);
                }
            }

        };
        timer.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }
}
