package com.prakshal.qeats;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.app.AppController;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.prakshal.qeats.model.Restaurant;

public class MainActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback{ //implements LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS =0;
    String perms[]={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};


    private String ip="35.200.227.34";
    private String url = "http://"+ip+":8081/qeats/v1/restaurants?latitude=";//21.724216&longitude=73.01525";
    private ProgressDialog pDialog;
    private List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    private ListView listView;
    private CustomListAdapter adapter;
    int cLocationPerm,fLocationPerm;
    public static float  latitude,longitude;




    public static void setLatitude(float lat){
        latitude = lat;
    }

    public static void setLongitude(float longit) {
        longitude = longit;
    }

    public static float getLatitude() {
        return latitude;
    }

    public static float getLongitude() {
        return longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        //setContentView(R.layout.activity_main);

        cLocationPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        fLocationPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cLocationPerm != PackageManager.PERMISSION_GRANTED || fLocationPerm != PackageManager.PERMISSION_GRANTED ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Permissions required")
                        .setMessage("Do you want to grant the permissions required(App cannot work without them,please refer to help)")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, perms,
                                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory( Intent.CATEGORY_HOME );
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                Toast.makeText(MainActivity.this,"You may have not granted one of the permissions",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(off==0){
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
            }
        } catch (Exception e){

        }

        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        Log.i("Latitude",Float.toString(location.latitude));
                        setLatitude(location.latitude);
                        setLongitude(location.longitude);
                    }
                });
        url += String.valueOf(getLatitude()) + "&longitude=" + String.valueOf(getLongitude());
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, restaurantList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                Restaurant  restaurant    = (Restaurant) listView.getItemAtPosition(position);

                String restId = restaurant.getRestaurantId();

                Intent intent = new Intent(getBaseContext(), ShowRestaurantMenuActivity.class);
                intent.putExtra("RESTAURANT_ID", restId);
                intent.putExtra("RESTAURANT_NAME",restaurant.getName());
                startActivity(intent);
            }

        });

        // Creating volley request obj
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray obj1 = response.getJSONArray("restaurants");
                            hidePDialog();

                            // Parsing json
                            for (int i = 0; i < obj1.length(); i++) {


                                JSONObject obj = obj1.getJSONObject(i);
                                Restaurant restaurant = new Restaurant();
                                restaurant.setRestaurantId(obj.getString("restaurantId"));
                                restaurant.setName(obj.getString("name"));
                                restaurant.setImageUrl(obj.getString("imageUrl"));
                                restaurant.setOpensAt(obj.getString("opensAt"));
                                restaurant.setClosesAt(obj.getString("closesAt"));

                                JSONArray genreArry = obj.getJSONArray("attributes");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                String[] attributes = new String[genre.size()];
                                attributes = genre.toArray(attributes);
                                restaurant.setAttributes(attributes);

                                restaurantList.add(restaurant);

                            }
                        }catch (JSONException e) {
                                e.printStackTrace();
                            }


                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("inside","errorresponse");
                VolleyLog.d(TAG, "Error: " + error.getClass());
                error.printStackTrace();
                hidePDialog();

            }
        });
        obreq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(obreq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

}
