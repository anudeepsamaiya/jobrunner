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

/**
 * Created by AnudeepSamaiya on 02-10-2015.
 */
public class SavedFeedFragmentAdapter extends RecyclerView.Adapter<SavedFeedFragmentAdapter.ViewHolder> {

    String tag = "SavedFeedFragment";

    static Context context;
    static List<Alert> alerts;

    public SavedFeedFragmentAdapter(Context context, List<Alert> alerts) {
        SavedFeedFragmentAdapter.alerts = alerts;
        SavedFeedFragmentAdapter.context = context;
        Log.d(tag, "recievd alerts " + alerts.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
        ViewHolder avh = new ViewHolder(v);
        return avh;
    }

    @Override
    public void onBindViewHolder(ViewHolder alertViewHolder, final int i) {
        alertViewHolder.setIsRecyclable(false);
        alertViewHolder.tvDescription.setText(alerts.get(i).getDesc());

    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Button btnLink, btnRemove;
        View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            btnLink = (Button) itemView.findViewById(R.id.btnLink);
            btnRemove = (Button) itemView.findViewById(R.id.btnSave);
            btnRemove.setText("Remove");

            btnLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alerts.get(getAdapterPosition()).getLink()));
                    context.startActivity(browserIntent);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRemoveClicked(view, getAdapterPosition());
                }
            });
        }
    }

    void onRemoveClicked(View view, int pos) {
        DbHelper.getInstance(context).removeAlert(alerts.get(pos));
        removeItem(pos);
        Snackbar.make(view, "Item Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", null).show();
        view.setEnabled(false);
    }

    public void addItem(int pos) {
        notifyItemInserted(pos);
        notifyItemRangeChanged(pos, alerts.size());
    }

    public void removeItem(int position) {
        alerts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alerts.size());
    }

    public void updateItems() {

    }
}
