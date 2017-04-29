package com.example.n5050.demoapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar volumeControl;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        volumeControl=(SeekBar) findViewById(R.id.volumeSeekbar);
        audioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        volumeControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeControl.setKeyProgressIncrement(1);
        volumeControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    public void openDialog(View view){
        Intent intent=new Intent(SettingsActivity.this,SelectTone.class);
        startActivity(intent);
    }
}
