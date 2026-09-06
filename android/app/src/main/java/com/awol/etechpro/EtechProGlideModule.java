package com.awol.etechpro;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class EtechProGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Limit disk cache to 50MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 50 * 1024 * 1024));
        // Limit memory cache to 20MB
        builder.setMemoryCache(new LruResourceCache(20 * 1024 * 1024));
    }
}
