package com.awol.etechpro.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.awol.etechpro.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("About");
        }

        // Show app version dynamically
        TextView tvVersion = findViewById(R.id.tv_version);
        try {
            PackageInfo pInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText("Version 1.0.0");
        }

        // Rate Us — opens Play Store
        LinearLayout btnRate = findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(v -> {
            String packageName = getPackageName();
            try {
                // Try to open Play Store app first
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                // Fall back to browser
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        });

        // Share with Friends
        LinearLayout btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out Etech Pro!");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey! I'm using Etech Pro — a great app for tech tips, tutorials, and app recommendations.\n\n"
                    + "Download it here: https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Etech Pro via"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
