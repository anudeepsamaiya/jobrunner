package com.studiotyche.apps.android.jobrunner.adapters;

/**
 * Created by AnudeepSamaiya on 11-10-2015.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.studiotyche.apps.android.jobrunner.R;
import com.studiotyche.apps.android.jobrunner.models.Alert;
import com.studiotyche.apps.android.jobrunner.persistence.DatabaseHelper;
import com.studiotyche.apps.android.jobrunner.utils.customtabs.CustomTabActivityHelper;
import com.studiotyche.apps.android.jobrunner.utils.customtabs.WebviewFallback;

import java.util.List;

public class SavedFeedAdapter extends RecyclerView.Adapter<SavedFeedAdapter.AlertViewHolder> {
    String TAG = "FeedAdapter";

    Activity activity;
    Context context;
    List<Alert> alerts;

    private Bitmap mActionButtonBitmap;
    private Bitmap mCloseButtonBitmap;
    CustomTabActivityHelper mCustomTabActivityHelper;

    public SavedFeedAdapter(Context context, Activity activity, CustomTabActivityHelper mCustomTabActivityHelper, List<Alert> alerts) {
        this.alerts = alerts;
        this.context = context;
        this.activity = activity;
        this.mCustomTabActivityHelper = mCustomTabActivityHelper;
        Log.d(TAG, "Received alerts " + alerts.size());
        setupCustomTabHelper();
        decodeBitmaps(context);
    }

    private void setupCustomTabHelper() {
        mCustomTabActivityHelper.setConnectionCallback(mConnectionCallback);
    }

    private void decodeBitmaps(Context context) {
        mActionButtonBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_share_24dp);
        mCloseButtonBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_arrow_back_24dp);
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
        Button btnLink, btnShare;

        AlertViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            btnLink = (Button) itemView.findViewById(R.id.btnLink);
            btnShare = (Button) itemView.findViewById(R.id.btnShare);

            btnLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnLinkClicked(v, getAdapterPosition());
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                openCustomTab(alerts.get(pos).getLink());
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alerts.get(pos).getLink()));
                context.startActivity(browserIntent);
            }
        } catch (Exception e) {
            Snackbar.make(v, "Broken website link.", Snackbar.LENGTH_LONG)
                    .setAction("Report", null).show();
            //Toast.makeText(context,"Broken website link.",Toast.LENGTH_LONG).show();
        }
    }

    private void openCustomTab(String url) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        int color = context.getResources().getColor(R.color.colorPrimary);
        intentBuilder.setToolbarColor(color);
        intentBuilder.setShowTitle(true);

        String menuItemTitle = context.getString(R.string.menu_title_share);
        PendingIntent menuItemPendingIntent = createPendingShareIntent();
        intentBuilder.addMenuItem(menuItemTitle, menuItemPendingIntent);

        if (mCloseButtonBitmap != null) {
            intentBuilder.setCloseButtonIcon(mCloseButtonBitmap);
        }

        if (mActionButtonBitmap != null) {
            intentBuilder.setActionButton(mActionButtonBitmap, context.getString(R.string.menu_title_share), createPendingShareIntent());
        }

        intentBuilder.setStartAnimations(context,
                R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(context,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabActivityHelper.openCustomTab(
                activity, intentBuilder.build(), Uri.parse(url), new WebviewFallback());
    }

    private PendingIntent createPendingShareIntent() {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, "Get more such updates on JobRunner\nhttps://goo.gl/b3Nko0");
        return PendingIntent.getActivity(context, 0, actionIntent, 0);
    }

    /***
     * Use this method to make UI changes
     */
    private CustomTabActivityHelper.ConnectionCallback mConnectionCallback = new CustomTabActivityHelper.ConnectionCallback() {
        @Override
        public void onCustomTabsConnected() {
            Toast.makeText(context, "Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCustomTabsDisconnected() {
            Toast.makeText(context, "Service DisConnected", Toast.LENGTH_SHORT).show();
        }
    };


    private void onRemoveClicked(View view, int pos) {
        DatabaseHelper.getInstance(context)
                .removeAlert(alerts.get(pos));
        removeItem(pos);
        Snackbar.make(view, "Item Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", null).show();
        view.setEnabled(false);
    }

    private void onShareClicked(int adapterPosition) {
        Alert toShareAlert = alerts.get(adapterPosition);

        String shareText = toShareAlert.getTitle() + " \n" +
                toShareAlert.getDesc() + " \n" +
                toShareAlert.getLink() + " \n" +
                " Get more updates about Latest Jobs openings : \n" +
                "https://play.google.com/store/apps/details?id=com.studiotyche.apps.android.jobrunner";

        doShare(shareText);
    }

    private void doShare(String shareText) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }


    public void removeItem(int position) {
        alerts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alerts.size());
        this.notifyDataSetChanged();
    }
}

