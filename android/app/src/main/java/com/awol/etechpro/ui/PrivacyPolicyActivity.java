package com.awol.etechpro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.awol.etechpro.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Privacy Policy");
        }

        // Clickable third-party links
        setLink(R.id.link_google_play,
                "https://policies.google.com/privacy");
        setLink(R.id.link_admob,
                "https://support.google.com/admob/answer/6128543");
        setLink(R.id.link_fcm,
                "https://firebase.google.com/support/privacy");
        setLink(R.id.link_firebase,
                "https://firebase.google.com/support/privacy");
        setLink(R.id.link_unity,
                "https://unity.com/legal/privacy-policy");
        setLink(R.id.link_onesignal,
                "https://onesignal.com/privacy_policy");
        setLink(R.id.link_applovin,
                "https://www.applovin.com/privacy/");
        setLink(R.id.link_startapp,
                "https://www.startapp.com/policy/privacy-policy/");
    }

    private void setLink(int viewId, String url) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
        }
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
