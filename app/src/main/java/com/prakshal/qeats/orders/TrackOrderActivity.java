package com.prakshal.qeats.orders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.prakshal.qeats.BaseDrawerActivity;
import com.prakshal.qeats.R;
import com.prakshal.qeats.adapter.OrderItemsListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;
import com.prakshal.qeats.model.Status;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackOrderActivity extends BaseDrawerActivity {

    private String orderUrl = Constants.API_ENDPOINT + Constants.GET_ORDER_API;
    private ProgressDialog pDialog;
    private Order order;
    private List<Item> items = new ArrayList<>();
    private ListView listView;
    private OrderItemsListAdapter adapter;

    private TextView placedAtTv;
    private TextView restaurantTv;
    private TextView deliveredAtTv;
    private TextView totalTv;
    private Button cancelBtn;
    private Button refreshBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_track_order, frameLayout);
        Bundle extras = getIntent().getExtras();
        String orderId = null;
        if(extras != null){
            orderId = extras.getString("order_id");
            getSupportActionBar().setTitle("Order #" + orderId);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            finish();
        }

        placedAtTv = findViewById(R.id.placed_at);
        restaurantTv = findViewById(R.id.restaurant_name);
        deliveredAtTv = findViewById(R.id.delivered_at);
        totalTv = findViewById(R.id.total);

        if(orderId != null){
            listView = findViewById(R.id.order_items);
            adapter = new OrderItemsListAdapter(this, items);
            listView.setAdapter(adapter);
            sendRequest(orderId);
        }

        refreshBtn = findViewById(R.id.refresh_status);
        cancelBtn = findViewById(R.id.cancel_button);


        final String id = orderId;

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(id);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(id);
            }
        });
    }

    private void cancelOrder(final String id) {

        String cancelOrderUrl = Constants.API_ENDPOINT + Constants.ORDER_CANCEL_API;

        final String userId = AppController.getInstance().getUserId();

        cancelOrderUrl += "?userId=" + userId;
        cancelOrderUrl += "&orderId=" + id;

        StringRequest request = new StringRequest(Request.Method.GET, cancelOrderUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("cancel_order", response);
                        Toast.makeText(getApplicationContext(), "Order cancelled." , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TrackOrderActivity.this, OrderDeliveredActivity.class);
                        intent.putExtra("order_id", id);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to cancel your order." , Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    public void sendRequest(String orderId){

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = orderUrl + "?orderId=" + orderId;

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("JSONResponse",response.toString());
                            hidePDialog();
                            Order temp = Parser.getOrderFromJson(response);

                            if(temp.getStatus() == Status.DELIVERED || temp.getStatus() == Status.CANCELLED){
                                Intent intent = new Intent(TrackOrderActivity.this, OrderDeliveredActivity.class);
                                startActivity(intent);
                            } else {
                                items.clear();
                                items.addAll(temp.getItems());
                                order = temp;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
                                placedAtTv.setText(simpleDateFormat.format(temp.getPlacedAt()));
                                restaurantTv.setText(temp.getRestaurant().getName());
                                deliveredAtTv.setText(temp.getStatus().name());
                                deliveredAtTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_24px, 0, 0, 0);
                                totalTv.setText(String.format("₹ %s", String.valueOf(order.getTotal())));
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error fetching data." , Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("inside","errorresponse");
                Toast.makeText(getApplicationContext(), "Error updating order status." , Toast.LENGTH_LONG).show();
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
