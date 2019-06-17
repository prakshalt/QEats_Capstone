package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.CustomCartListAdapter;
import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private String url = "http://35.200.159.249:8081/qeats/v1/cart?userId=Prakshal";//21.724216&longitude=73.01525";
    private ProgressDialog pDialog;
    private List<Item> itemList = new ArrayList<Item>();
    private ListView listView;
    private CustomCartListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cart, frameLayout);
        listView = (ListView) findViewById(R.id.cartList);
        adapter = new CustomCartListAdapter(this, itemList);
        listView.setAdapter(adapter);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.i("before","req");
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Inside","response");
                        try {
                            JSONArray obj1 = response.getJSONArray("items");
                            Log.i("JSONResponse",response.toString());
                            //Log.d(TAG, obj1.toString());
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
                                Item item = new Item();
                                item.setItemId(obj.getString("itemId"));
                                item.setName(obj.getString("name"));
                                item.setImageUrl(obj.getString("imageUrl"));
                                item.setPrice(obj.getInt("price"));
                                //total+=item.getPrice();
                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("attributes");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                item.setAttributes(genre);

                                // adding movie to movies array
                                itemList.add(item);

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
       // TextView carttotal = (TextView) findViewById(R.id.carttotal);
        //carttotal.setText("Total="+String.valueOf(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(2).setChecked(true);
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
