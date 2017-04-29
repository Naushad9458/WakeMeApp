package com.example.n5050.demoapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TurnOff extends AppCompatActivity {
    Intent intent;
    String mathExpression;
    NotificationManager notificationManager;
    TextView textView;
    int answer;
    EditText answerText;
    SQLiteDatabase database;
    int reqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_off);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            reqCode=extras.getInt("reqCode");
        }
        textView=(TextView) findViewById(R.id.displayAlarm);
        mathExpression=createExpression();

        textView.setText(mathExpression);
        intent=new Intent(this,PlaySound.class);

        startService(intent);
        showNotification(this);
        AlarmDbHelper dbHelper=new AlarmDbHelper(this);
        database=dbHelper.getWritableDatabase();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent){
        int action=keyEvent.getAction();
        int code=keyEvent.getKeyCode();
        switch (code){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(action==KeyEvent.ACTION_UP){

                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(action==KeyEvent.ACTION_DOWN){

                }
                return true;
            default:
                return super.dispatchKeyEvent(keyEvent);

        }
    }
    public void onPause(){
        super.onPause();
        ActivityManager activityManager=(ActivityManager) getApplicationContext().
                getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(),0);
    }
    public String createExpression(){
        String op1="";
        String op2="";
        int num1=(int) (Math.random()*30)+1;
        int num2=(int) (Math.random()*10)+1;
        int num3=(int) (Math.random()*50)+1;
        int operator1=3;
        int operator2=(int) (Math.random()*2)+1;
        if(operator1==1)
            op1="+";
        if(operator1==2)
            op1="-";
        if(operator1==3)
            op1="*";
        if(operator1==4)
            op1="/";

        if(operator2==1)
            op2="+";
        if(operator2==2)
            op2="-";
        if(operator2==3)
            op2="*";
        if(operator2==4)
            op2="/";



        /*switch (operator1){
            case 1:
                op1="+";
            case 2:
                op1="-";
            case 3:
                op1="*";
            case 4:
                op1="/";
        }
        switch (operator2){
            case 1:
                op2="+";
            case 2:
                op2="-";
            case 3:
                op2="*";
            case 4:
                op2="/";
        }*/
        if(operator2==1){
            answer=num1*num2+num3;
        }
        else
        if(operator2==2){
            answer=num1*num2-num3;
        }

        String expr=num1+op1+num2+op2+num3;
        return expr;
    }
    public void showNotification(Context context){
        NotificationCompat.Builder notificationBuilder= (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentTitle("Alarm Activated")
                .setContentText("Tap to turn off")
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("ALAARMMM!"))
                .setContentIntent(contentIntent(context))
                .setOngoing(true);

        notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(878887,notificationBuilder.build());

    }
    private static PendingIntent contentIntent(Context context){
        Intent intent=new Intent(context,TurnOff.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return PendingIntent.getActivity(context,878887,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public void alarmOff(View view) {
        String ans=""+answer;
        answerText=(EditText) findViewById(R.id.answerText);
        if(answerText.getText().toString().trim().equals("")){
            answerText.setError("Type in the answer first!");
        }
        else
        if(answerText.getText().toString().trim().equals(ans)){
        
            stopService(intent);
            notificationManager.cancel(878887);
            deleteAlarm();
            finish();
        }
        else{
            Toast.makeText(this,"Incorrect Answer",Toast.LENGTH_LONG).show();
        }
    }
    public void onBackPressed(){
        moveTaskToBack(true);

    }

    public void deleteAlarm(){
        String query="UPDATE "+ AlarmListContract.AlarmList.TABLE_NAME+" SET "+
                AlarmListContract.AlarmList.COLUMN_IS_ACTIVE+"=0 WHERE "+
                AlarmListContract.AlarmList.COLUMN_PENDING_INTENT+"="+reqCode+";";
        database.execSQL(query);

    }


}
