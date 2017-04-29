package com.example.n5050.demoapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by n5050 on 4/20/2017.
 */
public class AlarmContentProvider extends ContentProvider {

    public static final int ALARMS=100;
    public static final int ALARM_WITH_REQCODE=101;
    public static final int ALARM_WITH_PI=102;
    public static final UriMatcher sURI_MATCHER=buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AlarmListContract.AUTHORITY,AlarmListContract.PATH_ALARMS,ALARMS);
        uriMatcher.addURI(AlarmListContract.AUTHORITY,AlarmListContract.PATH_ALARMS+ "/#",ALARM_WITH_REQCODE);
        uriMatcher.addURI(AlarmListContract.AUTHORITY,AlarmListContract.PATH_ALARMS+ "/*",ALARM_WITH_PI);
        return uriMatcher;
    }
    private AlarmDbHelper alarmDbHelper;

    @Override
    public boolean onCreate(){
        Context context=getContext();
        alarmDbHelper=new AlarmDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues){
        final SQLiteDatabase db=alarmDbHelper.getWritableDatabase();
        int match=sURI_MATCHER.match(uri);

        Uri returnUri;

        switch (match){
            case ALARMS:
                long id=db.insert(AlarmListContract.AlarmList.TABLE_NAME,null,contentValues);
                if(id>0){
                    returnUri= ContentUris.withAppendedId(AlarmListContract.AlarmList.CONTENT_URI,id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into"+ uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri"+ uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }
    @Override
    public Cursor query(@NonNull Uri uri,String[] projection,String selection,
    String[] selectionArgs,String sortOrder){
        final SQLiteDatabase db=alarmDbHelper.getReadableDatabase();
        int match=sURI_MATCHER.match(uri);

        Cursor cursor;
        switch (match){
            case ALARMS:
                cursor=db.query(AlarmListContract.AlarmList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ALARM_WITH_REQCODE:
                String id=uri.getPathSegments().get(1);
                String mSelection=AlarmListContract.AlarmList.COLUMN_IS_ACTIVE+"=?";
                String[] mSelectionArgs=new String[]{id};
                cursor=db.query(AlarmListContract.AlarmList.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


            default:
                throw new UnsupportedOperationException("Unknown Uri "+ uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    @Override
    public int delete(@NonNull Uri uri,String selection,String[] selectionArgs){

        final SQLiteDatabase db=alarmDbHelper.getReadableDatabase();
        int match=sURI_MATCHER.match(uri);
        int rowsDeleted;

        switch (match){
            case ALARM_WITH_PI:
                String reqCode=uri.getPathSegments().get(1);
                String mSelection= AlarmListContract.AlarmList.COLUMN_PENDING_INTENT+"=?";
                String[] mSelectionArgs=new String[]{reqCode};
                rowsDeleted=db.delete(AlarmListContract.AlarmList.TABLE_NAME,mSelection,mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri"+ uri);

        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }
    @Override
    public int update(@NonNull Uri uri,ContentValues contentValues,String selection,String[] selectionArgs){
        throw new UnsupportedOperationException("xxx");
    }
    @Override
    public String getType(@NonNull Uri uri){
        throw new UnsupportedOperationException("xxx");
    }

}
