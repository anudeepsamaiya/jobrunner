package com.studiotyche.apps.android.jobrunner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnudeepSamaiya on 29-09-2015.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = "AlertFeedFragment";

    @IntDef({RECENT_FRAGMENT, SAVED_FRAGMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Name {
    }

    public static final int RECENT_FRAGMENT = 0;
    public static final int SAVED_FRAGMENT = 1;

    List<Alert> alerts;
    RecyclerView rv;
    RecyclerView.LayoutManager llm;
    RecyclerView.ItemDecoration itemDecoration;
    RecyclerView.Adapter adapter;

    public static List<FeedFragment> mInstance = new ArrayList<>();

    public static FeedFragment newInstance(@Name int instance) {
        FeedFragment fragment = new FeedFragment();
        mInstance.add(instance, fragment);
        return mInstance.get(instance);
    }

    public static FeedFragment getInstance(@Name int instance) {
        if (mInstance.isEmpty() || mInstance.size() < 2)
            return newInstance(instance);
        return mInstance.get(instance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment Attached");
        alerts = new ArrayList<Alert>();
        alerts = DbHelper.getInstance(this.getActivity()).getAllAlerts(DbHelper.RECENT, 5);
        mInstance.get(RECENT_FRAGMENT).setAdapter(new RVAdapter(this.getContext(), alerts));
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        Log.d(TAG, " before for");
        for (Alert a : alerts)
            Log.d(TAG, a.getTitle() + " " + a.getDesc() + " " + a.getLink() + " " + a.getTimeStamp() + " ");

        rv = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        itemDecoration = new DividerItemDecoration(this.getContext());

        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);

        return rootView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerView.Adapter getAdapter(@Name int instance) {
        if (instance == RECENT_FRAGMENT)
            mInstance.get(instance).setAdapter(new RVAdapter(this.getContext(), alerts));
        if (instance == SAVED_FRAGMENT)
            mInstance.get(instance).setAdapter(new SavedFeedAdapter(this.getContext(), alerts));
        return adapter;
    }

    public void addItem(@Name int instance, int pos) {
        Log.i(TAG, "From Add Item");

        if (instance == RECENT_FRAGMENT) {
            alerts = DbHelper.getInstance(this.getActivity()).getAllAlerts(DbHelper.RECENT, 5);
            ((RVAdapter) getAdapter(RECENT_FRAGMENT)).addItem(pos);
        }
        if (instance == SAVED_FRAGMENT) {
            alerts = DbHelper.getInstance(this.getActivity()).getAllAlerts(DbHelper.SAVED, 5);
            ((SavedFeedAdapter) getAdapter(SAVED_FRAGMENT)).addItem(pos);
        }
    }
}
