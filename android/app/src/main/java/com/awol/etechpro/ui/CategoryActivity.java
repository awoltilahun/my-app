package com.awol.etechpro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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

        rvCategories = findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        List<String[]> categories = new ArrayList<>();
        // [icon, name, subtitle, filter_type]
        categories.add(new String[]{"⭐", "App Reviews", "Top rated apps reviewed", "playstore"});
        categories.add(new String[]{"💡", "Tech Tips", "Daily tech tips and tricks", "tips_only"});
        categories.add(new String[]{"📰", "Tech News", "Latest technology news", "tips_only"});
        categories.add(new String[]{"📱", "Android Apps", "Best Android applications", "playstore"});
        categories.add(new String[]{"🎓", "Tutorials", "Step by step video guides", "youtube"});

        categoryAdapter = new CategoryAdapter(this, categories, categoryName -> {
            // Find the filter type for this category
            String filterType = "tag";
            for (String[] cat : categories) {
                if (cat[1].equals(categoryName)) {
                    filterType = cat[3];
                    break;
                }
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("search_query", categoryName);
            intent.putExtra("filter_type", filterType);
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
