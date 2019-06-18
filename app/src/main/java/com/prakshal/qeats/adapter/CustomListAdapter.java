package com.prakshal.qeats.adapter;

import com.prakshal.qeats.R;
import com.prakshal.qeats.app.AppController;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.prakshal.qeats.model.Restaurant;

public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Restaurant> restaurants;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Restaurant> movieItems) {
        this.activity = activity;
        this.restaurants = movieItems;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int location) {
        return restaurants.get(location);
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView timings = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView ratings = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Restaurant m = restaurants.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getImageUrl(), imageLoader);

        // title
        title.setText(m.getName());

        // rating
        timings.setText("Timings: " + String.valueOf(m.getOpensAt()+"-"+m.getClosesAt()));

        // genre
        String genreStr = "";
        for (String str : m.getAttributes()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);

        // release year
        ratings.setText("Ratings:"+String.valueOf(5));

        return convertView;
    }

}