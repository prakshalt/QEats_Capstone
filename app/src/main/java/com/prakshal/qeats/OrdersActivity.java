package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private String ip="35.200.227.34";
    private String url = "http://"+ip+":8081/qeats/v1/orders?userId=Prakshal";//21.724216&longitude=73.01525";
    private ProgressDialog pDialog;
    private List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_orders, frameLayout);
    }
}
