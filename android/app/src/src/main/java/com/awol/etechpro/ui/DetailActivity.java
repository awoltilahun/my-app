package com.awol.etechpro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.awol.etechpro.R;

public class DetailActivity extends AppCompatActivity {

    // Keys for Intent extras
    public static final String EXTRA_TITLE       = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_IMAGE_URL   = "extra_image_url";
    public static final String EXTRA_VIDEO_LINK  = "extra_video_link";
    public static final String EXTRA_WEBSITE_URL = "extra_website_url";
    public static final String EXTRA_DATE        = "extra_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Setup back button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }

        // Get data from intent
        String title      = getIntent().getStringExtra(EXTRA_TITLE);
        String description= getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String imageUrl   = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        String videoLink  = getIntent().getStringExtra(EXTRA_VIDEO_LINK);
        String websiteUrl = getIntent().getStringExtra(EXTRA_WEBSITE_URL);
        String date       = getIntent().getStringExtra(EXTRA_DATE);

        // Bind views
        ImageView ivImage         = findViewById(R.id.iv_detail_image);
        TextView tvTitle          = findViewById(R.id.tv_detail_title);
        TextView tvDescription    = findViewById(R.id.tv_detail_description);
        TextView tvDate           = findViewById(R.id.tv_detail_date);
        Button btnWebsite         = findViewById(R.id.btn_website);
        Button btnWatchVideo      = findViewById(R.id.btn_watch_video);
        Button btnDownload        = findViewById(R.id.btn_download);

        // Set text
        if (title != null)       tvTitle.setText(title);
        if (description != null) tvDescription.setText(description);
        if (date != null)        tvDate.setText(date);

        // Load image with Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_video_placeholder)
                    .centerCrop()
                    .into(ivImage);
        }

        // Show Visit Website button if websiteUrl exists
        if (websiteUrl != null && !websiteUrl.isEmpty()) {
            btnWebsite.setVisibility(View.VISIBLE);
            btnWebsite.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(intent);
            });
        }

        // Show Watch Video button if videoLink exists
        if (videoLink != null && !videoLink.isEmpty()) {
            btnWatchVideo.setVisibility(View.VISIBLE);
            btnWatchVideo.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                startActivity(intent);
            });
        }

        // Show Download button if websiteUrl looks like a Play Store link
        if (websiteUrl != null && websiteUrl.contains("play.google.com")) {
            btnWebsite.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(intent);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
