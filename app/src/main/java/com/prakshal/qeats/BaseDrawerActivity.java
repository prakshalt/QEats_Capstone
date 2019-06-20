package com.prakshal.qeats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.prakshal.qeats.app.AppController;
import com.prakshal.qeats.login.LoginActivity;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    protected FrameLayout frameLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //to prevent current item select over and over
        if (item.isChecked()){
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        /*if(id==R.id.nav_main)
        {
            startActivity(new Intent(getApplicationContext(),RestaurantsActivity.class));
        }*/
        if (id == R.id.nav_near) {
            startActivity(new Intent(getApplicationContext(), RestaurantsActivity.class));
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        } else if(id==R.id.nav_cart){
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }else if(id==R.id.nav_my_orders){
            startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
        }else if(id == R.id.logout){
            SharedPreferences.Editor editor = AppController.getInstance().getDefaultSharedPreferences().edit();
            editor.remove("user_id");
            editor.apply();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        //Add more here to go to that activity on select
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}