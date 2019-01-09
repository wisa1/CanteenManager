package com.example.canteenchecker.canteenmanager.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.canteenchecker.canteenmanager.ui.OverviewFragment;
import com.example.canteenchecker.canteenmanager.ui.RatingsFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new RatingsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
