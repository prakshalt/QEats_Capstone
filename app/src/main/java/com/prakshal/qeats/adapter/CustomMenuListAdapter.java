package com.prakshal.qeats.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.prakshal.qeats.R;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Cart;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.utils.Constants;
import com.prakshal.qeats.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMenuListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> items;
    View view;
    private String respStr;
    private String cartId;
    private String restId;
    private ProgressDialog pDialog;
    private Cart cart;

    public CustomMenuListAdapter(Activity activity, List<Item> items, String restId, Cart cart) {
        this.activity = activity;
        this.restId = restId;
        this.items = items;
        this.cart = cart;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_menu, null);

        TextView itemNameTv = (TextView) convertView.findViewById(R.id.item_name);

        TextView priceTv = convertView.findViewById(R.id.price);

        Item item = items.get(position);

        itemNameTv.setText(item.getName());

        priceTv.setText(String.format("\u20B9 %s", String.valueOf(item.getPrice())));

        Button addToCartBtn = convertView.findViewById(R.id.add_to_cart_btn);

        Button removeFromCartBtn = convertView.findViewById(R.id.remove_from_cart_btn);

        final String userId = AppController.getInstance().getUserId();

        removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart != null){
                    removeFromCart(cart.getId(), items.get(position).getId(), restId, parent);
                } else{
                    fetchCart(userId);
                    Toast.makeText(parent.getContext(),"Item not removed to cart",Toast.LENGTH_LONG).show();
                }
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cart != null){
                    addToCart(cart.getId(), items.get(position).getId(), restId, parent);
                } else{
                    fetchCart(userId);
                    Toast.makeText(parent.getContext(),"Item not added to cart",Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }


    public void addToCart(final String cartId, final String itemId, final String restId, final ViewGroup parent){

        String url = Constants.API_ENDPOINT + Constants.CART_ITEM_API;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cartId", cartId);
            jsonObject.put("itemId", itemId);
            jsonObject.put("restaurantId", restId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("response",response.toString());
                    Toast.makeText(parent.getContext(),"Item added to cart.",Toast.LENGTH_LONG).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(parent.getContext(),"Item not added to cart.",Toast.LENGTH_LONG).show();
                }
            });

        AppController.getInstance().addToRequestQueue(strRequest);
    }

    public void removeFromCart(final String cartId, final String itemId, final String restId, final ViewGroup parent){

        String url = Constants.API_ENDPOINT + Constants.CART_ITEM_API;


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cartId", cartId);
            jsonObject.put("itemId", itemId);
            jsonObject.put("restaurantId", restId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.DELETE, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response",response.toString());
                        Toast.makeText(parent.getContext(),"Item removed from cart.",Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(parent.getContext(),"Item not removed to cart.",Toast.LENGTH_LONG).show();
                    }
                });

        AppController.getInstance().addToRequestQueue(strRequest);
    }

    public void fetchCart(final String userId){


        String url = Constants.API_ENDPOINT + Constants.CART_API;

        url += "?userId=" + userId;

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url, null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("CART_API_RESPONSE", response.toString());
                            Cart temp = Parser.getCartFromJson(response);
                            //if(temp.getItems().size() == 0){
                               // Toast.makeText(getApplicationContext(), "Empty cart, add some items." , Toast.LENGTH_SHORT).show();
                           // }
                            cart = temp;
                        }catch (JSONException e) {
                            e.printStackTrace();
                           // Toast.makeText(getApplicationContext(), "Error fetching data." , Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error fetching data." , Toast.LENGTH_SHORT).show();
            }
        });
        obreq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(obreq);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}