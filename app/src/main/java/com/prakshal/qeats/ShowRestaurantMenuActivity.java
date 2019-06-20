package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.CustomMenuListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.login.LoginActivity;
import com.prakshal.qeats.model.Cart;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Menu;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowRestaurantMenuActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ProgressDialog pDialog;
    private List<Item> items = new ArrayList<>();
    private ListView listView;
    private CustomMenuListAdapter adapter;
    private String restId;
    private String restName;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_show_restaurant_menu, frameLayout);

        restId = getIntent().getStringExtra("RESTAURANT_ID");
        restName = getIntent().getStringExtra("RESTAURANT_NAME");

        getSupportActionBar().setTitle(restName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userId = AppController.getInstance().getUserId();
        if(userId == null){
            startActivity(new Intent(this, LoginActivity.class));
        } else{
            listView = findViewById(R.id.menu_items);
            adapter = new CustomMenuListAdapter(ShowRestaurantMenuActivity.this, items, restId, cart);
            listView.setAdapter(adapter);
            fetchData(restId, userId);
        }
    }

    public void fetchData(final String restId, final String userId){

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        fetchRestaurantMenu(restId);
        //fetchCart(userId);
        hidePDialog();
    }

    public void fetchRestaurantMenu(final String restId){

        String url = Constants.API_ENDPOINT + Constants.MENU_API;

        url += "?restaurantId=" + restId;

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Menu Json", response.toString());
                            Menu menu = Parser.getMenuFromJson(response.getJSONObject("menu"));
                            items.addAll(menu.getItems());
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
                error.printStackTrace();

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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cart_icon) {
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
