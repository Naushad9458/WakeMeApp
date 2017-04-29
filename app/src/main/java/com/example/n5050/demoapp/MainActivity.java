package com.example.n5050.demoapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    NotificationManager notificationManager;
    int reqCode;
    AlarmDbHelper alarmDbHelper;
    SQLiteDatabase db;
    String timeDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmDbHelper = new AlarmDbHelper(this);
        db = alarmDbHelper.getWritableDatabase();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void startAlarm(View view) {
        int rowCount = 0;
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        timeDisplay = getTimeDisplay(hour, min);

        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        calSet.set(Calendar.HOUR_OF_DAY, hour);
        calSet.set(Calendar.MINUTE, min);
        calSet.set(Calendar.SECOND, 0);

        if (calSet.compareTo(calNow) < 0) {
            calSet.add(Calendar.DATE, 1);
        }


        long dispHours = ((calSet.getTimeInMillis() - calNow.getTimeInMillis()) / 1000) / 3600;
        long dispMins = (((calSet.getTimeInMillis() - calNow.getTimeInMillis()) / 1000) % 3600) / 60;
        dispMins++;

        String alert;
        if (dispHours == 0) {
            alert = "Alarm Scheduled " + dispMins + " minutes from now.";
        } else {
            alert = "Alarm Scheduled " + dispHours + " hours and " + dispMins + " minutes from now.";
        }
        reqCode = (int) calSet.getTimeInMillis();


        Intent intent = new Intent(this, Receiver.class);
        intent.putExtra("reqCode", reqCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);


        Toast.makeText(this, alert, Toast.LENGTH_LONG).show();
        alarmSetNotify(reqCode, timeDisplay);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmListContract.AlarmList.COLUMN_PENDING_INTENT, reqCode);
        contentValues.put(AlarmListContract.AlarmList.COLUMN_ALARM_TIME, hour + ":" + min);
        contentValues.put(AlarmListContract.AlarmList.COLUMN_IS_ACTIVE, 1);
        Uri uri = getContentResolver().insert(AlarmListContract.AlarmList.CONTENT_URI, contentValues);
        //insertDb(reqCode, hour + ":" + min);


    }

    public void insertDb(int reqCode, String time) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmListContract.AlarmList.COLUMN_PENDING_INTENT, reqCode);
        contentValues.put(AlarmListContract.AlarmList.COLUMN_ALARM_TIME, time);
        contentValues.put(AlarmListContract.AlarmList.COLUMN_IS_ACTIVE, 1);
        db.insert(AlarmListContract.AlarmList.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void alarmSetNotify(int requestCode, String time) {
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentTitle("Alarm Scheduled")
                .setContentText(time)
                .setSmallIcon(R.mipmap.icon);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, notificationBuilder.build());


    }

    private Cursor checkAlarm(String s) {
        String query = "SELECT * FROM " + AlarmListContract.AlarmList.TABLE_NAME + " WHERE " +
                AlarmListContract.AlarmList.COLUMN_ALARM_TIME + "='" + s + "' AND " +
                AlarmListContract.AlarmList.COLUMN_IS_ACTIVE + "=1;";
        return db.rawQuery(query, null);

    }

    public String getTimeDisplay(int h, int m) {
        String temp = "";
        if (h == 0) {
            if (m < 10)
                temp = "12:0" + m + " AM";
            else
                temp = "12:" + m + " AM";
        }
        if (h == 12) {
            if (m < 10)
                temp = "12:0" + m + " PM";
            else
                temp = "12:" + m + " PM";

        } else if (h > 0 && h < 10) {
            if (m < 10)
                temp = "0" + h + ":0" + m + " AM";
            else
                temp = "0" + h + ":" + m + " AM";
        } else if (h > 10 && h < 13) {
            if (m < 10)
                temp = "" + h + ":0" + m + " AM";
            else
                temp = "" + h + ":" + m + " AM";
        } else if (h > 12) {
            h = h - 12;
            if (m < 10)
                temp = "0" + h + ":0" + m + " PM";
            else
                temp = "0" + h + ":" + m + " PM";
        }
        return temp;
    }
}
