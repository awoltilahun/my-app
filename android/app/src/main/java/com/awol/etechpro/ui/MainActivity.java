package com.awol.etechpro.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private RecyclerView rvTechTips;
    private EditText etSearch;
    private View scrollContent; // now points to rv_tech_tips
    private LinearLayout layoutOffline, layoutLoading;
    private Button btnRetry;
    private LinearLayout navHome, navCategory, navPlay, navSaved;
    private SwipeRefreshLayout swipeRefresh;

    private TechTipAdapter techTipAdapter;
    private List<TechTip> techTipList = new ArrayList<>();

    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            // Silent background refresh - only update if response has data
            fetchTechTips();
            refreshHandler.postDelayed(this, 600000); // 10 minutes
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String searchQuery = getIntent().getStringExtra("search_query");

        // No toolbar — using custom search bar with icon popup menu

        initViews();
        setupRecyclerView();
        setupSearchBar();
        // Prevent search bar from getting focus on startup
        etSearch.clearFocus();
        setupBottomNav();

        // Request notification permission on first launch
        requestNotificationPermission();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            etSearch.setText(searchQuery);
            searchTechTips(searchQuery);
        } else {
            loadData();
        }
    }

    // ── Notification Permission ───────────────────────────────────

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Show system dialog asking user to allow notifications
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications disabled. You can enable them in Settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ── Views ─────────────────────────────────────────────────────

    private void initViews() {
        rvTechTips    = findViewById(R.id.rv_tech_tips);
        etSearch      = findViewById(R.id.et_search);
        scrollContent = findViewById(R.id.rv_tech_tips);
        layoutOffline = findViewById(R.id.layout_offline);
        layoutLoading = findViewById(R.id.layout_loading);
        btnRetry      = findViewById(R.id.btn_retry);
        navHome       = findViewById(R.id.nav_home);
        navCategory   = findViewById(R.id.nav_category);
        navPlay       = findViewById(R.id.nav_play);
        navSaved      = findViewById(R.id.nav_saved);
        swipeRefresh  = findViewById(R.id.swipe_refresh);

        btnRetry.setOnClickListener(v -> loadData());

        // Close search when user presses back on keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (query.isEmpty()) loadData();
                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                etSearch.clearFocus();
            }
            return false;
        });

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorBlue));
        swipeRefresh.setOnRefreshListener(this::fetchTechTips);

        // App icon popup menu
        ImageView ivAppIcon = findViewById(R.id.iv_app_icon);
        ivAppIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add(0, 1, 0, "Settings");
            popup.getMenu().add(0, 2, 1, "About");
            popup.getMenu().add(0, 3, 2, "Privacy Policy");
            popup.getMenu().add(0, 4, 3, "Contact Us");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1: startActivity(new Intent(this, SettingsActivity.class)); return true;
                    case 2: startActivity(new Intent(this, AboutActivity.class)); return true;
                    case 3: startActivity(new Intent(this, PrivacyPolicyActivity.class)); return true;
                    case 4: startActivity(new Intent(this, ContactActivity.class)); return true;
                    default: return false;
                }
            });
            popup.show();
        });
    }

    private void setupRecyclerView() {
        techTipAdapter = new TechTipAdapter(this, techTipList);
        rvTechTips.setLayoutManager(new LinearLayoutManager(this));
        rvTechTips.setAdapter(techTipAdapter);
    }

    private void setupSearchBar() {
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Hide content when search bar is focused
                scrollContent.setVisibility(View.GONE);
                layoutOffline.setVisibility(View.GONE);
                layoutLoading.setVisibility(View.GONE);
            } else {
                // Restore content when search bar loses focus
                if (etSearch.getText().toString().trim().isEmpty()) {
                    loadData();
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Hide everything while search bar is empty and focused
                    scrollContent.setVisibility(View.GONE);
                    layoutOffline.setVisibility(View.GONE);
                    layoutLoading.setVisibility(View.GONE);
                } else if (query.matches("\\d+")) {
                    searchTechTipById(Long.parseLong(query));
                } else {
                    searchTechTips(query);
                }
            }
        });
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> selectTab(0));
        navCategory.setOnClickListener(v -> {
            selectTab(1);
            startActivity(new Intent(this, CategoryActivity.class));
        });
        navPlay.setOnClickListener(v -> {
            selectTab(2);
            startActivity(new Intent(this, PlayActivity.class));
        });
        navSaved.setOnClickListener(v -> {
            selectTab(3);
            startActivity(new Intent(this, SavedActivity.class));
        });
    }

    private void selectTab(int index) {
        navHome.setBackgroundResource(index == 0 ? R.drawable.nav_selected_bg : 0);
        navCategory.setBackgroundResource(index == 1 ? R.drawable.nav_selected_bg : 0);
        navPlay.setBackgroundResource(index == 2 ? R.drawable.nav_selected_bg : 0);
        navSaved.setBackgroundResource(index == 3 ? R.drawable.nav_selected_bg : 0);
    }

    // ── Data Loading ──────────────────────────────────────────────

    private void loadData() {
        // Only show loading screen if content is not already visible
        if (scrollContent.getVisibility() != View.VISIBLE) {
            showLoading();
        }
        fetchTechTips();
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutOffline.setVisibility(View.GONE);
        scrollContent.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutLoading.setVisibility(View.GONE);
        layoutOffline.setVisibility(View.GONE);
        scrollContent.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        layoutLoading.setVisibility(View.GONE);
        layoutOffline.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);
    }

    private void fetchTechTips() {
        RetrofitClient.getApiService().getAllTechTips(System.currentTimeMillis()).enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && !response.body().isEmpty()) {
                    techTipList = response.body();
                    techTipAdapter.updateList(techTipList);
                    Log.d(TAG, "Tips loaded: " + techTipList.size());
                    showContent();
                } else if (techTipList.isEmpty()) {
                    showOffline();
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "Failed: " + t.getMessage());
                if (techTipList.isEmpty()) showOffline();
            }
        });
    }

    private void searchTechTipById(Long id) {
        showLoading();
        RetrofitClient.getApiService().getTechTipById(id).enqueue(new Callback<TechTip>() {
            @Override
            public void onResponse(Call<TechTip> call, Response<TechTip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TechTip> result = new ArrayList<>();
                    result.add(response.body());
                    techTipAdapter.updateList(result);
                    showContent();
                } else {
                    techTipAdapter.updateList(new ArrayList<>());
                    showContent();
                    Toast.makeText(MainActivity.this,
                        "No tip found with ID: " + id, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TechTip> call, Throwable t) { showOffline(); }
        });
    }

    private void searchTechTips(String keyword) {
        RetrofitClient.getApiService().searchTechTips(keyword).enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    techTipAdapter.updateList(response.body());
                    showContent();
                } else {
                    techTipAdapter.updateList(new ArrayList<>());
                    showContent();
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                Log.e(TAG, "Search failed: " + t.getMessage());
            }
        });
    }

    // ── Options Menu ──────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.menu_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        } else if (id == R.id.menu_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        } else if (id == R.id.menu_bonus) {
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ── Lifecycle ─────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        // Only load data if list is empty
        if (techTipAdapter != null && techTipList.isEmpty()) {
            loadData();
        }
        refreshHandler.postDelayed(refreshRunnable, 600000);
    }

    @Override
    protected void onPause() {
        refreshHandler.removeCallbacks(refreshRunnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        refreshHandler.removeCallbacks(refreshRunnable);
        super.onDestroy();
    }
}
