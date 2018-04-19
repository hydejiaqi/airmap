package com.example.jjiang.myapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.airmap.airmapsdk.AirMapException;
import com.airmap.airmapsdk.AirMapLog;
import com.airmap.airmapsdk.networking.callbacks.AirMapCallback;
import com.airmap.airmapsdk.networking.services.AirMap;
import com.example.jjiang.myapplication.R;


import java.util.UUID;

public class AnonymousLoginDemoActivity extends AppCompatActivity {

    private static final String TAG = "AnonymousLoginActivity";

    private Toolbar toolbar;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anon_login);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusTextView = findViewById(R.id.status_text_view);

        // check if user is already logged in
        if (!TextUtils.isEmpty(AirMap.getUserId())) {
            statusTextView.setText("Already logged in as:\n\n" + AirMap.getUserId() + "\n\nwith ability to create flights, receive traffic and send telemetry.");

        // if not use Anonymous Login
        } else {
            // Any unique identifier from the developer for their user (UUID, username, email)
            String userId = UUID.randomUUID().toString();

            AirMap.performAnonymousLogin(userId, new AirMapCallback<Void>() {
                @Override
                public void onSuccess(Void response) {
                    AirMapLog.v(TAG, "Token is: " + AirMap.getAuthToken());
                    statusTextView.setText("Logged in as:\n\n" + AirMap.getUserId() + "\n\nNow able to create flights, receive traffic and send telemetry.");
                }

                @Override
                public void onError(AirMapException e) {
                    AirMapLog.e(TAG, e.getDetailedMessage(), e);
                    statusTextView.setText("Anonymous Login failed. Enable AirMapLog for more information.");
                }
            });
        }
    }
}
