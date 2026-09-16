package com.awol.etechpro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.awol.etechpro.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ActionBar ab = getSupportActionBar();
        if (ab != null) { ab.setDisplayHomeAsUpEnabled(true); ab.setTitle("Contact Us"); }

        Button btnSendEmail = findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:etechsupport2@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Etech Pro Support Request");
            startActivity(Intent.createChooser(intent, "Send email"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
