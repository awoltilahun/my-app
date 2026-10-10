package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAdsManager {

    private static final String TAG = "UnityAdsManager";

    // ── Ad IDs ────────────────────────────────────────────────────
    public static final String GAME_ID              = "800393686";
    public static final String PLACEMENT_BANNER      = "BP_Banner_Android";
    public static final String PLACEMENT_INTERSTITIAL= "BP_Interstitial_Android";
    public static final String PLACEMENT_REWARDED    = "BP_Rewarded_Android";

    private static boolean initialized = false;

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        if (initialized) return;
        UnityAds.initialize(context, GAME_ID, false,
                new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        initialized = true;
                        Log.d(TAG, "Unity Ads initialized");
                    }
                    @Override
                    public void onInitializationFailed(
                            UnityAds.UnityAdsInitializationError error, String message) {
                        Log.e(TAG, "Unity Ads init failed: " + message);
                    }
                });
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void loadInterstitial() {
        UnityAds.load(PLACEMENT_INTERSTITIAL, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Interstitial loaded");
            }
            @Override
            public void onUnityAdsFailedToLoad(String placementId,
                    UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Interstitial failed to load: " + message);
            }
        });
    }

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        UnityAds.show(activity, PLACEMENT_INTERSTITIAL, new UnityAdsShowOptions(),
                new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowComplete(String placementId,
                            UnityAds.UnityAdsShowCompletionState state) {
                        Log.d(TAG, "Interstitial complete");
                        loadInterstitial(); // preload next
                        if (onComplete != null) onComplete.run();
                    }
                    @Override
                    public void onUnityAdsShowFailure(String placementId,
                            UnityAds.UnityAdsShowError error, String message) {
                        Log.e(TAG, "Interstitial show failed: " + message);
                        if (onComplete != null) onComplete.run();
                    }
                    @Override public void onUnityAdsShowStart(String placementId) {}
                    @Override public void onUnityAdsShowClick(String placementId) {}
                });
    }

    // ── Rewarded ──────────────────────────────────────────────────

    public static void loadRewarded() {
        UnityAds.load(PLACEMENT_REWARDED, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Rewarded loaded");
            }
            @Override
            public void onUnityAdsFailedToLoad(String placementId,
                    UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Rewarded failed to load: " + message);
            }
        });
    }

    public static void showRewarded(Activity activity,
            OnRewardEarnedListener rewardListener, Runnable onComplete) {
        UnityAds.show(activity, PLACEMENT_REWARDED, new UnityAdsShowOptions(),
                new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowComplete(String placementId,
                            UnityAds.UnityAdsShowCompletionState state) {
                        loadRewarded(); // preload next
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                            // User watched full ad — grant reward
                            if (rewardListener != null) rewardListener.onRewardEarned();
                        }
                        if (onComplete != null) onComplete.run();
                    }
                    @Override
                    public void onUnityAdsShowFailure(String placementId,
                            UnityAds.UnityAdsShowError error, String message) {
                        Log.e(TAG, "Rewarded show failed: " + message);
                        if (onComplete != null) onComplete.run();
                    }
                    @Override public void onUnityAdsShowStart(String placementId) {}
                    @Override public void onUnityAdsShowClick(String placementId) {}
                });
    }

    // ── Banner ────────────────────────────────────────────────────

    public static BannerView createBanner(Activity activity, FrameLayout container) {
        BannerView banner = new BannerView(activity, PLACEMENT_BANNER,
                new UnityBannerSize(320, 50));
        banner.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                Log.d(TAG, "Banner loaded");
                container.setVisibility(android.view.View.VISIBLE);
                // Hide Start.io banner since Unity loaded successfully
                android.view.View startioBanner = activity.findViewById(
                        com.awol.etechpro.R.id.startio_banner);
                if (startioBanner != null) {
                    startioBanner.setVisibility(android.view.View.GONE);
                }
            }
            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView,
                    BannerErrorInfo errorInfo) {
                Log.e(TAG, "Banner failed: " + errorInfo.errorMessage);
                container.setVisibility(android.view.View.GONE);
                // Show Start.io banner as fallback
                android.view.View startioBanner = activity.findViewById(
                        com.awol.etechpro.R.id.startio_banner);
                if (startioBanner != null) {
                    startioBanner.setVisibility(android.view.View.VISIBLE);
                }
            }
            @Override public void onBannerClick(BannerView bannerAdView) {}
            @Override public void onBannerLeftApplication(BannerView bannerAdView) {}
            @Override public void onBannerShown(BannerView bannerAdView) {}
        });
        container.addView(banner);
        banner.load();
        return banner;
    }

    // ── Reward Listener Interface ─────────────────────────────────

    public interface OnRewardEarnedListener {
        void onRewardEarned();
    }
}
