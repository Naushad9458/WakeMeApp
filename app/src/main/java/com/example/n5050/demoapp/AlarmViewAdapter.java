package com.example.n5050.demoapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by n5050 on 4/10/2017.
 */
public class AlarmViewAdapter extends RecyclerView.Adapter<AlarmViewAdapter.AlarmViewholder> {

    View view;
    private Cursor cursor;
    Context context;


    public AlarmViewAdapter(Context c){
        this.context=c;

    }


    public AlarmViewholder onCreateViewHolder(ViewGroup viewGroup,int viewType){
        Context context=viewGroup.getContext();
        int layoutForList= R.layout.alarm_list;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately=false;
        view=layoutInflater.inflate(layoutForList,viewGroup,shouldAttachToParentImmediately);
        AlarmViewholder alarmViewholder=new AlarmViewholder(view);


        return alarmViewholder;
    }

    public void onBindViewHolder(AlarmViewholder viewholder,int position){

        if(!cursor.moveToPosition(position))
            return;
        String time=cursor.getString(cursor.getColumnIndex(AlarmListContract.AlarmList.COLUMN_ALARM_TIME));
        int id=cursor.getInt(cursor.getColumnIndex(AlarmListContract.AlarmList.COLUMN_PENDING_INTENT));

        viewholder.alarmTime.setText(time);

        viewholder.itemView.setTag(id);

    }
    public int getItemCount(){
        if(cursor==null)
            return 0;

        return cursor.getCount();

    }

    public void swapCursor(Cursor c){
        if(cursor!=null)
            cursor.close();
        cursor=c;
        if(c!=null)
            this.notifyDataSetChanged();
    }

    class AlarmViewholder extends RecyclerView.ViewHolder{
        TextView alarmTime;
        ImageView imageView;

         public  AlarmViewholder(View v){
           super(v);
           alarmTime=(TextView) v.findViewById(R.id.alarmtime);
             imageView=(ImageView) v.findViewById(R.id.alarmIcon);
         }
        void bind(int listIndex){
         alarmTime.setText(String.valueOf(listIndex));
        }

    }
}
