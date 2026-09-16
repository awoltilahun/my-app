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
        // Limit disk cache to 20MB (reduced from 50MB)
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 20 * 1024 * 1024));
        // Limit memory cache to 8MB (reduced from 20MB)
        builder.setMemoryCache(new LruResourceCache(8 * 1024 * 1024));
    }
}
