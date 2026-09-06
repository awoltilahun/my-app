package com.awol.etechpro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.awol.etechpro.R;
import com.awol.etechpro.adapter.TechTipAdapter;
import com.awol.etechpro.api.RetrofitClient;
import com.awol.etechpro.model.TechTip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryTipsActivity extends AppCompatActivity {

    private RecyclerView rvTips;
    private TechTipAdapter adapter;
    private LinearLayout layoutEmpty, layoutLoading;
    private SwipeRefreshLayout swipeRefresh;
    private String categoryName;
    private String filterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tips);

        categoryName = getIntent().getStringExtra("category_name");
        filterType   = getIntent().getStringExtra("filter_type");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_category_tips);
        if (toolbar != null) setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(categoryName != null ? categoryName : "Tips");
        }

        rvTips       = findViewById(R.id.rv_category_tips);
        layoutEmpty  = findViewById(R.id.layout_empty_category);
        layoutLoading = new LinearLayout(this); // dummy
        swipeRefresh = findViewById(R.id.swipe_refresh_category);

        rvTips.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TechTipAdapter(this, new ArrayList<>());
        rvTips.setAdapter(adapter);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadFilteredTips);
        }

        loadFilteredTips();
    }

    private void loadFilteredTips() {
        RetrofitClient.getApiService().getAllTechTips(System.currentTimeMillis())
            .enqueue(new Callback<List<TechTip>>() {
                @Override
                public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<TechTip> filtered = filterTips(response.body());
                        adapter.updateList(filtered);
                        if (filtered.isEmpty()) {
                            rvTips.setVisibility(View.GONE);
                            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            rvTips.setVisibility(View.VISIBLE);
                            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<TechTip>> call, Throwable t) {
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                }
            });
    }

    private List<TechTip> filterTips(List<TechTip> all) {
        List<TechTip> result = new ArrayList<>();
        for (TechTip tip : all) {
            String videoLink  = tip.getVideoLink()  != null ? tip.getVideoLink().trim()  : "";
            String websiteUrl = tip.getWebsiteUrl() != null ? tip.getWebsiteUrl().trim() : "";
            boolean hasYoutube    = videoLink.contains("youtube") || videoLink.contains("youtu.be");
            boolean hasPlayStore  = websiteUrl.contains("play.google.com");
            boolean hasNoLinks    = !hasYoutube && !hasPlayStore && videoLink.isEmpty() && websiteUrl.isEmpty();

            switch (filterType != null ? filterType : "") {
                case "playstore":
                    if (hasPlayStore) result.add(tip);
                    break;
                case "youtube":
                    if (hasYoutube) result.add(tip);
                    break;
                case "tips_only":
                    if (hasNoLinks) result.add(tip);
                    break;
                default:
                    result.add(tip);
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
