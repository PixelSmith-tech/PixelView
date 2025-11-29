package com.pixelsmith.pixelview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private final Context context;
    private final List<AppItem> appList;
    private final boolean isRecent;
    private final int maxItemsToShow;


    public AppAdapter(Context context, List<AppItem> appList, boolean isRecent, int maxItemsToShow) {
        this.context = context;
        this.appList = appList;
        this.isRecent = isRecent;
        this.maxItemsToShow = maxItemsToShow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isRecent ? R.layout.item_recent_app : R.layout.item_app;
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppItem app = appList.get(position);

        holder.label.setText(app.label);
        holder.icon.setImageDrawable(app.icon);

        ViewGroup.LayoutParams layoutParams = holder.icon.getLayoutParams();

        if (app.isBanner) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.icon.setBackground(null);
            holder.icon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            int size = dpToPx(64);
            layoutParams.width = size;
            layoutParams.height = size;
            holder.icon.setBackgroundResource(R.drawable.bg_circle);
            holder.icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        holder.icon.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(v -> {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                SharedPreferences prefs = context.getSharedPreferences("recent_apps", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(app.packageName, System.currentTimeMillis());
                editor.apply();
                context.startActivity(launchIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (maxItemsToShow > 0) ? Math.min(appList.size(), maxItemsToShow) : appList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.appIcon);
            label = itemView.findViewById(R.id.appLabel);
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
