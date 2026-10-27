package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.SdkInitializationListener;

public class InMobiAdsManager {

    private static final String TAG = "InMobiAdsManager";

    // ── IDs ───────────────────────────────────────────────────────
    private static final String ACCOUNT_ID            = "312b9c5e25d041c9b8238927b96de56e";
    private static final long   PLACEMENT_BANNER       = 1000085256L;
    private static final long   PLACEMENT_INTERSTITIAL = 10000852564L;

    private static boolean initialized = false;
    private static InMobiInterstitial interstitialAd;

    // ── Initialize — MUST be called on main thread ────────────────

    public static void initialize(Context context) {
        if (initialized) return;
        InMobiSdk.init(context.getApplicationContext(), ACCOUNT_ID, null,
                new SdkInitializationListener() {
                    @Override
                    public void onInitializationComplete(@Nullable Error error) {
                        if (error != null) {
                            Log.e(TAG, "❌ InMobi init failed: " + error.getMessage());
                        } else {
                            initialized = true;
                            Log.d(TAG, "✅ InMobi init successful — account: " + ACCOUNT_ID);
                            loadInterstitial(context);
                        }
                    }
                });
    }

    public static boolean isInitialized() {
        return initialized;
    }

    // ── Banner ────────────────────────────────────────────────────

    public static void loadBanner(Activity activity, ViewGroup container) {
        if (!initialized) {
            Log.e(TAG, "loadBanner called before init — skipping");
            return;
        }
        Log.d(TAG, "Banner load requested — placement: " + PLACEMENT_BANNER);
        InMobiBanner banner = new InMobiBanner(activity, PLACEMENT_BANNER);
        banner.setEnableAutoRefresh(false);
        banner.setBannerSize(320, 50); // standard banner size in dp
        banner.setListener(new BannerAdEventListener() {

            // Called when ad is fetched and ready to display
            @Override
            public void onAdLoadSucceeded(@NonNull InMobiBanner ad,
                    @NonNull AdMetaInfo metaInfo) {
                Log.d(TAG, "✅ Banner loaded");
                activity.runOnUiThread(() -> {
                    container.removeAllViews();
                    container.addView(ad);
                    container.setVisibility(View.VISIBLE);
                });
            }

            // Called when ad fetch fails
            @Override
            public void onAdLoadFailed(@NonNull InMobiBanner ad,
                    @NonNull InMobiAdRequestStatus status) {
                Log.e(TAG, "❌ Banner failed: " + status.getMessage()
                        + " (code: " + status.getStatusCode() + ")");
                activity.runOnUiThread(() ->
                        container.setVisibility(View.GONE));
            }

            // Called when ad cannot be fetched from server
            @Override
            public void onAdFetchFailed(@NonNull InMobiBanner ad,
                    @NonNull InMobiAdRequestStatus status) {
                Log.e(TAG, "❌ Banner fetch failed: " + status.getMessage());
                activity.runOnUiThread(() ->
                        container.setVisibility(View.GONE));
            }

            @Override
            public void onAdDisplayed(@NonNull InMobiBanner ad) {
                Log.d(TAG, "Banner displayed");
            }

            @Override
            public void onAdDismissed(@NonNull InMobiBanner ad) {}

            @Override
            public void onAdClicked(@NonNull InMobiBanner ad,
                    @Nullable java.util.Map<Object, Object> params) {
                Log.d(TAG, "Banner clicked");
            }
        });
        banner.load();
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void loadInterstitial(Context context) {
        if (!initialized) return;
        Log.d(TAG, "Interstitial load requested — placement: " + PLACEMENT_INTERSTITIAL);
        interstitialAd = new InMobiInterstitial(context.getApplicationContext(),
                PLACEMENT_INTERSTITIAL,
                new InterstitialAdEventListener() {

                    @Override
                    public void onAdLoadSucceeded(@NonNull InMobiInterstitial ad,
                            @NonNull AdMetaInfo metaInfo) {
                        Log.d(TAG, "✅ Interstitial loaded");
                    }

                    @Override
                    public void onAdLoadFailed(@NonNull InMobiInterstitial ad,
                            @NonNull InMobiAdRequestStatus status) {
                        Log.e(TAG, "❌ Interstitial failed: " + status.getMessage()
                                + " (code: " + status.getStatusCode() + ")");
                    }

                    @Override
                    public void onAdFetchFailed(@NonNull InMobiInterstitial ad,
                            @NonNull InMobiAdRequestStatus status) {
                        Log.e(TAG, "❌ Interstitial fetch failed: " + status.getMessage());
                    }

                    @Override
                    public void onAdDisplayed(@NonNull InMobiInterstitial ad,
                            @NonNull AdMetaInfo metaInfo) {
                        Log.d(TAG, "Interstitial displayed");
                    }

                    @Override
                    public void onAdDisplayFailed(@NonNull InMobiInterstitial ad) {
                        Log.e(TAG, "❌ Interstitial display failed");
                    }

                    @Override
                    public void onAdDismissed(@NonNull InMobiInterstitial ad) {
                        Log.d(TAG, "Interstitial dismissed — preloading next");
                        loadInterstitial(context);
                    }

                    @Override
                    public void onAdClicked(@NonNull InMobiInterstitial ad,
                            @Nullable java.util.Map<Object, Object> params) {
                        Log.d(TAG, "Interstitial clicked");
                    }
                });
        interstitialAd.load();
    }

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        if (interstitialAd != null && interstitialAd.isReady()) {
            Log.d(TAG, "Showing interstitial");
            interstitialAd.show(activity);
            if (onComplete != null) onComplete.run();
        } else {
            Log.e(TAG, "Interstitial not ready — skipping");
            if (onComplete != null) onComplete.run();
        }
    }

    public static boolean isInterstitialReady() {
        return interstitialAd != null && interstitialAd.isReady();
    }
}
