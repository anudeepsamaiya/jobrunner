package com.studiotyche.apps.android.jobrunner.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studiotyche.apps.android.jobrunner.R;
import com.studiotyche.apps.android.jobrunner.adapters.FeedAdapter;
import com.studiotyche.apps.android.jobrunner.models.Alert;
import com.studiotyche.apps.android.jobrunner.persistence.AlertFeedTable;
import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;
import com.studiotyche.apps.android.jobrunner.utils.DividerItemDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnudeepSamaiya on 29-09-2015.
 */

/**
 * TODO: show isVisited and lastVisited status of Alert link
 */
public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";
    int pos;

    List<Alert> alerts = new ArrayList<Alert>();
    RecyclerView rv;
    RecyclerView.LayoutManager llm;
    RecyclerView.ItemDecoration itemDecoration;
    RecyclerView.Adapter adapter;

    public static FeedFragment getInstance(ArrayList<Alert> alerts, @FeedAdapter.Name int pos) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putInt("Position", pos);
        args.putParcelableArrayList("Dataset", alerts);
        feedFragment.setArguments(args);
        return feedFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Fragment Attached");

        if (pos == 0)
            if (!DbHelper.getInstance(this.getActivity()).checkIfTableExists(AlertFeedTable.NAME))
                getTopAlerts();

        Bundle args = getArguments();
        alerts = args.getParcelableArrayList("Dataset");
        pos = args.getInt("Position");

        if (pos == 0)
            adapter = new FeedAdapter(this.getActivity(), alerts, FeedAdapter.RECENT);
        else
            adapter = new FeedAdapter(this.getActivity(), alerts, FeedAdapter.SAVED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        Log.d(TAG, " before for");
        for (Alert a : alerts)
            Log.d(TAG, a.getTitle() + " " + a.getDesc() + " " + a.getLink() + " " + a.getTimeStamp() + " ");

        rv = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        itemDecoration = new DividerItemDecoration(this.getActivity());

        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        return rootView;
    }


    public void getTopAlerts() {
        Log.i(TAG, "inside getTopAlerts() making volley call");
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = "http://gabja-harishvi.rhcloud.com/rest/getTop";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
            DbHelper.getInstance(this.getActivity()).addNewAlertToDB(alert);
        }
        if (pos == 0)
            ((FeedAdapter) adapter).addItem(0); //addItem at position 0
        Log.d(TAG, "Alert size " + this.alerts.size());
    }
}
