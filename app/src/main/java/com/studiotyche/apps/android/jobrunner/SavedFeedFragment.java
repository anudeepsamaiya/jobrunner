package com.studiotyche.apps.android.jobrunner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */
public class SavedFeedFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemDecoration itemDecoration;
    static RecyclerView.Adapter adapter;

    static List<Alert> alerts;

    public static SavedFeedFragment newInstance() {
        SavedFeedFragment fragment = new SavedFeedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alerts = new ArrayList<Alert>();
        adapter = new SavedFeedFragmentAdapter(this.getContext(), alerts);
        if (alerts.isEmpty()) {
            alerts = DbHelper.getInstance(this.getActivity()).getAllAlerts(DbHelper.SAVED, 50);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {

        View rootView = layoutInflater.inflate(R.layout.fragment_feed, parent, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false);
        itemDecoration = new DividerItemDecoration(this.getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public static void addItem(int pos) {
        adapter.notifyItemInserted(pos);
        adapter.notifyItemRangeChanged(pos, alerts.size());
    }
}
