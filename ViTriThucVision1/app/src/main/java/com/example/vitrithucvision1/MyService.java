package com.example.vitrithucvision1;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.vitrithucvision1.MySingleton.MySingleton;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service
{
    public static final String MyPREFERENCES = "TenUser" ;
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private final int RQ_ACCESS_FINE_LOCATION=1;
    LocationManager locationManager;
    String latitude,longitude;
    String server_url = "https://thanhvalinh113.000webhostapp.com/InsertCurrentUser.php";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
//        SharedPreferences preferences = getApplication().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        final String nickName = preferences.getString("tenUser","");
//        Toast.makeText(MyService.this,nickName,Toast.LENGTH_SHORT).show();

        //kiem tra quyen truy cap vi tri

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                getLocation();
                //handler.postDelayed(runnable, 300000);
                handler.postDelayed(runnable, 60000);
            }
        };

        handler.post(runnable);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyService.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
//            ActivityCompat.requestPermissions(this,new String[]
//                    {Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
        }
        else {

            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi = LocationGps.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

//                toadoLat = Float.parseFloat(latitude);
//                toadoLong = Float.parseFloat(longitude);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                //builder.setTitle("thong tin");
                                //String Response = String.valueOf(response);
                                if(response.equals("ok"))
                                {
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thành công !",Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(GoogleMap.this,nickName,Toast.LENGTH_SHORT).show();

                                }
                                else
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thất bại !",Toast.LENGTH_SHORT).show();

//                            Toast.makeText(GoogleMap.this,response,Toast.LENGTH_SHORT
//                            ).show();


                            }


                        }

                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyService.this,"Error cập nhật vị trí..",Toast.LENGTH_SHORT
                        ).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("ViDo",latitude);
                        params.put("KinhDo",longitude);
                        params.put("TenUser","NguyenThanh");
                        return params;
                    }
                };

                MySingleton.getInstance(MyService.this).addTorequestque(stringRequest);


                Toast.makeText(MyService.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
//                toadoLat = Float.parseFloat(latitude);
//                toadoLong = Float.parseFloat(longitude);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                //builder.setTitle("thong tin");
                                //String Response = String.valueOf(response);
                                if(response.equals("ok"))
                                {
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thành công !",Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(GoogleMap.this,nickName,Toast.LENGTH_SHORT).show();

                                }
                                else
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thất bại !",Toast.LENGTH_SHORT).show();

//                            Toast.makeText(GoogleMap.this,response,Toast.LENGTH_SHORT
//                            ).show();


                            }


                        }

                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyService.this,"Error cập nhật vị trí..",Toast.LENGTH_SHORT
                        ).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("ViDo",latitude);
                        params.put("KinhDo",longitude);
                        params.put("TenUser","NguyenThanh");
                        return params;
                    }
                };

                MySingleton.getInstance(MyService.this).addTorequestque(stringRequest);

                Toast.makeText(MyService.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
//                toadoLat = Float.parseFloat(latitude);
//                toadoLong = Float.parseFloat(longitude);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                //builder.setTitle("thong tin");
                                //String Response = String.valueOf(response);
                                if(response.equals("ok"))
                                {
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thành công !",Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(GoogleMap.this,nickName,Toast.LENGTH_SHORT).show();

                                }
                                else
                                    Toast.makeText(MyService.this,"cập nhật vị trí hiện tại thất bại !",Toast.LENGTH_SHORT).show();

//                            Toast.makeText(GoogleMap.this,response,Toast.LENGTH_SHORT
//                            ).show();


                            }


                        }

                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyService.this,"Error cập nhật vị trí..",Toast.LENGTH_SHORT
                        ).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("ViDo",latitude);
                        params.put("KinhDo",longitude);
                        params.put("TenUser","NguyenThanh");
                        return params;
                    }
                };

                MySingleton.getInstance(MyService.this).addTorequestque(stringRequest);

                Toast.makeText(MyService.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service started by ThanhMickey.", Toast.LENGTH_LONG).show();
    }

}
