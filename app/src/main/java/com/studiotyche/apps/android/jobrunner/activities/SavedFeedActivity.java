package com.studiotyche.apps.android.jobrunner.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.studiotyche.apps.android.jobrunner.R;
import com.studiotyche.apps.android.jobrunner.adapters.SavedFeedAdapter;
import com.studiotyche.apps.android.jobrunner.models.Alert;
import com.studiotyche.apps.android.jobrunner.persistence.DatabaseHelper;
import com.studiotyche.apps.android.jobrunner.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class SavedFeedActivity extends AppCompatActivity {

    public static AdView mAdView;

    RecyclerView rv;
    RecyclerView.LayoutManager llm;
    RecyclerView.ItemDecoration itemDecoration;
    ItemTouchHelper itemTouchHelper;
    RecyclerView.Adapter adapter;

    List<Alert> alerts = new ArrayList<Alert>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(this.getTitle());
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupRecyclerView() {
        alerts = DatabaseHelper.getInstance(this).getAllAlerts(DatabaseHelper.SAVED, 20);
        adapter = new SavedFeedAdapter(this, alerts);

        rv = (RecyclerView) findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemDecoration = new DividerItemDecoration(this);

        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;// true if moved, false otherwise
                    }

                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        DatabaseHelper.getInstance(SavedFeedActivity.this)
                                .removeAlert(alerts.get(viewHolder.getAdapterPosition()));
                        ((SavedFeedAdapter) adapter).removeItem(viewHolder.getAdapterPosition());
                    }
                });

        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}