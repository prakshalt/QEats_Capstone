package com.prakshal.qeats.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prakshal.qeats.R;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;
import com.prakshal.qeats.orders.OrderDeliveredActivity;

import java.util.List;

public class OrderItemsListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;

    private List<Item> items;

    public OrderItemsListAdapter(Activity activity, List<Item> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
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
            convertView = inflater.inflate(R.layout.list_row_order_items, null);

        TextView itemTextView = convertView.findViewById(R.id.item);
        TextView priceTextView = convertView.findViewById(R.id.price);

        String value = items.get(position).getName() + "x" + items.get(position).getItemCount();
        int price  = items.get(position).getPrice() * items.get(position).getItemCount();

        itemTextView.setText(value);
        priceTextView.setText(String.valueOf(price));



        return convertView;
    }
}
