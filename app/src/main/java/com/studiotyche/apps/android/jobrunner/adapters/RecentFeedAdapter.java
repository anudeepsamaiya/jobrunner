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
        Button btnLink, btnSave, btnShare;

        AlertViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            btnLink = (Button) itemView.findViewById(R.id.btnLink);
            btnSave = (Button) itemView.findViewById(R.id.btnSave);
            btnShare = (Button) itemView.findViewById(R.id.btnShare);

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

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShareClicked(getAdapterPosition());
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

    private void onShareClicked(int adapterPosition) {
        Alert toShareAlert = alerts.get(adapterPosition);

        String shareText = toShareAlert.getTitle() + " \n" +
                toShareAlert.getDesc() + " \n" +
                toShareAlert.getLink() + " \n" +
                "Get more such updates on JobRunner\n" +
                "https://goo.gl/b3Nko0";

        doShare(shareText);
    }

    private void doShare(String shareText) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public void addItem(int pos) {
        alerts.clear();
        alerts.addAll(pos, DatabaseHelper.getInstance(context).getAllAlerts(DatabaseHelper.RECENT, 20));
        this.notifyItemInserted(pos);
        this.notifyItemRangeInserted(pos, alerts.size());
        this.notifyItemRangeChanged(pos, alerts.size());
        this.notifyDataSetChanged();
        Log.i(TAG, "From Adapter addItem to " + " Size " + alerts.size());
    }

    public void removeItem(int position) {
        alerts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alerts.size());
        this.notifyDataSetChanged();
    }
}
