package com.example.quanlyuser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.quanlyuser.MySingleton.MySingleton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtTenUser,edtMaCode,edtTuoi;
    Button btnXacNhan;
    public static final String MyPREFERENCES = "TenUser" ;
    String server_url = "https://thanhvalinh113.000webhostapp.com/InsertUserGoogleMap.php";
    AlertDialog.Builder builder;
    public static final String TenUser = "tenUser";
    public static final String Tuoi = "tuoi";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getApplication().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String nickName = preferences.getString("tenUser","");
        String tuoi = preferences.getString("tuoi","");
        if(nickName != "" && tuoi != "")
        {
            Intent intent = new Intent(MainActivity.this,GoogleMap.class);
            startActivity(intent);


            //dang xuat SharedPreferences
//            SharedPreferences prefs = getApplication().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.clear();
//            editor.commit();
        }
        else
        {
            addView();
            addEvent();
        }

    }

    private void addEvent() {
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //code Loading củ chuối, đang để mặc định 10s
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.holo_blue_bright
                );
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.cancel();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 10000);

                String maCode = edtMaCode.getText().toString();
                String tenUser = edtTenUser.getText().toString();
                String tuoi = edtTuoi.getText().toString();

                if(maCode.equals("IVS"))
                {
                    if (!tenUser.equals("") && !tuoi.equals(""))
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(final String response) {
                                        builder.setTitle("thong tin");
                                        //String Response = String.valueOf(response);
                                        if(response.equals("ok"))
                                        {
                                            builder.setMessage("Đăng nhập thành công !");
                                            SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(TenUser,edtTenUser.getText().toString());
                                            editor.putString(Tuoi,edtTuoi.getText().toString());
                                            editor.commit();
                                        }
                                        else
                                            builder.setMessage("Đăng nhập thất bại !");

                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                edtTenUser.setText("");
                                                edtMaCode.setText("");
                                                edtTuoi.setText("");
                                                if(response.equals("ok"))
                                                {
                                                    Intent intent = new Intent(MainActivity.this,GoogleMap.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT
                                        ).show();


                                    }


                                }

                                , new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this,"Error..",Toast.LENGTH_SHORT
                                ).show();
                                error.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String,String>();
                                params.put("TenUser",edtTenUser.getText().toString());
                                params.put("Tuoi",edtTuoi.getText().toString());
                                return params;
                            }
                        };

                        MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
                    }
                    else
                        Toast.makeText(MainActivity.this,"Vui lòng điên đầy đủ thông tin !",Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(MainActivity.this,"Mã code không đúng !",Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void addView() {
        edtTenUser = findViewById(R.id.edtTenUser);
        edtMaCode = findViewById(R.id.edtMaCode);
        edtTuoi = findViewById(R.id.edtTuoi);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        builder = new AlertDialog.Builder(MainActivity.this);
    }

}
