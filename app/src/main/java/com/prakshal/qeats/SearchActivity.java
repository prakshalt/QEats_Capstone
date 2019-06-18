package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Restaurant;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private String url = Constants.API_ENDPOINT + Constants.RESTAURANTS_API;
    private ProgressDialog pDialog;
    private List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    private ListView listView;
    private CustomListAdapter adapter;
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
        getLayoutInflater().inflate(R.layout.activity_search, frameLayout);
        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        Log.i("Latitude",Float.toString(location.latitude));

                        setLatitude(location.latitude);
                        setLongitude(location.longitude);
                    }
                });
        Log.i("Location",Float.toString(getLatitude()));
        url += String.valueOf(getLatitude()) + "&longitude=" + String.valueOf(getLongitude())+"&searchFor=Hot";
        listView = (ListView) findViewById(R.id.searchlist);
        adapter = new CustomListAdapter(this, restaurantList);
        listView.setAdapter(adapter);
        sendRequest();

    }

    public void sendRequest(){

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray restaurantsJsonArray = response.getJSONArray("restaurants");
                            Log.i("JSONResponse",response.toString());
                            hidePDialog();

                            for (int i = 0; i < restaurantsJsonArray.length(); i++) {
                                JSONObject obj = restaurantsJsonArray.getJSONObject(i);
                                Restaurant restaurant = Parser.getRestaurantFromJson(obj);
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
                //  VolleyLog.d(TAG, "Error: " + error.getClass());
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
        navigationView.getMenu().getItem(1).setChecked(true);
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
