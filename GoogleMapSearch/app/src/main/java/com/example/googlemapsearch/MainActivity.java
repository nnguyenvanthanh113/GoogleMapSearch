package com.example.googlemapsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.example.googlemapsearch.Model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Float ViDo,KinhDo;
    RequestQueue mQueue;
    public  ArrayList<Location> locations = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.googlemap);

        mQueue = Volley.newRequestQueue(MainActivity.this);

        //Dọc du lieu tu website
        Doc();

        //tim dia diem qua searchView
        Search();


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
                                    locations.add(new Location(
                                            obj.getString("Id"),
                                            obj.getString("Time"),
                                            obj.getString("ViDo"),
                                            obj.getString("KinhDo")

                                    ));


                                }


                                    Location location = locations.get(locations.size()-1);
                                    //Toast.makeText(MainActivity.this,location.getId(),Toast.LENGTH_LONG).show();
                                    ViDo = Float.parseFloat(location.getViDo());
                                    KinhDo = Float.parseFloat(location.getKinhDo());
                                    Toast.makeText(MainActivity.this,ViDo+" "+KinhDo,Toast.LENGTH_LONG).show();

                                LatLng latLng = new LatLng(ViDo,KinhDo);
                                map.addMarker(new MarkerOptions().position(latLng).title("vi tri hien tai"));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            }
                            //adapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.map_option, menu);
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

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

//        if(ViDo == null && KinhDo == null)
//        {
//
//        }
//        else
//        {
//            //LatLng latLng = new LatLng(21.026902, 105.833015);
//            LatLng latLng = new LatLng(ViDo,KinhDo);
//            map.addMarker(new MarkerOptions().position(latLng).title("Hà Nội"));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//        }

    }
}
