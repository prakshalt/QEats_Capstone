package com.prakshal.qeats.adapter;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.prakshal.qeats.MainActivity;
import com.prakshal.qeats.R;
import com.prakshal.qeats.ShowRestaurantMenuActivity;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.prakshal.qeats.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomMenuListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> menuItems;
    View view;
    private String respStr;
    private String cartId;
    private String restId;
    private ProgressDialog pDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomMenuListAdapter(Activity activity, List<Item> menuItems,String Restid) {
        this.activity = activity;
        this.menuItems = menuItems;
        this.restId=Restid;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int location) {
        return menuItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_menu, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        final TextView title = (TextView) convertView.findViewById(R.id.itemname);
        final TextView Itemidtv = (TextView) convertView.findViewById(R.id.itemid);
        TextView timings = (TextView) convertView.findViewById(R.id.price);
        TextView genre = (TextView) convertView.findViewById(R.id.attributes);

        Item m = menuItems.get(position);

        title.setText(m.getName());

        Itemidtv.setText(m.getItemId());

        timings.setText("Price: " + String.valueOf(m.getPrice()));
        String genreStr = "";
        for (String str : m.getAttributes()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);

        String ip="35.200.227.34";
        String geturl = "http://"+ip+":8081/qeats/v1/cart?userId=Prakshal";


        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, geturl,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Inside","response");
                        Log.i("JSONResponse",response.toString());
                        try {
                            Log.i(response.getString("id"),"h");
                            cartId = response.getString("id");

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("inside","errorresponse");
                // VolleyLog.d(TAG, "Error: " + error.getClass());
                error.printStackTrace();

            }
        });

        AppController.getInstance().addToRequestQueue(obreq);


        Button addToCartBtn= (Button)convertView.findViewById(R.id.add_to_cart_btn);

        addToCartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String resp = sendAddToCartRequest(cartId,Itemidtv.getText().toString(),restId);
                   Toast.makeText(parent.getContext(),"Item added to cart",Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    public String sendAddToCartRequest(final String cartid,final String itemId,final String restId){
        String ip="35.200.227.34";


        String url = "http://"+ip+":8081/qeats/v1/cart/item";
        final String requestBody="{\"cartId\":\""+cartId+"\","+
                "\"itemId\":\""+itemId+"\",\"restaurantId\":\""+restId+"\"}";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("response",response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                })
        {
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               HashMap<String, String> headers = new HashMap<String, String>();
               headers.put("Content-Type", "application/json; charset=utf-8");
               return headers;
           }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        AppController.getInstance().addToRequestQueue(strRequest);
        return respStr;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}