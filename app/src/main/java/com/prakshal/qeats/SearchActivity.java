package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private String ip="35.200.227.34";
    private String url = "http://"+ip+":8081/qeats/v1/restaurants?latitude=";//21.724216&longitude=73.01525";
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
                     //   url+=Float.toString(location.latitude)+"longitude="+Float.toString(location.longitude)+"searchFor=Hot";
                    }
                });
        Log.i("Location",Float.toString(getLatitude()));
        url += String.valueOf(getLatitude()) + "&longitude=" + String.valueOf(getLongitude())+"&searchFor=Hot";
        listView = (ListView) findViewById(R.id.searchlist);
        adapter = new CustomListAdapter(this, restaurantList);
        listView.setAdapter(adapter);

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
                        Log.i("Inside","response");
                        try {
                            JSONArray obj1 = response.getJSONArray("restaurants");
                            Log.i("JSONResponse",response.toString());
                            // Log.d(TAG, obj1.toString());
                            hidePDialog();

                            // Parsing json
                            for (int i = 0; i < obj1.length(); i++) {


                                JSONObject obj = obj1.getJSONObject(i);
                              /*  Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                movie.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                movie.setYear(obj.getInt("releaseYear"));*/
                                Restaurant restaurant = new Restaurant();
                                restaurant.setRestaurantId(obj.getString("restaurantId"));
                                restaurant.setName(obj.getString("name"));
                                restaurant.setImageUrl(obj.getString("imageUrl"));
                                restaurant.setOpensAt(obj.getString("opensAt"));
                                restaurant.setClosesAt(obj.getString("closesAt"));

                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("attributes");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                String[] attributes = new String[genre.size()];
                                attributes = genre.toArray(attributes);
                                restaurant.setAttributes(attributes);

                                // adding movie to movies array
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
