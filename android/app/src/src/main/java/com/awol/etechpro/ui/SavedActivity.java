package com.awol.etechpro.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;
import com.awol.etechpro.adapter.AppLinkAdapter;
import com.awol.etechpro.model.AppLink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private RecyclerView rvSaved;
    private AppLinkAdapter adapter;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Saved Apps");
        }

        rvSaved = findViewById(R.id.rv_saved);
        layoutEmpty = findViewById(R.id.layout_empty);
        rvSaved.setLayoutManager(new LinearLayoutManager(this));

        List<AppLink> savedApps = getSavedApps();
        adapter = new AppLinkAdapter(this, savedApps);
        rvSaved.setAdapter(adapter);

        if (savedApps.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSaved.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSaved.setVisibility(View.VISIBLE);
        }
    }

    private List<AppLink> getSavedApps() {
        SharedPreferences prefs = getSharedPreferences("saved_apps", MODE_PRIVATE);
        String json = prefs.getString("apps", null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<AppLink>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
