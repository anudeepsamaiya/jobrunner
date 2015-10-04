package com.studiotyche.apps.android.jobrunner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by AnudeepSamaiya on 04-10-2015.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    String TAG = "FeedAdapter";

    Context context;
    List<Alert> alerts;

    public FeedAdapter(Context context, List<Alert> alerts) {
        this.alerts = alerts;
        this.context = context;
        Log.d(TAG, "Recieved alerts " + alerts.size());
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public FeedViewHolder(View itemView) {
            super(itemView);
        }
    }
}
