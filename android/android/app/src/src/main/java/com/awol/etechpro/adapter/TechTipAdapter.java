package com.awol.etechpro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.awol.etechpro.R;
import com.awol.etechpro.model.TechTip;
import com.awol.etechpro.ui.DetailActivity;

import java.util.List;

public class TechTipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FEATURED = 0;
    private static final int VIEW_TYPE_REGULAR  = 1;

    private Context context;
    private List<TechTip> techTipList;

    public TechTipAdapter(Context context, List<TechTip> techTipList) {
        this.context = context;
        this.techTipList = techTipList;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_FEATURED : VIEW_TYPE_REGULAR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FEATURED) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_tech_tip_featured, parent, false);
            return new FeaturedViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_tech_tip, parent, false);
            return new RegularViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TechTip tip = techTipList.get(position);

        if (holder instanceof FeaturedViewHolder) {
            FeaturedViewHolder fHolder = (FeaturedViewHolder) holder;
            fHolder.tvTitle.setText(tip.getTitle());
            fHolder.tvDescription.setText(tip.getDescription());
            fHolder.tvDate.setText(formatDate(tip.getCreatedAt()));

            String imageUrl = tip.getDisplayImageUrl();
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_video_placeholder)
                        .error(R.drawable.ic_video_placeholder)
                        .centerCrop()
                        .into(fHolder.ivImage);
            }

            fHolder.itemView.setOnClickListener(v -> openDetail(tip));

        } else if (holder instanceof RegularViewHolder) {
            RegularViewHolder rHolder = (RegularViewHolder) holder;
            rHolder.tvTitle.setText(tip.getTitle());
            rHolder.tvDescription.setText(tip.getDescription());
            rHolder.tvDate.setText(formatDate(tip.getCreatedAt()));

            String imageUrl = tip.getDisplayImageUrl();
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_video_placeholder)
                        .error(R.drawable.ic_video_placeholder)
                        .centerCrop()
                        .into(rHolder.ivThumbnail);
            }

            rHolder.itemView.setOnClickListener(v -> openDetail(tip));
        }
    }

    private void openDetail(TechTip tip) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_TITLE,       tip.getTitle());
        intent.putExtra(DetailActivity.EXTRA_DESCRIPTION, tip.getDescription());
        intent.putExtra(DetailActivity.EXTRA_IMAGE_URL,   tip.getDisplayImageUrl());
        intent.putExtra(DetailActivity.EXTRA_VIDEO_LINK,  tip.getVideoLink());
        intent.putExtra(DetailActivity.EXTRA_WEBSITE_URL, tip.getWebsiteUrl());
        intent.putExtra(DetailActivity.EXTRA_DATE,        formatDate(tip.getCreatedAt()));
        context.startActivity(intent);
    }

    private String formatDate(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "";
        // createdAt comes as "2026-08-24T15:11:39" — format to "August 24, 2026"
        try {
            String[] parts = createdAt.split("T")[0].split("-");
            String[] months = {"January","February","March","April","May","June",
                               "July","August","September","October","November","December"};
            int month = Integer.parseInt(parts[1]) - 1;
            return months[month] + " " + parts[2] + ", " + parts[0];
        } catch (Exception e) {
            return createdAt;
        }
    }

    @Override
    public int getItemCount() {
        return techTipList != null ? techTipList.size() : 0;
    }

    public void updateList(List<TechTip> newList) {
        this.techTipList = newList;
        notifyDataSetChanged();
    }

    // ── ViewHolders ───────────────────────────────────────────────

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvDate;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage        = itemView.findViewById(R.id.iv_featured_image);
            tvTitle        = itemView.findViewById(R.id.tv_featured_title);
            tvDescription  = itemView.findViewById(R.id.tv_featured_description);
            tvDate         = itemView.findViewById(R.id.tv_featured_date);
        }
    }

    static class RegularViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvDescription, tvDate;

        RegularViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail   = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle       = itemView.findViewById(R.id.tv_tip_title);
            tvDescription = itemView.findViewById(R.id.tv_tip_description);
            tvDate        = itemView.findViewById(R.id.tv_tip_date);
        }
    }
}
