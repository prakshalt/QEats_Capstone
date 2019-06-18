package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.prakshal.qeats.adapter.CustomCartListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String url = Constants.API_ENDPOINT + Constants.CART_API;
    private ProgressDialog pDialog;
    private List<Item> itemList = new ArrayList<>();
    private List<Integer> priceList = new ArrayList<>();
   private ListView listView;
   private String cartId;
    String respStr;
    private CustomCartListAdapter adapter;
    private static boolean alreadyRecreated = false;
    private static int total=0;

    public static int getTotal() {
        return total;
    }

    public static void setTotal(int total) {
        CartActivity.total = total;
    }

    public static void addTotal(int no) {
        total += no;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cart, frameLayout);
        listView = (ListView) findViewById(R.id.cartList);
        adapter = new CustomCartListAdapter(this, itemList);
        listView.setAdapter(adapter);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request(i.e Loading anim)
        pDialog.setMessage("Loading...");
        pDialog.show();
        //This is used to send request for getting a JSON object.
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray obj1 = response.getJSONArray("items"); //This gives array of objects
                            Log.i("JSONResponse",response.toString());
                            cartId = response.getString("id"); //use getters on response to get value of specific key
                            hidePDialog();//stop loading anim

                            // Parsing json
                            for (int i = 0; i < obj1.length(); i++) {


                                JSONObject obj = obj1.getJSONObject(i);
                                Item item = new Item();
                                item.setItemId(obj.getString("itemId"));
                                item.setName(obj.getString("name"));
                                item.setImageUrl(obj.getString("imageUrl"));
                                item.setPrice(obj.getInt("price"));
                                addTotal(item.getPrice());
                                JSONArray genreArry = obj.getJSONArray("attributes");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                item.setAttributes(genre);
                                itemList.add(item);
                                priceList.add(obj.getInt("price"));
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
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
        Log.i("PriceList",Arrays.toString(priceList.toArray()));
        TextView carttotal = (TextView) findViewById(R.id.carttotal);
        carttotal.setText("Total="+String.valueOf(getTotal()));

        //Order Button
        Button orderBtn = (Button) findViewById(R.id.orderbtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = sendOrder(cartId);
            }
        });

    }

    public String sendOrder(String cartId){
        String url = Constants.API_ENDPOINT + Constants.GET_ORDER_API;
        final String requestBody="{\"cartId\":\"" + cartId + "\"}";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("response",response);
                        respStr = response;
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
    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(2).setChecked(true);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if(!alreadyRecreated){
                    Intent intent = getIntent();
                    finish();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                  //  recreate();
                    alreadyRecreated = true;
                }
            }
        }, 500);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        setTotal(0);
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
}
