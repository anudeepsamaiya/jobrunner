package com.studiotyche.apps.android.jobrunner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.util.List;

/**
 * Created by AnudeepSamaiya on 29-09-2015.
 */
public class AlertFeedFragment extends Fragment {

    private static final String TAG = "AlertFeedFragment";
    List<Alert> alerts;
    public static RecyclerView.Adapter adapter;
    public static RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        alerts = DbHelper.getInstance(this.getActivity()).getAllAlerts();
        Log.d(TAG, " before for");
        for (Alert a : alerts) {
            Log.d(TAG, a.getTitle() + " " + a.getDesc() + " " + a.getLink() + " " + a.getTimeStamp() + " ");
        }

        rv = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext());
        adapter = new RVAdapter(this.getContext(), alerts);


        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);

        return rootView;
    }
}
