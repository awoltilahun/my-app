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

# Unity Ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-dontwarn com.unity3d.ads.**
-dontwarn com.unity3d.services.**

# Start.io (formerly StartApp)
-keep class com.startapp.** { *; }
-keep class com.truenet.** { *; }
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-dontwarn org.jetbrains.annotations.**

# Meta Audience Network
-keep class com.facebook.** { *; }
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.**

# LevelPlay (ironSource) SDK
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class com.google.android.gms.appset.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.ironsource.adapters.** { *; }
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *; }
-dontwarn com.iab.omid.**
-keep class com.iab.omid.** { *; }
-keepattributes JavascriptInterface
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers class com.facebook.ads.internal.AdSdkVersion { static *; }
-keepclassmembers class com.facebook.ads.internal.settings.AdSdkVersion { static *; }
-keepclassmembers class com.facebook.ads.BuildConfig { static *; }
-keep public interface com.facebook.ads** { *; }
-dontwarn com.facebook.ads.internal.**
-keeppackagenames com.facebook.*
-keep public class com.facebook.ads.** { *; }
