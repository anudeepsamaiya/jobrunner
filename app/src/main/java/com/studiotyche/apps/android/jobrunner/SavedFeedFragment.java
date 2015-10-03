package com.studiotyche.apps.android.jobrunner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.util.List;

/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */
public class SavedFeedFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemDecoration itemDecoration;
    static SavedFeedFragmentAdapter adapter;

    List<Alert> alerts;

    public static SavedFeedFragment newInstance() {
        SavedFeedFragment fragment = new SavedFeedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {

        View rootView = layoutInflater.inflate(R.layout.fragment_feed, parent, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false);
        itemDecoration = new DividerItemDecoration(this.getContext());
        adapter = new SavedFeedFragmentAdapter(this.getContext(),
                DbHelper.getInstance(this.getActivity()).getAllAlerts(DbHelper.SAVED, 50));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
