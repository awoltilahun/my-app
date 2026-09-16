package com.awol.etechpro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    private RecyclerView rvTechTips;
    private RecyclerView rvAppLinks;
    private EditText etSearch;
    private ProgressBar progressBar;
    private AdView bannerAdView;

    private TechTipAdapter techTipAdapter;
    private AppLinkAdapter appLinkAdapter;

    private List<TechTip> techTipList = new ArrayList<>();
    private List<AppLink> appLinkList = new ArrayList<>();

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;

    private static final String BANNER_AD_UNIT_ID       = "ca-app-pub-3940256099942544/6300978111";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID     = "ca-app-pub-3940256099942544/5224354917";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the default action bar since we have custom header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Etech Pro");
        }

        MobileAds.initialize(this, status -> Log.d(TAG, "AdMob initialized"));

        initViews();
        setupRecyclerViews();
        setupSearchBar();
        loadBannerAd();
        loadInterstitialAd();
        loadRewardedAd();

        fetchTechTips();
        fetchAppLinks();
    }

    private void initViews() {
        rvTechTips   = findViewById(R.id.rv_tech_tips);
        rvAppLinks   = findViewById(R.id.rv_app_links);
        etSearch     = findViewById(R.id.et_search);
        progressBar  = findViewById(R.id.progress_bar);
        bannerAdView = findViewById(R.id.banner_ad_view);
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
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Search as user types
                String query = s.toString().trim();
                if (query.length() >= 1) {
                    searchAppLinks(query);
                } else {
                    // Restore full list when search is cleared
                    fetchAppLinks();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchTechTips() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().getAllTechTips().enqueue(new Callback<List<TechTip>>() {
            @Override
            public void onResponse(Call<List<TechTip>> call, Response<List<TechTip>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    techTipList = response.body();
                    techTipAdapter.updateList(techTipList);
                }
            }
            @Override
            public void onFailure(Call<List<TechTip>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load tips: " + t.getMessage());
                Toast.makeText(MainActivity.this,
                    "Failed to load tips. Check your connection.", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Failed to fetch app links: " + t.getMessage());
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
                    // If no results, show empty list
                    appLinkAdapter.updateList(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<List<AppLink>> call, Throwable t) {
                Log.e(TAG, "Search failed: " + t.getMessage());
            }
        });
    }

    private void loadBannerAd() {
        bannerAdView.loadAd(new AdRequest.Builder().build());
    }

    private void loadInterstitialAd() {
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, new AdRequest.Builder().build(),
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd ad) {
                    mInterstitialAd = ad;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitialAd();
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
                @Override
                public void onAdLoaded(RewardedAd ad) {
                    mRewardedAd = ad;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() {
                            mRewardedAd = null;
                            loadRewardedAd();
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
            Toast.makeText(this, "Bonus loading, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showInterstitialAd();
        int id = item.getItemId();
        if (id == R.id.menu_about) {
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
