package com.example.currentgooglemap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.currentgooglemap.model.Sim808;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static  final int REQUEST_LOCATION=1;

    LocationManager locationManager;
    String latitude,longitude;
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Float ViDo,KinhDo;
    RequestQueue mQueue;
    public ArrayList<Sim808> sim808s = new ArrayList<>();
    Float toadoLat,toadoLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googlemap);



        searchView = findViewById(R.id.sv_location);


        mQueue = Volley.newRequestQueue(MainActivity.this);

        //Dọc du lieu tu website
        Doc();

        //tim dia diem qua searchView
        Search();





    }

    private void getLocation() {
        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,

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


                Toast.makeText(MainActivity.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                toadoLat = Float.parseFloat(latitude);
                toadoLong = Float.parseFloat(longitude);

                Toast.makeText(MainActivity.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                toadoLat = Float.parseFloat(latitude);
                toadoLong = Float.parseFloat(longitude);

                Toast.makeText(MainActivity.this,latitude+" "+longitude,Toast.LENGTH_SHORT).show();
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
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
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
                        Toast.makeText(MainActivity.this,"khong tim thấy vị trí !",Toast.LENGTH_SHORT).show();
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
        String url= "https://thanhvalinh113.000webhostapp.com/SIM_808_Location.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            if (response != null) {
                                for(int i = 0; i < response.length(); i++){
                                    JSONObject obj = response.getJSONObject(i);
                                    sim808s.add(new com.example.currentgooglemap.model.Sim808(
                                            obj.getString("Id"),
                                            obj.getString("Time"),
                                            obj.getString("ViDo"),
                                            obj.getString("KinhDo")

                                    ));


                                }


                                Sim808 sim808 = sim808s.get(sim808s.size()-1);

                                ViDo = Float.parseFloat(sim808.getViDo());
                                KinhDo = Float.parseFloat(sim808.getKinhDo());
                               // Toast.makeText(MainActivity.this,ViDo+" "+KinhDo,Toast.LENGTH_LONG).show();

                                LatLng latLng = new LatLng(ViDo,KinhDo);
                                map.addMarker(new MarkerOptions().position(latLng).title("vi tri IOT"));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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
                }
                LatLng latLng = new LatLng(toadoLat,toadoLong);
                map.addMarker(new MarkerOptions().position(latLng).title("vitritam").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


    }
}
