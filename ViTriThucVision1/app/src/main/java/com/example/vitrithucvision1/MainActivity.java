package com.example.vitrithucvision1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.vitrithucvision1.model.InfoUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback, GoogleApiClient
                .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Marker marker;
    private GoogleMap mMap;
    private Location mLocation;
    LocationManager locationManager;
    private final int RQ_ACCESS_FINE_LOCATION=1;
    RequestQueue mQueue;
    public ArrayList<InfoUser> sim808s = new ArrayList<>();
    String toadoLat,toadoLong;
    Float ViDo,KinhDo;
    String server_url = "https://thanhvalinh113.000webhostapp.com/InsertCurrentUser.php";
    Handler handler = new Handler();
    Runnable refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //kiem tra quyen truy cap vi tri
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Nếu chưa bật GPS sẽ hiện thị thông báo
        AlertDialog();








        refresh = new Runnable() {
            public void run() {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MainActivity.this);
                mQueue = Volley.newRequestQueue(MainActivity.this);
                sim808s.clear();

                //lay du lieu từ sever
                ReadData();

                Toast.makeText(MainActivity.this,"da load sau 1 phut",Toast.LENGTH_SHORT).show();
                handler.postDelayed(refresh, 300000);
            }
        };
        handler.post(refresh);


    }

    private void ReadData() {
        String url= "https://thanhvalinh113.000webhostapp.com/InfoUserLocation.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            mMap.clear();
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




                                    Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_action_car);
                                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);




                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(sim808.getTenUser()+" "+sim808.getTuoi() +" "+ sim808.getTime())
                                            //.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/JPEG_example_flower.jpg/300px-JPEG_example_flower.jpg"))));
                                            .icon(markerIcon));
                                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void AlertDialog()
    {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {


            final AlertDialog.Builder builder= new AlertDialog.Builder(this);

            builder.setMessage("Bạn cần cấp quyền GPS !!").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
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
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this,"vui lòng kiểm tra kết nối !!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //mLocation=location;
        toadoLong = String.valueOf(location.getLongitude());
        toadoLat = String.valueOf(location.getLatitude());

        UpdateCurrent(location);
    }

    private void UpdateCurrent(Location location) {
        if(location != null)
        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            Toast.makeText(MainActivity.this,String.valueOf(location.getLongitude()),Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this,String.valueOf(location.getLatitude()),Toast.LENGTH_SHORT).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
                locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }
            else
            {
                mMap = googleMap;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }

                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                //String locationProvider = LocationManager.NETWORK_PROVIDER;
//                onLocationChanged( mLocation = locationManager.getLastKnownLocation(locationProvider));
                String LocationGps= LocationManager.GPS_PROVIDER;
                String LocationNetwork = LocationManager.NETWORK_PROVIDER;
                String LocationPassive = LocationManager.PASSIVE_PROVIDER;
                if (LocationGps !=null)
                {
                    onLocationChanged( mLocation = locationManager.getLastKnownLocation(LocationGps));
                }
                else if (LocationNetwork !=null)
                {
                    onLocationChanged( mLocation = locationManager.getLastKnownLocation(LocationNetwork));
                }
                else if (LocationPassive !=null)
                {
                    onLocationChanged( mLocation = locationManager.getLastKnownLocation(LocationPassive));
                }
                else
                {
                    Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    //menu
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
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.maplai:
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.mapvetinh:
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.mapdiahinh:
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // khi tat app thi service se goi vi tri toi server


    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this,MyService.class));
    }
}
