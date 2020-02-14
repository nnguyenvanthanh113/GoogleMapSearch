package com.example.quanlyuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.quanlyuser.MySingleton.MySingleton;
import com.example.quanlyuser.model.InfoUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.quanlyuser.MainActivity.MyPREFERENCES;

public class GoogleMap extends AppCompatActivity implements OnMapReadyCallback {


    private static  final int REQUEST_LOCATION=1;
    private final Handler handler = new Handler();
    LocationManager locationManager;
    String latitude,longitude;
    com.google.android.gms.maps.GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Float ViDo,KinhDo;
    RequestQueue mQueue;
    public ArrayList<InfoUser> sim808s = new ArrayList<>();
    Float toadoLat,toadoLong;
    String server_url = "https://thanhvalinh113.000webhostapp.com/InsertCurrentUser.php";
    //AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        //ham addEvent thuc hien công việc chính
        addEvent();

        //Refresh();






    }

    private void Refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                doTheAutoRefresh();
            }

            private void doTheAutoRefresh() {
                addEvent();
                Toast.makeText(GoogleMap.this,"Loading after 5 minute !",Toast.LENGTH_SHORT).show();
            }
        }, 300000);

    }

    private void addEvent() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googlemap);



        searchView = findViewById(R.id.sv_location);


        mQueue = Volley.newRequestQueue(GoogleMap.this);

        //cap nhat vi tri hien tai cua User
        CapNhatViTri();

        //Dọc du lieu tu website
        Doc();

        //tim dia diem qua searchView
        Search();
    }

    private void CapNhatViTri() {
        //Add permission
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps

            OnGPS();
        }
        else
        {
            //lay TenUser từ điện thoại đã lưu trong Sharepreference
            SharedPreferences preferences = getApplication().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String nickName = preferences.getString("tenUser","");
            Toast.makeText(GoogleMap.this,nickName,Toast.LENGTH_SHORT).show();
            //GPS is already On then
            getLocation();

            //Toast.makeText(GoogleMap.this,latitude+" NOW "+longitude,Toast.LENGTH_SHORT).show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            //builder.setTitle("thong tin");
                            //String Response = String.valueOf(response);
                            if(response.equals("ok"))
                            {
                                Toast.makeText(GoogleMap.this,"cập nhật vị trí hiện tại thành công !",Toast.LENGTH_SHORT).show();
                                //Toast.makeText(GoogleMap.this,nickName,Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(GoogleMap.this,"cập nhật vị trí hiện tại thất bại !",Toast.LENGTH_SHORT).show();

//                            Toast.makeText(GoogleMap.this,response,Toast.LENGTH_SHORT
//                            ).show();


                        }


                    }

                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(GoogleMap.this,"Error cập nhật vị trí..",Toast.LENGTH_SHORT
                    ).show();
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("ViDo",latitude);
                    params.put("KinhDo",longitude);
                    params.put("TenUser",nickName);
                    return params;
                }
            };

            MySingleton.getInstance(GoogleMap.this).addTorequestque(stringRequest);

        }

    }


    private void getLocation() {
        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(GoogleMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleMap.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();

                latitude = String.valueOf(lat);
                longitude=String.valueOf(longi);

                toadoLat = Float.parseFloat(latitude);
                toadoLong = Float.parseFloat(longitude);


                //Toast.makeText(GoogleMap.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                toadoLat = Float.parseFloat(latitude);
                toadoLong = Float.parseFloat(longitude);

                //Toast.makeText(GoogleMap.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                toadoLat = Float.parseFloat(latitude);
                toadoLong = Float.parseFloat(longitude);

                //Toast.makeText(GoogleMap.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }
    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


    private void Search() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;


                if(location !=null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(GoogleMap.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                        Toast.makeText(GoogleMap.this,"khong tim thấy vị trí !",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if(addressList.get(0) != null || !addressList.get(0).equals(""))
                    {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title(location));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        //System.out.print("123");
                    }
                    else
                    {
                        Toast.makeText(GoogleMap.this,"khong tim thấy vị trí !",Toast.LENGTH_SHORT).show();
                        ///System.out.print("321");

                    }

                }
                else {
                    LatLng latLng = new LatLng(12.268536, 109.201012);
                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,50));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }

    private void Doc() {
        String url= "https://thanhvalinh113.000webhostapp.com/InfoUserLocation.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            if (response != null) {
                                for(int i = 0; i < response.length(); i++){
                                    JSONObject obj = response.getJSONObject(i);
                                    sim808s.add(new InfoUser(
                                            obj.getString("Id"),
                                            obj.getString("Time"),
                                            obj.getString("ViDo"),
                                            obj.getString("KinhDo"),
                                            obj.getString("MaUser"),
                                            obj.getString("TenUser"),
                                            obj.getString("Tuoi")
                                    ));


                                    InfoUser sim808 = sim808s.get(i);

                                ViDo = Float.parseFloat(sim808.getViDo());
                                KinhDo = Float.parseFloat(sim808.getKinhDo());
                                // Toast.makeText(MainActivity.this,ViDo+" "+KinhDo,Toast.LENGTH_LONG).show();

                                LatLng latLng = new LatLng(ViDo,KinhDo);
                                map.addMarker(new MarkerOptions().position(latLng).title(sim808.getTenUser()+" "+sim808.getTuoi() +" "+ sim808.getTime()));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapbinhthuong:
                map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.maplai:
                map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.mapvetinh:
                map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.mapdiahinh:
                map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case  R.id.action_location:
                //Add permission
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    //Write Function To enable gps

                    OnGPS();
                }
                else
                {
                    //GPS is already On then

                    getLocation();
                    LatLng latLng = new LatLng(toadoLat,toadoLong);
                    map.addMarker(new MarkerOptions().position(latLng).title("vị trí của ban !").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
                return true;
            case R.id.action_refresh:
                handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Write code for your refresh logic
                    doTheAutoRefresh();
                }

                private void doTheAutoRefresh() {

//                    //remove arraylist
//                    sim808s.clear();
//
//                    //Toast.makeText(GoogleMap.this,"da load sau 1s",Toast.LENGTH_SHORT).show();
//                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.googlemap);
//
//
//
//                    searchView = findViewById(R.id.sv_location);
//
//
//                    mQueue = Volley.newRequestQueue(GoogleMap.this);
//
//                    //cap nhat vi tri hien tai cua User
//                    CapNhatViTri();
//
//
//                    //Dọc du lieu tu website
//                    Doc();
//
//                    //tim dia diem qua searchView
//                    Search();

                    Intent intent = new Intent(GoogleMap.this,MainActivity.class);
                    startActivity(intent);


                }
            }, 500);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;
    }
}
