package com.studiotyche.apps.android.jobrunner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.AlertViewHolder> {
    String TAG = "FeedAdapter";

    @IntDef({RECENT, SAVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Name {
    }

    public static final int RECENT = 0;
    public static final int SAVED = 1;
    @Name
    int tabName;

    Context context;
    List<Alert> alerts;

    public FeedAdapter(Context context, List<Alert> alerts, @Name int tabName) {
        this.alerts = alerts;
        this.context = context;
        this.tabName = tabName;
        Log.d(TAG, "Received alerts " + alerts.size());
    }

    @Override
    public AlertViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
        AlertViewHolder avh = new AlertViewHolder(v);
        return avh;
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

    @Override
    public int getItemViewType(int position) {
        return tabName;
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

            if (tabName == RECENT) {
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onSaveClicked(view, getAdapterPosition());
                    }
                });
            } else {
                btnSave.setText("Remove");
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRemoveClicked(view, getAdapterPosition());
                    }
                });
            }
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
        DbHelper.getInstance(context)
                .saveAlert(alerts.get(pos));
        removeItem(pos);
        addItem(0);
        Snackbar.make(view, "Item Saved", Snackbar.LENGTH_LONG)
                .setAction("UNDO", null).show();
        view.setEnabled(false);
    }

    void onRemoveClicked(View view, int pos) {
        DbHelper.getInstance(context)
                .removeAlert(alerts.get(pos));
        removeItem(pos);
        Snackbar.make(view, "Item Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", null).show();
        view.setEnabled(false);
    }

    public void addItem(int pos) {
        alerts.clear();
        if (tabName == RECENT)
            alerts.addAll(pos, DbHelper.getInstance(context).getAllAlerts(DbHelper.RECENT, 20));
        else if (tabName == SAVED)
            alerts.addAll(pos, DbHelper.getInstance(context).getAllAlerts(DbHelper.SAVED, 20));
        this.notifyItemInserted(pos);
        this.notifyItemRangeInserted(pos, alerts.size());
        this.notifyItemRangeChanged(pos, alerts.size());
        this.notifyDataSetChanged();
        Log.i(TAG, "From FEEDAdapterv addItem to " + tabName + " Size " + alerts.size());
    }

    void removeItem(int position) {
        alerts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alerts.size());
        this.notifyDataSetChanged();
    }
}
