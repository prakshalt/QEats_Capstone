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

public class CustomCartListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> itemList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomCartListAdapter(Activity activity, List<Item> items) {
        this.activity = activity;
        this.itemList = items;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int location) {
        return itemList.get(location);
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
            convertView = inflater.inflate(R.layout.list_row_cart, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        TextView title = (TextView) convertView.findViewById(R.id.cartitemname);
        TextView pricetv = (TextView) convertView.findViewById(R.id.pricecartitem);

        TextView totaltv = (TextView) convertView.findViewById(R.id.carttotal);
        Item m = itemList.get(position);

        title.setText(m.getName());

        pricetv.setText(String.valueOf(m.getPrice()));

        return convertView;
    }

}