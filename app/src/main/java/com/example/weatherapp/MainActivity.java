package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRl;
    private ProgressBar progressBar;
    private TextView cityNameTV, temperatureTV,weatherConditionTV;
    private TextInputEditText enterCityNameET;
    private ImageView searchIV,weatherIconIV,backgroundIV;
    private RecyclerView weatherRV;

    ArrayList<weatherRVModel> list;
    weatherRVAdaptor adaptor;

    LocationManager locationManager;
    private static final int PERMISSION_NO = 1;

    String NightImage = "https://i.pinimg.com/originals/e7/43/00/e74300718c18c6b26e38878f49137b93.jpg";
    String DayImage = "https://i.pinimg.com/originals/bd/a2/67/bda2671c3b78f9ba9f0264f59782e1cc.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags
                (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        homeRl = findViewById(R.id.Home);
        progressBar = findViewById(R.id.ProgressBar);
        cityNameTV = findViewById(R.id.cityNameTextView);
        temperatureTV = findViewById(R.id.temperatureTextView);
        weatherConditionTV = findViewById(R.id.weatherConditionTextView);
        enterCityNameET = findViewById(R.id.enterCityNameEditText);
        searchIV = findViewById(R.id.searchImageView);
        weatherIconIV = findViewById(R.id.weatherIconImageView);
        backgroundIV = findViewById(R.id.backgroundImageView);
        weatherRV = findViewById(R.id.recyclerView);

        list = new ArrayList<>();
        adaptor = new weatherRVAdaptor(list,this);
        weatherRV.setAdapter(adaptor);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_NO);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null && location.getAccuracy() >= 0) {
            String cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            getWeatherInfo("Lucknow");
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = enterCityNameET.getText().toString().trim();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter City Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    cityNameTV.setText(city);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_NO){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,10);
            for(Address address:addresses){
                if(address!=null){
                    String city = address.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }
                    else
                        Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return cityName;
    }

    void getWeatherInfo(String cityName) {

        String  url = "http://api.weatherapi.com/v1/forecast.json?key=df6db29154c94c2c939130114232802&q="+cityName+"&days=1";
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.INVISIBLE);
                homeRl.setVisibility(View.VISIBLE);
                list.clear();
                try{
                    // response = response.getJSONObject("current");
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°c");

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    weatherConditionTV.setText(condition);
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weatherIconIV);
//                    if(isDay==0) {
//                        // Morning
//                        Picasso.get().load(DayImage).into(backgroundIV);
//                    }
//                    else {
//                        // Night
//                        Picasso.get().load(NightImage).into(backgroundIV);
//                    }

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastArr = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hours = forecastArr.getJSONArray("hour");

                    for(int i=0;i<hours.length();i++){

                        JSONObject hour = hours.getJSONObject(i);
                        String time = hour.getString("time");
                        String temp = hour.getString("temp_c");
                        String img = hour.getJSONObject("condition").getString("icon");
                        String wind = hour.getString("wind_kph");
                        list.add(new weatherRVModel(time,temp,img,wind));
                    }
                    adaptor.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter A Valid City...", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }


}