package com.awol.etechpro.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;
import com.awol.etechpro.adapter.TechTipAdapter;
import com.awol.etechpro.api.RetrofitClient;
import com.awol.etechpro.model.TechTip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    private RecyclerView rvPlayTips;
    private TechTipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Play Videos");
        }

        rvPlayTips = findViewById(R.id.rv_play_tips);
        rvPlayTips.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TechTipAdapter(this, new ArrayList<>());
        rvPlayTips.setAdapter(adapter);

        fetchTechTips();
    }

    private void fetchTechTips() {
        RetrofitClient.getApiService().getAllTechTips(System.currentTimeMillis()).enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Only show tips that have video links
                    List<TechTip> videoTips = new ArrayList<>();
                    for (TechTip tip : response.body()) {
                        if (tip.getVideoLink() != null && !tip.getVideoLink().isEmpty()) {
                            videoTips.add(tip);
                        }
                    }
                    adapter.updateList(videoTips);
                    if (videoTips.isEmpty()) {
                        Toast.makeText(PlayActivity.this,
                            "No videos available yet.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                Log.e(TAG, "Failed: " + t.getMessage());
                Toast.makeText(PlayActivity.this,
                    "Failed to load videos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
