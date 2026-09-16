package com.awol.etechpro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.awol.etechpro.R;
import com.bumptech.glide.Glide;

import java.io.File;

public class ClearCacheActivity extends AppCompatActivity {

    private TextView tvCacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Clear Cache");
        }

        tvCacheSize = findViewById(R.id.tv_cache_size_detail);
        tvCacheSize.setText("Cache size: " + formatSize(getCacheSize()));

        // Clear Cache button
        Button btnClear = findViewById(R.id.btn_do_clear_cache);
        btnClear.setOnClickListener(v -> {
            deleteCache();
            new Thread(() -> Glide.get(this).clearDiskCache()).start();
            Glide.get(this).clearMemory();
            tvCacheSize.setText("Cache size: 0 KB");
            Toast.makeText(this, "Cache cleared successfully!", Toast.LENGTH_SHORT).show();
        });

        // Cancel button
        Button btnCancel = findViewById(R.id.btn_cancel_cache);
        btnCancel.setOnClickListener(v -> finish());
    }

    private long getCacheSize() {
        long size = 0;
        try {
            size += getDirSize(getCacheDir());
            if (getExternalCacheDir() != null)
                size += getDirSize(getExternalCacheDir());
        } catch (Exception e) { /* ignore */ }
        return size;
    }

    private long getDirSize(File dir) {
        if (dir == null) return 0;
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                size += file.isDirectory() ? getDirSize(file) : file.length();
            }
        }
        return size;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        else return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void deleteCache() {
        try {
            deleteDir(getCacheDir());
            if (getExternalCacheDir() != null) deleteDir(getExternalCacheDir());
        } catch (Exception e) { /* ignore */ }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) deleteDir(child);
            }
        }
        return dir != null && dir.delete();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
