package com.prakshal.qeats.orders;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.prakshal.qeats.BaseDrawerActivity;
import com.prakshal.qeats.OrdersActivity;
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

public class OrderDeliveredActivity extends BaseDrawerActivity {

    private String url = Constants.API_ENDPOINT + Constants.GET_ORDER_API;
    private ProgressDialog pDialog;
    private Order order;
    private List<Item> items = new ArrayList<>();
    private ListView listView;
    private OrderItemsListAdapter adapter;

    private TextView placedAtTv;
    private TextView restaurantTv;
    private TextView deliveredAtTv;
    private TextView totalTv;
    private Button reorderBtn;
    private Button rateBtn;
    private LinearLayout ratingLayout;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_order_delivered, frameLayout);
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
        ratingLayout = findViewById(R.id.rating_layout);
        ratingBar = findViewById(R.id.rating_given);

        if(orderId != null){
            listView = findViewById(R.id.order_items);
            adapter = new OrderItemsListAdapter(this, items);
            listView.setAdapter(adapter);
            sendRequest(orderId);
        }

        reorderBtn = findViewById(R.id.reorder_button);
        rateBtn = findViewById(R.id.rate_button);


        final String id = orderId;

        reorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reOrder(id);
            }
        });



        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDeliveredActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                LayoutInflater inflater = getLayoutInflater();
                builder.setTitle("Rate your order:");
                View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_ratingbar, null);
                final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
                builder.setView(dialogLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateRating(ratingBar.getRating(), id);
                        recreate();
                        //Toast.makeText(getApplicationContext(), "Rating is " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    private void reOrder(final String orderId) {

        String reOrderUrl = Constants.API_ENDPOINT + Constants.REORDER_API;

        final String userId = AppController.getInstance().getUserId();

        reOrderUrl += "?userId=" + userId;
        reOrderUrl += "&orderId=" + orderId;

        StringRequest request = new StringRequest(Request.Method.GET, reOrderUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response_rate", response);
                        Toast.makeText(getApplicationContext(), "Items added to cart." , Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestQueue(request);

    }

    public void updateRating(final float rating, final String orderId){

        String ratingUrl = Constants.API_ENDPOINT + Constants.GET_RATE_API;

        final String userId = AppController.getInstance().getUserId();

        ratingUrl += "?userId=" + userId;
        ratingUrl += "&orderId=" + orderId;
        ratingUrl += "&rating=" + String.valueOf((int)rating);

        StringRequest request = new StringRequest(Request.Method.GET, ratingUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response_rate", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
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
                            Order temp = Parser.getOrderFromJson(response);

                            if(temp.getStatus() == Status.DELIVERED || temp.getStatus() == Status.CANCELLED){
                                items.addAll(temp.getItems());
                                order = temp;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
                                placedAtTv.setText(simpleDateFormat.format(temp.getPlacedAt()));
                                restaurantTv.setText(temp.getRestaurant().getName());
                                deliveredAtTv.setText(temp.getStatus().name());
                                if(temp.getStatus() == Status.CANCELLED){
                                    deliveredAtTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);
                                }else{
                                    deliveredAtTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
                                }
                                if(order.isRated() && order.getRating() > 0){
                                    ratingBar.setRating(order.getRating());
                                    ViewGroup layout = (ViewGroup) rateBtn.getParent();
                                    if(layout != null) //for safety only  as you are doing onClick
                                        layout.removeView(rateBtn);
                                }else{
                                    ratingLayout.setVisibility(View.GONE);
                                }
                                totalTv.setText(String.format("₹ %s", String.valueOf(order.getTotal())));
                            } else {
                                Intent intent = new Intent(OrderDeliveredActivity.this, TrackOrderActivity.class);
                                intent.putExtra("order_id", temp.getId());
                                startActivity(intent);
                            }

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
