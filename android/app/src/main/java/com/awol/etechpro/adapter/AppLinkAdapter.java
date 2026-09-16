package com.awol.etechpro.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;
import com.awol.etechpro.model.AppLink;

import java.util.List;

public class AppLinkAdapter extends RecyclerView.Adapter<AppLinkAdapter.AppLinkViewHolder> {

    private Context context;
    private List<AppLink> appLinkList;

    public AppLinkAdapter(Context context, List<AppLink> appLinkList) {
        this.context = context;
        this.appLinkList = appLinkList;
    }

    @NonNull
    @Override
    public AppLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_link, parent, false);
        return new AppLinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppLinkViewHolder holder, int position) {
        AppLink appLink = appLinkList.get(position);
        holder.tvAppName.setText(appLink.getName());

        // Show first letter as icon
        if (appLink.getName() != null && !appLink.getName().isEmpty()) {
            holder.tvAppInitial.setText(String.valueOf(appLink.getName().charAt(0)).toUpperCase());
        }

        holder.btnOpenPlayStore.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appLink.getPlaystoreUrl()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appLinkList != null ? appLinkList.size() : 0;
    }

    public void updateList(List<AppLink> newList) {
        this.appLinkList = newList;
        notifyDataSetChanged();
    }

    static class AppLinkViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppName;
        TextView tvAppInitial;
        Button btnOpenPlayStore;

        AppLinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvAppInitial = itemView.findViewById(R.id.tv_app_initial);
            btnOpenPlayStore = itemView.findViewById(R.id.btn_open_playstore);
        }
    }
}
