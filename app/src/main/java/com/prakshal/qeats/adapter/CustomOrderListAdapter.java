package com.prakshal.qeats.adapter;

import com.prakshal.qeats.R;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.prakshal.qeats.model.Restaurant;
import com.prakshal.qeats.model.Status;

public class CustomOrderListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private String orderId;
    private int total;
    private List<Item> orderItems;
    private Restaurant restaurant;
    private Status status;

    public CustomOrderListAdapter(Activity activity, List<Item> orderItems, String orderId, int total, Status status,Restaurant restaurant) {
        this.activity = activity;
        this.orderItems = orderItems;
        this.orderId = orderId;
        this.total = total;
        this.status=status;
        this.restaurant=restaurant;
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int location) {
        return orderItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_orders, null);

        TextView orderIdtv = (TextView) convertView.findViewById(R.id.OrderId);
        TextView restNametv = (TextView) convertView.findViewById(R.id.restName);
        TextView statustv = (TextView) convertView.findViewById(R.id.status);
        TextView totaltv = (TextView) convertView.findViewById(R.id.Total);
        TextView itemstv = (TextView) convertView.findViewById(R.id.items);

        orderIdtv.setText(orderId);
        restNametv.setText(restaurant.getName());
        statustv.setText(status.toString());
        totaltv.setText(Integer.toString(total));
        StringBuilder items = new StringBuilder();
        for(Item item:orderItems){
            items.append(item.getName());
            items.append(",");
        }
        String itemsStr = items.toString();
        itemsStr = itemsStr.substring(0,itemsStr.length()-1);
        itemstv.setText(itemsStr);


        return convertView;
    }

}