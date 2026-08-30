package com.awol.etechpro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Categories");
        }

        rvCategories = findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        // Sample categories
        List<String[]> categories = new ArrayList<>();
        categories.add(new String[]{"📱", "Android Apps", "12 apps"});
        categories.add(new String[]{"🎓", "Tutorials", "8 tips"});
        categories.add(new String[]{"🎮", "Games", "6 apps"});
        categories.add(new String[]{"📷", "Photography", "4 apps"});
        categories.add(new String[]{"🎵", "Music", "5 apps"});
        categories.add(new String[]{"💼", "Productivity", "9 apps"});
        categories.add(new String[]{"🔒", "Security", "3 apps"});
        categories.add(new String[]{"🌐", "Social Media", "7 apps"});

        categoryAdapter = new CategoryAdapter(this, categories, category ->
            Toast.makeText(this, category + " coming soon!", Toast.LENGTH_SHORT).show()
        );
        rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
