package com.prakshal.qeats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.prakshal.qeats.adapter.CustomListAdapter;
import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Restaurant;

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
    private String ip="35.200.227.34";
    private String url = "http://"+ip+":8081/qeats/v1/cart?userId=Prakshal";//21.724216&longitude=73.01525";
    private ProgressDialog pDialog;
    private List<Item> itemList = new ArrayList<Item>();
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
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.i("before","req");
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,null, new

                Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Inside","response");
                        try {
                            JSONArray obj1 = response.getJSONArray("items");
                            Log.i("JSONResponse",response.toString());
                            cartId = response.getString("id");
                            //Log.d(TAG, obj1.toString());
                            hidePDialog();

                            // Parsing json
                            for (int i = 0; i < obj1.length(); i++) {


                                JSONObject obj = obj1.getJSONObject(i);
                              /*  Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                movie.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                movie.setYear(obj.getInt("releaseYear"));*/
                                Item item = new Item();
                                item.setItemId(obj.getString("itemId"));
                                item.setName(obj.getString("name"));
                                item.setImageUrl(obj.getString("imageUrl"));
                                item.setPrice(obj.getInt("price"));
                                addTotal(item.getPrice());
                                //total+=item.getPrice();
                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("attributes");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                item.setAttributes(genre);

                                // adding movie to movies array
                                itemList.add(item);
                                priceList.add(obj.getInt("price"));
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
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

        Button orderBtn = (Button) findViewById(R.id.orderbtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = sendOrder(cartId);
            }
        });
       // recreate();

    }

    public String sendOrder(String cartId){
        String url = "http://35.200.159.249:8081/qeats/v1/order";
        final String requestBody="{\"cartId\":\""+cartId+"\"}";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("response",response);
                        respStr = response;
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
   //     recreate();
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
