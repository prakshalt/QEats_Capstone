package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.adapter.OrderItemsListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.login.LoginActivity;
import com.prakshal.qeats.model.Cart;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;
import com.prakshal.qeats.orders.TrackOrderActivity;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private ProgressDialog pDialog;
    private Cart cart;
    private List<Item> items = new ArrayList<>();
    private ListView listView;
    private OrderItemsListAdapter adapter;

    private TextView restaurantTv;
    private TextView totalTv;
    private Button placeOrderBtn;
    private Button editCartBtn;


    private String cartId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cart, frameLayout);

        restaurantTv = findViewById(R.id.restaurant_name);
        totalTv = findViewById(R.id.total);


        String userId = AppController.getInstance().getUserId();
        if(userId == null){
            startActivity(new Intent(this, LoginActivity.class));
        } else{
            listView = findViewById(R.id.order_items);
            adapter = new OrderItemsListAdapter(this, items);
            listView.setAdapter(adapter);
            fetchCart(userId);
        }


        placeOrderBtn = findViewById(R.id.place_order_button);
        editCartBtn = findViewById(R.id.edit_cart_button);

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartId != null && cart.getItems().size() > 0){
                    new AlertDialog.Builder(CartActivity.this)
                    .setTitle("Confirm")
                    .setMessage("Do you want to place the order ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            placeOrder(cartId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
                } else if(cart.getItems().size() == 0){
                    Toast.makeText(getApplicationContext(), "Empty cart, add some items." , Toast.LENGTH_SHORT).show();
                } else {
                    recreate();
                }
            }
        });


        editCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart != null && cart.getItems().size() > 0){
                    Intent intent = new Intent(getBaseContext(), ShowRestaurantMenuActivity.class);
                    intent.putExtra("RESTAURANT_ID", cart.getRestaurantId());
                    intent.putExtra("RESTAURANT_NAME", cart.getRestaurant().getName());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Empty cart, add some items." , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void placeOrder(final String cartId){

        Log.i("CART_ID", cartId);

        String url = Constants.API_ENDPOINT + Constants.POST_ORDER_API;

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Placing your order...");
        pDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cartId", cartId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("JSONResponse",response.toString());
                            Order temp = Parser.getOrderFromJson(response);
                            Intent intent = new Intent(CartActivity.this, TrackOrderActivity.class);
                            intent.putExtra("order_id", temp.getId());
                            startActivity(intent);
                            hidePDialog();
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error placing order." , Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        hidePDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error placing order." , Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                hidePDialog();

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    public void fetchCart(final String userId){


        String url = Constants.API_ENDPOINT + Constants.CART_API;

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Placing your order...");
        pDialog.show();

        url += "?userId=" + userId;

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url, null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("CART_API_RESPONSE", response.toString());
                            Cart temp = Parser.getCartFromJson(response);
                            if(temp.getItems().size() == 0){
                                Toast.makeText(getApplicationContext(), "Empty cart, add some items." , Toast.LENGTH_SHORT).show();
                            }
                            cart = temp;
                            cartId = temp.getId();
                            items.addAll(temp.getItems());
                            if(cart.getRestaurant() != null && cart.getItems().size() > 0)
                                if(cart.getRestaurant().getName().equals("null")){
                                    restaurantTv.setText("");
                                }else{
                                    restaurantTv.setText(temp.getRestaurant().getName());
                                }
                            else{
                                restaurantTv.setText("");
                            }
                            totalTv.setText(String.format("₹ %s", String.valueOf(cart.getTotal())));
                            hidePDialog();
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error fetching data." , Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error fetching data." , Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
