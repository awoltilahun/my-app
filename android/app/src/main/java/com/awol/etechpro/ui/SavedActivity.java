package com.awol.etechpro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;
import com.awol.etechpro.adapter.TechTipAdapter;
import com.awol.etechpro.model.TechTip;
import com.awol.etechpro.util.SavedManager;

import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private RecyclerView rvSaved;
    private TechTipAdapter adapter;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_saved);
        if (toolbar != null) setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Saved Tips");
        }

        rvSaved     = findViewById(R.id.rv_saved);
        layoutEmpty = findViewById(R.id.layout_empty);

        rvSaved.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload saved tips every time screen is shown
        List<TechTip> savedTips = SavedManager.getSavedTips(this);
        adapter = new TechTipAdapter(this, savedTips);
        rvSaved.setAdapter(adapter);

        if (savedTips.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSaved.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSaved.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
