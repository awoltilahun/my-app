package com.awol.etechpro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.awol.etechpro.R;
import com.awol.etechpro.model.TechTip;
import com.awol.etechpro.ui.DetailActivity;
import com.awol.etechpro.util.LevelPlayAdsManager;
import com.awol.etechpro.util.SavedManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TechTipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FEATURED  = 0;
    private static final int VIEW_TYPE_REGULAR   = 1;
    private static final int VIEW_TYPE_AD        = 2;

    // Show MREC after every 3 tips (positions 3, 7, 11, 15 in the mixed list)
    private static final int AD_INTERVAL = 3;

    private Context context;
    private List<TechTip> techTipList;
    // Track which adapter positions have already requested an MREC — prevents reload on scroll
    private final Set<Integer> requestedAdPositions = new HashSet<>();

    public TechTipAdapter(Context context, List<TechTip> techTipList) {
        this.context = context;
        this.techTipList = techTipList;
    }

    // ── Item count with injected ads ──────────────────────────────

    @Override
    public int getItemCount() {
        if (techTipList == null || techTipList.isEmpty()) return 0;
        int tipCount = techTipList.size();
        // Number of ads to inject — one after every AD_INTERVAL tips
        // First ad after position AD_INTERVAL (not after position 0)
        int adCount = tipCount / AD_INTERVAL;
        return tipCount + adCount;
    }

    // ── View type — determines if position is a tip or an ad ─────

    @Override
    public int getItemViewType(int position) {
        if (isAdPosition(position)) return VIEW_TYPE_AD;
        int tipIndex = getTipIndex(position);
        return tipIndex == 0 ? VIEW_TYPE_FEATURED : VIEW_TYPE_REGULAR;
    }

    /**
     * Returns true if the given adapter position should show an ad.
     * Ad positions: AD_INTERVAL, AD_INTERVAL*2+1, AD_INTERVAL*3+2 ...
     * Pattern: every (AD_INTERVAL + 1)th item starting at AD_INTERVAL
     */
    private boolean isAdPosition(int position) {
        // Every (AD_INTERVAL + 1) positions, starting from AD_INTERVAL
        return (position + 1) % (AD_INTERVAL + 1) == 0;
    }

    /**
     * Converts adapter position to tip list index,
     * accounting for injected ad positions.
     */
    private int getTipIndex(int position) {
        // Count how many ads come before this position
        int adsBeforePosition = (position + 1) / (AD_INTERVAL + 1);
        return position - adsBeforePosition;
    }

    // ── ViewHolder creation ───────────────────────────────────────

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FEATURED) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_tech_tip_featured, parent, false);
            return new FeaturedViewHolder(view);
        } else if (viewType == VIEW_TYPE_AD) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_ad_mrec, parent, false);
            return new AdViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_tech_tip, parent, false);
            return new RegularViewHolder(view);
        }
    }

    // ── ViewHolder binding ────────────────────────────────────────

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdViewHolder) {
            AdViewHolder adHolder = (AdViewHolder) holder;
            // Only load once per position — prevents reload every time user scrolls
            if (!requestedAdPositions.contains(position)) {
                requestedAdPositions.add(position);
                adHolder.mrecContainer.setVisibility(View.GONE);
                if (context instanceof Activity) {
                    LevelPlayAdsManager.loadMrec((Activity) context, adHolder.mrecContainer);
                }
            }
            return;
        }

        TechTip tip = techTipList.get(getTipIndex(position));

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
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_video_placeholder)
                    .centerCrop()
                    .into(rHolder.ivThumbnail);

            // Save button
            updateSaveIcon(rHolder.ivSave, tip);
            rHolder.ivSave.setOnClickListener(v -> {
                if (SavedManager.isSaved(context, tip)) {
                    SavedManager.removeTip(context, tip);
                    Toast.makeText(context, "Removed from saved", Toast.LENGTH_SHORT).show();
                } else {
                    SavedManager.saveTip(context, tip);
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                }
                updateSaveIcon(rHolder.ivSave, tip);
            });

            rHolder.itemView.setOnClickListener(v -> openDetail(tip));

            // Share button
            if (rHolder.ivShare != null) {
                rHolder.ivShare.setOnClickListener(v -> {
                    android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tip.getTitle());
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                            tip.getTitle() + "\n\n" + tip.getDescription() +
                            (tip.getWebsiteUrl() != null && !tip.getWebsiteUrl().isEmpty() ?
                            "\n\nGet it: " + tip.getWebsiteUrl() : "") +
                            "\n\nShared from Etech Pro");
                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
                });
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void updateSaveIcon(ImageView ivSave, TechTip tip) {
        if (SavedManager.isSaved(context, tip)) {
            ivSave.setColorFilter(ContextCompat.getColor(context, R.color.colorBlue));
        } else {
            ivSave.clearColorFilter();
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
        intent.putExtra(DetailActivity.EXTRA_TIP_ID,      tip.getId() != null ? tip.getId() : -1L);
        context.startActivity(intent);
    }

    private String formatDate(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "";
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

    public void updateList(List<TechTip> newList) {
        this.techTipList = newList;
        requestedAdPositions.clear(); // reset so ads reload with fresh data
        notifyDataSetChanged();
    }

    // ── ViewHolders ───────────────────────────────────────────────

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvDate;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage       = itemView.findViewById(R.id.iv_featured_image);
            tvTitle       = itemView.findViewById(R.id.tv_featured_title);
            tvDescription = itemView.findViewById(R.id.tv_featured_description);
            tvDate        = itemView.findViewById(R.id.tv_featured_date);
        }
    }

    static class RegularViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail, ivSave, ivShare;
        TextView tvTitle, tvDescription, tvDate;

        RegularViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail   = itemView.findViewById(R.id.iv_thumbnail);
            ivSave        = itemView.findViewById(R.id.iv_save);
            ivShare       = itemView.findViewById(R.id.iv_share);
            tvTitle       = itemView.findViewById(R.id.tv_tip_title);
            tvDescription = itemView.findViewById(R.id.tv_tip_description);
            tvDate        = itemView.findViewById(R.id.tv_tip_date);
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mrecContainer;

        AdViewHolder(@NonNull View itemView) {
            super(itemView);
            mrecContainer = itemView.findViewById(R.id.mrec_ad_container);
        }
    }
}
