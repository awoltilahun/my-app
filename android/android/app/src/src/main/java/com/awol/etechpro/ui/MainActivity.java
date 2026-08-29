package com.awol.etechpro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.awol.etechpro.R;
import com.awol.etechpro.adapter.AppLinkAdapter;
import com.awol.etechpro.adapter.TechTipAdapter;
import com.awol.etechpro.api.RetrofitClient;
import com.awol.etechpro.model.AppLink;
import com.awol.etechpro.model.TechTip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Views
    private RecyclerView rvTechTips, rvAppLinks;
    private EditText etSearch;
    private AdView bannerAdView;
    private ScrollView scrollContent;
    private LinearLayout layoutOffline, layoutLoading;
    private Button btnRetry;

    // Nav tabs
    private LinearLayout navHome, navCategory, navPlay, navSaved;

    // Adapters
    private TechTipAdapter techTipAdapter;
    private AppLinkAdapter appLinkAdapter;

    private List<TechTip> techTipList = new ArrayList<>();
    private List<AppLink> appLinkList = new ArrayList<>();

    // AdMob
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;

    private static final String BANNER_AD_UNIT_ID       = "ca-app-pub-3940256099942544/6300978111";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID     = "ca-app-pub-3940256099942544/5224354917";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, status -> Log.d(TAG, "AdMob initialized"));

        initViews();
        setupRecyclerViews();
        setupSearchBar();
        setupBottomNav();
        loadBannerAd();
        loadInterstitialAd();
        loadRewardedAd();
        loadData();
    }

    private void initViews() {
        rvTechTips    = findViewById(R.id.rv_tech_tips);
        rvAppLinks    = findViewById(R.id.rv_app_links);
        etSearch      = findViewById(R.id.et_search);
        bannerAdView  = findViewById(R.id.banner_ad_view);
        scrollContent = findViewById(R.id.scroll_content);
        layoutOffline = findViewById(R.id.layout_offline);
        layoutLoading = findViewById(R.id.layout_loading);
        btnRetry      = findViewById(R.id.btn_retry);
        navHome       = findViewById(R.id.nav_home);
        navCategory   = findViewById(R.id.nav_category);
        navPlay       = findViewById(R.id.nav_play);
        navSaved      = findViewById(R.id.nav_saved);

        btnRetry.setOnClickListener(v -> loadData());
    }

    private void setupRecyclerViews() {
        techTipAdapter = new TechTipAdapter(this, techTipList);
        rvTechTips.setLayoutManager(new LinearLayoutManager(this));
        rvTechTips.setAdapter(techTipAdapter);

        appLinkAdapter = new AppLinkAdapter(this, appLinkList);
        rvAppLinks.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAppLinks.setAdapter(appLinkAdapter);
    }

    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() >= 1) {
                    searchAppLinks(query);
                } else {
                    fetchAppLinks();
                }
            }
        });
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> selectTab(0));
        navCategory.setOnClickListener(v -> {
            selectTab(1);
            showInterstitialAd();
            startActivity(new Intent(this, CategoryActivity.class));
        });
        navPlay.setOnClickListener(v -> {
            selectTab(2);
            showInterstitialAd();
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

    private void loadData() {
        showLoading();
        fetchTechTips();
        fetchAppLinks();
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
        RetrofitClient.getApiService().getAllTechTips().enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    techTipList = response.body();
                    techTipAdapter.updateList(techTipList);
                    showContent();
                } else {
                    showOffline();
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                Log.e(TAG, "Failed: " + t.getMessage());
                showOffline();
            }
        });
    }

    private void fetchAppLinks() {
        RetrofitClient.getApiService().getAllAppLinks().enqueue(new Callback<List<AppLink>>() {
            @Override
            public void onResponse(Call<List<AppLink>> call, Response<List<AppLink>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appLinkList = response.body();
                    appLinkAdapter.updateList(appLinkList);
                }
            }
            @Override
            public void onFailure(Call<List<AppLink>> call, Throwable t) {
                Log.e(TAG, "App links failed: " + t.getMessage());
            }
        });
    }

    private void searchAppLinks(String keyword) {
        RetrofitClient.getApiService().searchAppLinks(keyword).enqueue(new Callback<List<AppLink>>() {
            @Override
            public void onResponse(Call<List<AppLink>> call, Response<List<AppLink>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appLinkAdapter.updateList(response.body());
                } else {
                    appLinkAdapter.updateList(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<List<AppLink>> call, Throwable t) {
                Log.e(TAG, "Search failed: " + t.getMessage());
            }
        });
    }

    // ── AdMob ────────────────────────────────────────────────────

    private void loadBannerAd() {
        bannerAdView.loadAd(new AdRequest.Builder().build());
    }

    private void loadInterstitialAd() {
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, new AdRequest.Builder().build(),
            new InterstitialAdLoadCallback() {
                @Override public void onAdLoaded(InterstitialAd ad) {
                    mInterstitialAd = ad;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null; loadInterstitialAd();
                        }
                        @Override public void onAdFailedToShowFullScreenContent(AdError e) {
                            mInterstitialAd = null;
                        }
                    });
                }
                @Override public void onAdFailedToLoad(LoadAdError e) { mInterstitialAd = null; }
            });
    }

    public void showInterstitialAd() {
        if (mInterstitialAd != null) mInterstitialAd.show(this);
    }

    private void loadRewardedAd() {
        RewardedAd.load(this, REWARDED_AD_UNIT_ID, new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override public void onAdLoaded(RewardedAd ad) {
                    mRewardedAd = ad;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() {
                            mRewardedAd = null; loadRewardedAd();
                        }
                        @Override public void onAdFailedToShowFullScreenContent(AdError e) {
                            mRewardedAd = null;
                        }
                    });
                }
                @Override public void onAdFailedToLoad(LoadAdError e) { mRewardedAd = null; }
            });
    }

    public void showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, reward ->
                Toast.makeText(this, "Bonus unlocked!", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Loading bonus, try again.", Toast.LENGTH_SHORT).show();
        }
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
        if (id == R.id.menu_about) {
            showInterstitialAd();
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.menu_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        } else if (id == R.id.menu_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        } else if (id == R.id.menu_bonus) {
            showRewardedAd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume()  { super.onResume();  if (bannerAdView != null) bannerAdView.resume(); }
    @Override protected void onPause()   { if (bannerAdView != null) bannerAdView.pause();  super.onPause(); }
    @Override protected void onDestroy() { if (bannerAdView != null) bannerAdView.destroy(); super.onDestroy(); }
}
