package com.studiotyche.apps.android.jobrunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studiotyche.apps.android.jobrunner.persistence.AlertFeedTable;
import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static String mInformationTextString;
    private static Boolean registeredWithGoogle = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if (!DbHelper.getInstance(this).checkIfTableExists(AlertFeedTable.NAME))
            getTopAlerts();

        registerWithGoogle();
        LocalBroadcastManager.getInstance(LauncherActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));

        Intent intent = new Intent();
        Bundle data = new Bundle();
        intent.putExtra("RegisteredWithGoogle", registeredWithGoogle);
        intent.putExtra("Information", mInformationTextString);
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }


    private void registerWithGoogle() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                } else {
                    mInformationTextString = getString(R.string.token_error_message);
                    registeredWithGoogle = false;
                }
            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
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

    public void getTopAlerts() {
        Log.i(TAG, "inside getTopAlerts() making volley call");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://gabja-harishvi.rhcloud.com/rest/getTop";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "got response.");
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
        ArrayList<Alert> alerts = new Gson().fromJson(response, listType);
        for (Alert alert : alerts) {
            DbHelper.getInstance(this).addNewAlert(alert);
            FeedFragment.getInstance(DbHelper.getInstance(this).getAllAlerts(DbHelper.RECENT, 20)).adapter.notifyDataSetChanged();
        }

    }
}