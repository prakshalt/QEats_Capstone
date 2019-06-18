package com.prakshal.qeats.orders;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.prakshal.qeats.R;
import com.prakshal.qeats.adapter.CustomOrderListAdapter;
import com.prakshal.qeats.adapter.OrderItemsListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.login.LoginActivity;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDeliveredActivity extends AppCompatActivity {

    private String url = Constants.API_ENDPOINT + Constants.GET_ORDER_API;
    private ProgressDialog pDialog;
    private Order order;
    private List<Item> items = new ArrayList<>();
    private ListView listView;
    private OrderItemsListAdapter adapter;

    private TextView placedAtTv;
    private TextView restaurantTv;
    private TextView deliveredAtTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_delivered);
        Bundle extras = getIntent().getExtras();
        String orderId = null;
        if(extras != null){
            orderId = extras.getString("order_id");
        } else {
            finish();
        }

        placedAtTv = findViewById(R.id.placed_at);
        restaurantTv = findViewById(R.id.restaurant_name);
        deliveredAtTv = findViewById(R.id.delivered_at);

        if(orderId != null){
            listView = findViewById(R.id.order_items);
            adapter = new OrderItemsListAdapter(this, items);
            listView.setAdapter(adapter);
            sendRequest(orderId);
        }

    }

    public void sendRequest(String orderId){

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        url += "?orderId=" + orderId;

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("JSONResponse",response.toString());
                            hidePDialog();
                            Order temp = Parser.getOrderFromJson(response);
                            items.addAll(temp.getItems());
                            order = temp;
                            placedAtTv.setText(temp.getPlacedAt().toString());
                            restaurantTv.setText(temp.getRestaurant().getName());
                            deliveredAtTv.setText(temp.getStatus().name());
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
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
