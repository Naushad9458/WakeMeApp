package com.example.n5050.demoapp;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private AlarmViewAdapter alarmViewAdapter;
    private RecyclerView alarmTimeList;
    private SQLiteDatabase database;
    Cursor cursor;
    int id;
    public static final int TASK_LOADER_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //AlarmDbHelper dbHelper=new AlarmDbHelper(this);
        //database=dbHelper.getWritableDatabase();

        //cursor=getAlarms();

        alarmTimeList=(RecyclerView) findViewById(R.id.rv_alarm_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        alarmTimeList.setLayoutManager(layoutManager);
        alarmTimeList.setHasFixedSize(true);
        alarmViewAdapter=new AlarmViewAdapter(this);
        alarmTimeList.setAdapter(alarmViewAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                id=(int) viewHolder.itemView.getTag();

                AlertDialog alertDialog=new AlertDialog.Builder(Home.this).create();
                alertDialog.setTitle("Cancel Alarm");
                alertDialog.setMessage("Sure?");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sId=Integer.toString(id);
                        Uri uri= AlarmListContract.AlarmList.CONTENT_URI;
                        uri=uri.buildUpon().appendPath(sId).build();
                        cancelAlarm(id);
                        getContentResolver().delete(uri,null,null);

                        //alarmViewAdapter.swapCursor(getAlarms());
                        //Cursor c=getAlarms();
                        //alarmTimeList.setAdapter(new AlarmViewAdapter(c));
                        getLoaderManager().restartLoader(TASK_LOADER_ID,null,Home.this);

                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getLoaderManager().restartLoader(TASK_LOADER_ID,null,Home.this);
                    }
                });
                alertDialog.show();


            }
        }).attachToRecyclerView(alarmTimeList);

        getLoaderManager().initLoader(TASK_LOADER_ID,null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.action_settings){
            Intent intent=new Intent(Home.this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        getLoaderManager().restartLoader(TASK_LOADER_ID,null,this);
        //Cursor cursor=getAlarms();
        //alarmTimeList.setAdapter(new AlarmViewAdapter(cursor));
    }

    public void openAlarmActivity(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void openTravelAlarm(View view){
        Intent intent=new Intent(this,SetGpsAlarm.class);
        startActivity(intent);
    }
    private Cursor getAlarms(){
        String query="SELECT * FROM "+ AlarmListContract.AlarmList.TABLE_NAME +" WHERE "+AlarmListContract.AlarmList.COLUMN_IS_ACTIVE+" =1;";
        //String query="SELECT "+ AlarmListContract.AlarmList.COLUMN_ALARM_TIME+" FROM "+ AlarmListContract.AlarmList.TABLE_NAME+" WHERE "+
        //        AlarmListContract.AlarmList.COLUMN_IS_ACTIVE+"='1';";
        return database.rawQuery(query,null);

    }
    private void cancelAlarm(int id){
        Intent intent=new Intent(this,Receiver.class);
        PendingIntent.getBroadcast(this,id,intent,PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,final Bundle loaderArgs){
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor c=null;

            @Override
            protected void onStartLoading(){
                if(c!=null){
                    deliverResults(c);
                }
                else {
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {
                try{
                    Uri uri= AlarmListContract.AlarmList.CONTENT_URI;
                    uri=uri.buildUpon().appendPath("1").build();
                    return getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);
                }
                catch (Exception e){
                    Log.e("WakeMeApp","Failed to load data");
                    e.printStackTrace();
                    return null;

                }
            }
            public void deliverResults(Cursor data){
                c=data;
                super.deliverResult(data);
            }

        };
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor){
        alarmViewAdapter.swapCursor(cursor);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        alarmViewAdapter.swapCursor(null);
    }

}
