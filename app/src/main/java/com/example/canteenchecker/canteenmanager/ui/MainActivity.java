package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.MainFragmentPagerAdapter;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements RatingsFragment.OnFragmentInteractionListener,
                   OverviewFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private MainFragmentPagerAdapter adapter;


    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.mniEditCanteenData).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.mniEditCanteenData:
                editCanteenData();
                return true;
            case R.id.logout:
                CanteenManagerApplication.getInstance().setAuthenticationToken("");
                startActivity(LoginActivity.createIntent(getApplicationContext()));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void editCanteenData() {
        Context context = getApplication();
        Intent intent = EditCanteenDataActivity.createIntent(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
