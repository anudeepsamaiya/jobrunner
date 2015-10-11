package com.studiotyche.apps.android.jobrunner.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studiotyche.apps.android.jobrunner.AppPreferences;
import com.studiotyche.apps.android.jobrunner.R;
import com.studiotyche.apps.android.jobrunner.adapters.RecentFeedAdapter;
import com.studiotyche.apps.android.jobrunner.models.Alert;
import com.studiotyche.apps.android.jobrunner.persistence.AlertFeedTable;
import com.studiotyche.apps.android.jobrunner.persistence.DatabaseHelper;
import com.studiotyche.apps.android.jobrunner.services.RegistrationIntentService;
import com.studiotyche.apps.android.jobrunner.utils.DividerItemDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static AdView mAdView;

    TextView mInformationTextView;
    ProgressBar progressBar;

    RecyclerView rv;
    RecyclerView.LayoutManager llm;
    RecyclerView.ItemDecoration itemDecoration;
    ItemTouchHelper itemTouchHelper;
    RecyclerView.Adapter adapter;

    List<Alert> alerts = new ArrayList<Alert>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerWithGoogle();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));

        if (!DatabaseHelper.getInstance(this).checkIfTableExists(AlertFeedTable.NAME))
            getTopAlerts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(this.getTitle());
        }

        setupRecyclerView();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupRecyclerView() {
        alerts = DatabaseHelper.getInstance(this).getAllAlerts(DatabaseHelper.RECENT, 20);
        adapter = new RecentFeedAdapter(this, alerts);

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
                        DatabaseHelper.getInstance(MainActivity.this)
                                .removeAlert(alerts.get(viewHolder.getAdapterPosition()));
                        ((RecentFeedAdapter) adapter).removeItem(viewHolder.getAdapterPosition());
                    }
                });

        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));
        super.onResume();

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void getTopAlerts() {
        progressBar = (ProgressBar) findViewById(R.id.pbFetchingTopFeed);
        progressBar.setVisibility(View.VISIBLE);

        Log.i(TAG, "inside getTopAlerts() making volley call");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://gabja-harishvi.rhcloud.com/rest/getTop";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "got response. " + response);
                        saveToDb(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void saveToDb(String response) {
        Type listType = new TypeToken<ArrayList<Alert>>() {
        }.getType();
        List<Alert> alerts = new Gson().fromJson(response, listType);
        for (Alert alert : alerts) {
            DatabaseHelper.getInstance(this).addNewAlertToDB(alert);
        }
        ((RecentFeedAdapter) adapter).addItem(0); //addItem at position 0
        Log.d(TAG, "Alert size " + this.alerts.size());
    }

    private void registerWithGoogle() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, false);
                Log.i("joberio", "checking registration status: " + sentToken);
                if (sentToken) {
                } else {
                    rv.setVisibility(View.GONE);
                    mInformationTextView.setVisibility(View.VISIBLE);
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

        if (id == R.id.action_refresh) {
            getTopAlerts();
        }

        if (id == R.id.action_saved) {
            Intent intent = new Intent();
            intent.setClass(this, SavedFeedActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
