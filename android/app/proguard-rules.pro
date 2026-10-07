# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**

# Gson
-keep class com.google.gson.** { *; }
-keep class com.awol.etechpro.model.** { *; }
-keepattributes *Annotation*

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Google Mobile Ads (AdMob) — REQUIRED: without these rules, ads fail silently in release builds
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
-keep class com.google.android.gms.common.** { *; }

# App Open Ad
-keep class com.google.android.gms.ads.appopen.** { *; }

# Rewarded Ad
-keep class com.google.android.gms.ads.rewarded.** { *; }

# Interstitial Ad
-keep class com.google.android.gms.ads.interstitial.** { *; }

# AdMob initialization
-keep class com.google.android.gms.ads.initialization.** { *; }
-keep class com.google.android.gms.ads.MobileAds { *; }

# Keep app model classes
-keep class com.awol.etechpro.** { *; }
