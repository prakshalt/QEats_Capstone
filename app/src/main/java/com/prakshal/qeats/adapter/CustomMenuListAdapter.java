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
import com.prakshal.qeats.model.Movie;

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
    private List<Item> movieItems;
    Context context;
    View view;
   // private String itemId;
    private String respStr;
    private String cartId;
    private String restId;
    private ProgressDialog pDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomMenuListAdapter(Activity activity, List<Item> movieItems,String Restid) {
        Log.i("Constructor","called");
        this.activity = activity;
        this.movieItems = movieItems;
        this.restId=Restid;
     //   this.context=activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
//        context = convertView.getContext();
        view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_menu, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
       // NetworkImageView thumbNail = (NetworkImageView) convertView
               // .findViewById(R.id.thumbnail);
        final TextView title = (TextView) convertView.findViewById(R.id.itemname);
        final TextView Itemidtv = (TextView) convertView.findViewById(R.id.itemid);
        TextView timings = (TextView) convertView.findViewById(R.id.price);
        TextView genre = (TextView) convertView.findViewById(R.id.attributes);
       // TextView ratings = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Item m = movieItems.get(position);

       // itemId = m.getItemId();
//        Log.i("itemId",itemId);
        // thumbnail image
       // thumbNail.setImageUrl(m.getImageUrl(), imageLoader);

        // title
        title.setText(m.getName());

        Itemidtv.setText(m.getItemId());

        // rating
        timings.setText("Price: " + String.valueOf(m.getPrice()));

        // genre
        String genreStr = "";
        for (String str : m.getAttributes()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);

        String ip= "35.200.159.249";
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


        // release year
       // ratings.setText("Ratings:"+String.valueOf(5));
        Button addToCartBtn= (Button)convertView.findViewById(R.id.add_to_cart_btn);

        addToCartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               // Toast.makeText(ShowRestaurantMenuActivity.this,"You may have not granted one of the permissions",Toast.LENGTH_LONG).show();
                Log.i("btn cliced",Itemidtv.getText().toString());
                String resp = sendAddToCartRequest(cartId,Itemidtv.getText().toString(),restId);
                //if(sendAddToCartRequest(itemId,restId).equals("0")){
                   Toast.makeText(parent.getContext(),"Item added to cart",Toast.LENGTH_LONG).show();
                //}
            }
        });

        return convertView;
    }

    public String sendAddToCartRequest(final String cartid,final String itemId,final String restId){
        String ip= "35.200.159.249";
       /* String geturl = "http://"+ip+":8081/qeats/v1/cart?userId=Prakshal";


        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, geturl,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Inside","response");
                        Log.i("JSONResponse",response.toString());
                        try {
                            Log.i(response.getString("id"),"h");

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


*/
        Log.i("in fn",cartid+itemId+","+restId);

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
                        //respStr = response.ge
                       // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                       // Log.i("error",error.getMessage());
                        //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            /*@Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cartId",cartId);
                params.put("itemId",itemId);
                params.put("restaurantId",restId);
                return params;
            }*/
           /* @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }*/
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



      /*  Map<String, String> params = new HashMap();
        params.put("CartId","1");
        params.put("itemId",itemId);
        params.put("restaurantId",restId);
        JSONObject requestBody = new JSONObject(params);
        String requestString = requestBody.toString();
        /*pDialog = new ProgressDialog(.getContext());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();*/
        /*JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, url,requestBody, new
                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Inside","response");
                        Log.i("JSONResponse",response.toString());
                        try {
                            //JSONObject obj2 = response.getJSONObject("cartResponseType");
                            respStr = response.getString("cartResponseType");
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
                hidePDialog();

            }
        });
        obreq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        // Adding request to request queue

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