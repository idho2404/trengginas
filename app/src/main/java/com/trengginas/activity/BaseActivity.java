package com.trengginas.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trengginas.R;

public class BaseActivity extends Activity {

    private static final long LOGO_ANIMATION_DURATION = 500; // 1.5 seconds
    private static final long TRENGGINAS_ANIMATION_DURATION = 1000; // 2 seconds
    private static final long MOTTO_ANIMATION_DURATION = 2000; // 5 seconds
    private static final long PROGRESS_BAR_ANIMATION_DURATION = 4000; // 8 seconds

    private ProgressBar progressBar;
    private TextView progressPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Get references to the views
        final ImageView logoImageView = findViewById(R.id.logoImageView);
        final TextView appNameTextView = findViewById(R.id.appNameTextView);
        final TextView mottoTextView = findViewById(R.id.mottoTextView);
        final LinearLayout bottomLayout = findViewById(R.id.bottomLayout);

        progressBar = findViewById(R.id.progressBar);
        progressPercentage = findViewById(R.id.progressPercentage);

        // Initially, set visibility to invisible for all views except the background
        logoImageView.setVisibility(View.INVISIBLE);
        appNameTextView.setVisibility(View.INVISIBLE);
        mottoTextView.setVisibility(View.INVISIBLE);
        bottomLayout.setVisibility(View.INVISIBLE);

        // Animate logo appearance with zoom-in effect and start progress bar animation simultaneously
        logoImageView.postDelayed(() -> {
            logoImageView.setVisibility(View.VISIBLE);
            logoImageView.setScaleX(0.5f);
            logoImageView.setScaleY(0.5f);
            logoImageView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(LOGO_ANIMATION_DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            // Start updating progress bar simultaneously with the logo animation
            startProgressBar();
        }, 0); // Start immediately

        // Animate app name appearance with typewriter effect after logo animation
        appNameTextView.postDelayed(() -> {
            appNameTextView.setVisibility(View.VISIBLE);
            animateText(appNameTextView, "TRENGGINAS", TRENGGINAS_ANIMATION_DURATION / "TRENGGINAS".length());
        }, LOGO_ANIMATION_DURATION);

        // Animate motto appearance with typewriter effect after Trengginas animation
        mottoTextView.postDelayed(() -> {
            mottoTextView.setVisibility(View.VISIBLE);
            animateText(mottoTextView, "(Trenggalek dalam Informasi Statistik)", MOTTO_ANIMATION_DURATION / "(Trenggalek dalam Informasi Statistik)".length());
        }, LOGO_ANIMATION_DURATION + TRENGGINAS_ANIMATION_DURATION);

        // Animate bottom section appearance with fade-in
        bottomLayout.postDelayed(() -> {
            bottomLayout.setVisibility(View.VISIBLE);
            bottomLayout.animate()
                    .alpha(1f)
                    .setDuration(1000) // 1 second fade-in
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }, LOGO_ANIMATION_DURATION + TRENGGINAS_ANIMATION_DURATION + MOTTO_ANIMATION_DURATION);

        // Proceed with checking network connectivity after all animations
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isConnected()) {
                // Navigate to HomeActivity after all animations are done
                startActivity(new Intent(BaseActivity.this, HomeActivity.class));
                finish();
            } else {
                // Navigate to NetworkCheckActivity
                startActivity(new Intent(BaseActivity.this, NetworkCheckActivity.class));
                finish();
            }
        }, PROGRESS_BAR_ANIMATION_DURATION + 1000); // Add 1 second buffer
    }

    private void animateText(final TextView textView, final String text, long durationPerChar) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] index = {0};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index[0] <= text.length()) {
                    textView.setText(text.subSequence(0, index[0]));
                    index[0]++;
                    handler.postDelayed(this, durationPerChar); // Adjust the speed per character
                }
            }
        }, durationPerChar); // Initial delay before typing starts
    }

    private void startProgressBar() {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                final int progress = i;
                handler.post(() -> {
                    progressBar.setProgress(progress);
                    progressPercentage.setText(progress + "%");
                });
                try {
                    Thread.sleep(PROGRESS_BAR_ANIMATION_DURATION / 100); // Smooth animation
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }
}
