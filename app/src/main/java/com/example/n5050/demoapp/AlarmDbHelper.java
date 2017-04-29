package com.example.n5050.demoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by n5050 on 4/13/2017.
 */
public class AlarmDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="alarm.db";
    public static final int DATABASE_VERSION=1;

    public  AlarmDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_ALARM_TABLE="CREATE TABLE "+ AlarmListContract.AlarmList.TABLE_NAME+" ("+
                AlarmListContract.AlarmList._ID +" INTEGER PRIMARY KEY," +
                AlarmListContract.AlarmList.COLUMN_PENDING_INTENT + " VARCHAR NOT NULL," +
                AlarmListContract.AlarmList.COLUMN_ALARM_TIME + " VARCHAR NOT NULL," +
                AlarmListContract.AlarmList.COLUMN_IS_ACTIVE + " VARCHAR NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);


    }
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ AlarmListContract.AlarmList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
