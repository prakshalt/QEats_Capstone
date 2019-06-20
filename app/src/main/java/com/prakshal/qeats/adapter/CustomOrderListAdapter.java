package com.prakshal.qeats.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.prakshal.qeats.R;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;

import java.util.List;

public class CustomOrderListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orders;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomOrderListAdapter(Activity activity, List<Order> orders) {
        this.activity = activity;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int location) {
        return orders.get(location);
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
        NetworkImageView thumbNail =  convertView.findViewById(R.id.thumbnail);

        thumbNail.setImageUrl(orders.get(position).getRestaurant().getImageUrl(), imageLoader);
        orderIdtv.setText(orders.get(position).getId());
        restNametv.setText(orders.get(position).getRestaurant().getName());
        statustv.setText(orders.get(position).getStatus().name());
        totaltv.setText(String.format("\u20B9 %s", String.valueOf(orders.get(position).getTotal())));

        if(statustv.getText().toString().equals("PLACED")){
            statustv.setTextColor(Color.parseColor("#0000FF"));
        }
        if(statustv.getText().toString().equals("CANCELLED")){
            statustv.setTextColor(Color.parseColor("#FF0000"));
        }
        if(statustv.getText().toString().equals("DELIVERED")){
            statustv.setTextColor(Color.parseColor("#00FF00"));
        }
        else {
            statustv.setTextColor(Color.parseColor("#FFA500"));
        }


        StringBuilder items = new StringBuilder();
        for(Item item: orders.get(position).getItems()){
            items.append(item.getName());
            items.append(", ");
        }

        String itemsStr = items.toString();
        itemsStr = itemsStr.substring(0, itemsStr.length()-2);
        itemstv.setText(itemsStr);

        return convertView;
    }

}