package com.awol.etechpro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;
import com.awol.etechpro.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar_category);
        if (toolbar != null) setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Categories");
        }

        rvCategories = findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        List<String[]> categories = new ArrayList<>();
        categories.add(new String[]{"⭐", "App Reviews", "Top rated apps reviewed"});
        categories.add(new String[]{"💡", "Tech Tips", "Daily tech tips and tricks"});
        categories.add(new String[]{"📰", "Tech News", "Latest technology news"});
        categories.add(new String[]{"📱", "Android Apps", "Best Android applications"});
        categories.add(new String[]{"🎓", "Tutorials", "Step by step guides"});
        categories.add(new String[]{"🎮", "Games", "Top mobile games"});
        categories.add(new String[]{"📷", "Photography", "Camera and photo apps"});
        categories.add(new String[]{"🎵", "Music & Audio", "Music streaming apps"});
        categories.add(new String[]{"💼", "Productivity", "Work smarter apps"});
        categories.add(new String[]{"🔒", "Security", "Privacy and security tools"});
        categories.add(new String[]{"🌐", "Social Media", "Social networking apps"});
        categories.add(new String[]{"🎬", "Video & Movies", "Streaming and video apps"});

        categoryAdapter = new CategoryAdapter(this, categories, categoryName -> {
            // Go back to home and search by category name
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("search_query", categoryName);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
