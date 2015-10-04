package com.studiotyche.apps.android.jobrunner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnudeepSamaiya on 29-09-2015.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    List<Alert> alerts = new ArrayList<Alert>();
    RecyclerView rv;
    RecyclerView.LayoutManager llm;
    RecyclerView.ItemDecoration itemDecoration;
    RecyclerView.Adapter adapter;

    public static FeedFragment getInstance(ArrayList<Alert> alerts) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("mData", alerts);
        feedFragment.setArguments(args);
        return feedFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Fragment Attached");

        Bundle args = getArguments();
        alerts = args.getParcelableArrayList("mData");
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
        adapter = new RVAdapter(this.getActivity(), alerts);

        rv.setLayoutManager(llm);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(adapter);

        return rootView;
    }
}
