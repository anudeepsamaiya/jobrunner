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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static String mInformationTextString;
    private static Boolean registeredWithGoogle = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        registerWithGoogle();
        LocalBroadcastManager.getInstance(LauncherActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));
        try {
            wait(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
