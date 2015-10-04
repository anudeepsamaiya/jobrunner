package com.studiotyche.apps.android.jobrunner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static TextView mInformationTextView;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        if (intent.getBooleanExtra("RegisteredWithGoogle", false)) {
            mInformationTextView.setVisibility(View.VISIBLE);
            mInformationTextView.setText(intent.getStringExtra("Information"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(this.getTitle());
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupViewPager(ViewPager viewPager) {
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager());

        ArrayList<Alert> recentAlerts = DbHelper.getInstance(this).getAllAlerts(DbHelper.RECENT, 20);
        ArrayList<Alert> savedAlerts = DbHelper.getInstance(this).getAllAlerts(DbHelper.SAVED, 20);

        adapter.addFragment(FeedFragment.getInstance(recentAlerts, FeedAdapter.RECENT), "Recent");
        adapter.addFragment(FeedFragment.getInstance(savedAlerts, FeedAdapter.SAVED), "Saved");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setupViewPager(viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
