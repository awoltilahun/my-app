package com.awol.etechpro.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

public class TechTipAdapter extends RecyclerView.Adapter<TechTipAdapter.TechTipViewHolder> {

    private Context context;
    private List<TechTip> techTipList;

    public TechTipAdapter(Context context, List<TechTip> techTipList) {
        this.context = context;
        this.techTipList = techTipList;
    }

    @NonNull
    @Override
    public TechTipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tech_tip, parent, false);
        return new TechTipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechTipViewHolder holder, int position) {
        TechTip techTip = techTipList.get(position);
        holder.tvTitle.setText(techTip.getTitle());
        holder.tvDescription.setText(techTip.getDescription());

        String videoId = techTip.getYoutubeVideoId();
        if (videoId != null && !videoId.isEmpty()) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_video_placeholder)
                    .centerCrop()
                    .into(holder.ivThumbnail);
            holder.ivThumbnail.setVisibility(View.VISIBLE);
            holder.ivPlayButton.setVisibility(View.VISIBLE);
            holder.ivThumbnail.setOnClickListener(v -> {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(techTip.getVideoLink())));
            });
            holder.ivPlayButton.setOnClickListener(v -> {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(techTip.getVideoLink())));
            });
        } else {
            holder.ivThumbnail.setVisibility(View.GONE);
            holder.ivPlayButton.setVisibility(View.GONE);
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

    static class TechTipViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail, ivPlayButton;
        TextView tvTitle, tvDescription;

        TechTipViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            ivPlayButton = itemView.findViewById(R.id.iv_play_button);
            tvTitle = itemView.findViewById(R.id.tv_tip_title);
            tvDescription = itemView.findViewById(R.id.tv_tip_description);
        }
    }
}
