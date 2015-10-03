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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static List<Alert> alerts;
    public static TextView mInformationTextView;
/*

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // {"desc":"Catching Joker and Saving Gotham","link":"DCComics.com","timestamp":"Fri Oct 02 21:41:19 GMT+05:30 2015","title":"Batman"}

        //alerts = new ArrayList<Alert>();
        //alerts.add(new Alert("A", "B", "C", "D"));
/*
        DbHelper.getInstance(this).addNewAlert(new Alert("A", "B", "C", "D"));
        DbHelper.getInstance(this).addNewAlert(new Alert("E", "H", "K", "M"));
        DbHelper.getInstance(this).addNewAlert(new Alert("F", "I", "L", "O"));
        DbHelper.getInstance(this).addNewAlert(new Alert("G", "J", "M", "P"));
*/
/*

        registerWithGoogle();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));

*/
        Intent intent = new Intent();
        if (intent.getBooleanExtra("RegisteredWithGoogle", false)) {
            mInformationTextView.setVisibility(View.VISIBLE);
            mInformationTextView.setText(intent.getStringExtra("Information"));
        }
        alerts = DbHelper.getInstance(this).getAllAlerts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(this.getTitle());
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlertFeedFragment(), "Recent");
        adapter.addFragment(SavedFeedFragment.newInstance(), "Saved");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
/*
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
*/
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
