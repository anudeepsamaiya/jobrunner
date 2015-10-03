package com.studiotyche.apps.android.jobrunner;
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

import com.studiotyche.apps.android.jobrunner.persistence.DbHelper;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AlertViewHolder> {
    String tag = "RVAdapter";

    static Context context;
    static List<Alert> alerts;

    public RVAdapter(Context context, List<Alert> alerts) {
        RVAdapter.alerts = alerts;
        RVAdapter.context = context;
        Log.d(tag, "recievd alerts " + alerts.size());
    }

    @Override
    public AlertViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
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
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alerts.get(getAdapterPosition()).getLink()));
                    context.startActivity(browserIntent);
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSaveClicked(view);
                }
            });
        }

        void onSaveClicked(View view) {
            DbHelper.getInstance(context).addSavedFeedRecord(alerts.get(getAdapterPosition()));
            Snackbar.make(view, "Item Saved", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", null).show();
            view.setEnabled(false);
        }
    }
}
