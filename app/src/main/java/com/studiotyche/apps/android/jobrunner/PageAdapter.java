package com.studiotyche.apps.android.jobrunner;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AnudeepSamaiya on 29-09-2015.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>(2);
    private final List<String> mFragmentTitles = new ArrayList<>(2);

    private static Map<String, Fragment> mPageMap;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPageMap = new ArrayMap<>(2);
        } else {
            mPageMap = new HashMap<>(2);
        }
    }

    public void addFragment(Fragment fragment, String title) {
        mPageMap.put(title, fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mPageMap.get(mFragmentTitles.get(position));
    }

    @Override
    public int getCount() {
        return mPageMap.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}
