package com.example.n5050.demoapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class SetGpsAlarm extends AppCompatActivity {
    EditText editText;
    String stationName;
    double latitude;
    double longitude;
    String stationCode;
    String url;
    Intent intent;
    TextView alarmDetails;
    SharedPreferences sharedPreferences;
    ImageButton btnDeleteAlarm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gps_alarm);
        alarmDetails=(TextView) findViewById(R.id.alarmDetails);
        btnDeleteAlarm=(ImageButton) findViewById(R.id.deleteAlarm);
        sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        if(sharedPreferences.getString("stationName","").length()>0) {
            alarmDetails.setVisibility(View.VISIBLE);
            btnDeleteAlarm.setVisibility(View.VISIBLE);

            alarmDetails.setText("Travel Alarm Active for " + sharedPreferences.getString("stationName",""));
        }

        editText=(EditText) findViewById(R.id.stationCode);
        if(!((LocationManager) this.getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSAlert();

        }


    }
    @Override
    public void onResume(){
        super.onResume();
        sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        if(sharedPreferences.getString("stationName","").length()>0) {
            alarmDetails.setVisibility(View.VISIBLE);
            btnDeleteAlarm.setVisibility(View.VISIBLE);

            alarmDetails.setText("Travel Alarm Active for " + sharedPreferences.getString("stationName",""));
        }
        else {
            alarmDetails.setVisibility(View.GONE);
            btnDeleteAlarm.setVisibility(View.GONE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
    }

    private void showGPSAlert(){
        AlertDialog alertDialog=new AlertDialog.Builder(SetGpsAlarm.this).create();
        alertDialog.setTitle("GPS Turned Off!");
        alertDialog.setMessage("Switch it on?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }




    public void fetchData(View view) {
        stationCode=editText.getText().toString().trim().toLowerCase().replace(" ","");
        if (editText.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Station Code", Toast.LENGTH_SHORT).show();

        }
        else
        if(sharedPreferences.getString("stationName","").length()>0){
            final AlertDialog alertDialog=new AlertDialog.Builder(SetGpsAlarm.this).create();
            alertDialog.setTitle("A Travel Alarm is already Active!");
            alertDialog.setMessage("You must delete existing one before setting up a new alarm.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okey", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        }
        else {
            url=Config.BASE_URL+stationCode+"/apikey/"+Config.API_KEY;
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Fetching Data.Please Wait...");
            progressDialog.show();
            Log.e("code",stationCode);
            Log.e("url",url);
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Response", "Success " + response.length());
                    Log.e("Response", response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        Log.e("Response", "JsonObject OK");
                        JSONArray jsonArray = jsonObject.getJSONArray("stations");
                        Log.e("Response", "JsonArray OK Length= " + jsonArray.length());
                        if (jsonArray.length() == 0) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Incorrect Station Code",Toast.LENGTH_LONG).show();

                        }
                        else {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String c = object.getString("code");
                                Log.e("Response", c + " " + i);
                                if (c.equals(stationCode.toUpperCase())) {

                                    stationName = object.getString("fullname");
                                    latitude = object.getDouble("lat");
                                    longitude = object.getDouble("lng");

                                    Log.e("stationName", stationName);
                                    Log.e("lon", "" + longitude);
                                    Log.e("lat", "" + latitude);
                                    Intent intent = new Intent(SetGpsAlarm.this, GpsAlarm.class);
                                    intent.putExtra("stationName", stationName);
                                    intent.putExtra("longitude", longitude);
                                    intent.putExtra("latitude", latitude);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    break;
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    Log.e("Volley Error",error.getMessage());

                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


        }


    }

    public void deleteAlarm(View view) {
        Intent intent=new Intent(this,FetchLocation.class);
        stopService(intent);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(87123);
        SharedPreferences sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
        alarmDetails.setVisibility(View.GONE);
        btnDeleteAlarm.setVisibility(View.GONE);

    }
}
