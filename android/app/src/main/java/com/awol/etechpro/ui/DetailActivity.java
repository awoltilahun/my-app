package com.awol.etechpro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.awol.etechpro.R;
import com.awol.etechpro.adapter.TechTipAdapter;
import com.awol.etechpro.api.RetrofitClient;
import com.awol.etechpro.model.TechTip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    // Keys for Intent extras
    public static final String EXTRA_TITLE       = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_IMAGE_URL   = "extra_image_url";
    public static final String EXTRA_VIDEO_LINK  = "extra_video_link";
    public static final String EXTRA_WEBSITE_URL = "extra_website_url";
    public static final String EXTRA_DATE        = "extra_date";
    public static final String EXTRA_TIP_ID      = "extra_tip_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_detail);
        if (toolbar != null) setSupportActionBar(toolbar);
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
        long   currentId  = getIntent().getLongExtra(EXTRA_TIP_ID, -1);

        // Bind views
        ImageView ivImage      = findViewById(R.id.iv_detail_image);
        TextView tvTitle       = findViewById(R.id.tv_detail_title);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvDate        = findViewById(R.id.tv_detail_date);
        Button btnWebsite      = findViewById(R.id.btn_website);
        Button btnWatchVideo   = findViewById(R.id.btn_watch_video);
        Button btnDownload     = findViewById(R.id.btn_download);

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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl)));
            });
        }

        // Show Watch Video button if videoLink exists
        if (videoLink != null && !videoLink.isEmpty()) {
            btnWatchVideo.setVisibility(View.VISIBLE);
            btnWatchVideo.setOnClickListener(v -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink)));
            });
        }

        // Show Open Now button if websiteUrl is a Play Store link
        if (websiteUrl != null && websiteUrl.contains("play.google.com")) {
            btnWebsite.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl)));
            });
        }

        // Load More Tips
        loadMoreTips(currentId);
    }

    private void loadMoreTips(long currentId) {
        RetrofitClient.getApiService().getAllTechTips(System.currentTimeMillis())
                .enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter out the current tip
                    List<TechTip> moreTips = new ArrayList<>();
                    for (TechTip tip : response.body()) {
                        if (tip.getId() == null || tip.getId() != currentId) {
                            moreTips.add(tip);
                        }
                    }
                    if (!moreTips.isEmpty()) {
                        LinearLayout layoutMoreTips = findViewById(R.id.layout_more_tips);
                        RecyclerView rvMoreTips     = findViewById(R.id.rv_more_tips);
                        layoutMoreTips.setVisibility(View.VISIBLE);
                        rvMoreTips.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                        rvMoreTips.setAdapter(new TechTipAdapter(DetailActivity.this, moreTips));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                // Silently fail — more tips is optional, don't show error
            }
        });
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
