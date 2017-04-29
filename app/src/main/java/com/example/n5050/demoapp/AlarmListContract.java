package com.example.n5050.demoapp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by n5050 on 4/10/2017.
 */
public class AlarmListContract {

    private AlarmListContract(){

    }
    public static final String AUTHORITY="com.example.n5050.demoapp";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+AUTHORITY);
    public static final String PATH_ALARMS="alarms";

    public static final class AlarmList implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();
        public static final String TABLE_NAME="alarms";
        public static final String COLUMN_PENDING_INTENT="pendingIntent";
        public static final String COLUMN_ALARM_TIME="alarmTime";
        public static final String COLUMN_IS_ACTIVE="isActive";

    }
}
