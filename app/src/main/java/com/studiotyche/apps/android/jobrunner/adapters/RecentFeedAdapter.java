package com.studiotyche.apps.android.jobrunner.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.studiotyche.apps.android.jobrunner.R;
import com.studiotyche.apps.android.jobrunner.models.Alert;
import com.studiotyche.apps.android.jobrunner.persistence.DatabaseHelper;

import java.util.List;

public class RecentFeedAdapter extends RecyclerView.Adapter<RecentFeedAdapter.AlertViewHolder> {
    String TAG = "FeedAdapter";

    Context context;
    List<Alert> alerts;

    public RecentFeedAdapter(Context context, List<Alert> alerts) {
        this.alerts = alerts;
        this.context = context;
        Log.d(TAG, "Received alerts " + alerts.size());
    }

    @Override
    public AlertViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new AlertViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(AlertViewHolder alertViewHolder, final int i) {
        alertViewHolder.setIsRecyclable(false);
        alertViewHolder.tvDescription.setText(alerts.get(i).getDesc());
        alertViewHolder.tvTitle.setText(alerts.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Button btnLink, btnSave;

        AlertViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            btnLink = (Button) itemView.findViewById(R.id.btnLink);
            btnSave = (Button) itemView.findViewById(R.id.btnSave);

            btnLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnLinkClicked(v, getAdapterPosition());
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSaveClicked(view, getAdapterPosition());
                }
            });
        }
    }

    private void onBtnLinkClicked(View v, int pos) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alerts.get(pos).getLink()));
            context.startActivity(browserIntent);
        } catch (Exception e) {
            Snackbar.make(v, "Broken website link.", Snackbar.LENGTH_LONG)
                    .setAction("Report", null).show();
            //Toast.makeText(context,"Broken website link.",Toast.LENGTH_LONG).show();
        }
    }

    void onSaveClicked(View view, int pos) {
        DatabaseHelper.getInstance(context).saveAlert(alerts.get(pos));
        removeItem(pos);
        addItem(0);

        Snackbar.make(view, "Item Saved", Snackbar.LENGTH_LONG)
                .setAction("UNDO", null).show();

        view.setEnabled(false);
    }

    public void addItem(int pos) {
        alerts.clear();
        alerts.addAll(pos, DatabaseHelper.getInstance(context).getAllAlerts(DatabaseHelper.RECENT, 20));
       /* else if (tabName == SAVED)
            alerts.addAll(pos, DatabaseHelper.getInstance(context).getAllAlerts(DatabaseHelper.SAVED, 20));*/
        this.notifyItemInserted(pos);
        this.notifyItemRangeInserted(pos, alerts.size());
        this.notifyItemRangeChanged(pos, alerts.size());
        this.notifyDataSetChanged();
        Log.i(TAG, "From FEEDAdapterv addItem to " + " Size " + alerts.size());
    }

    public void removeItem(int position) {
        alerts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alerts.size());
        this.notifyDataSetChanged();
    }
}
