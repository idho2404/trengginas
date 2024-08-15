package com.trengginas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.trengginas.R;

public class TentangActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView loadingText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WebView webView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);

        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_tentang);
        loadingText = findViewById(R.id.loading_tentang);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                loadingText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                loadingText.setVisibility(View.GONE); // Hide the text
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE); // Show the progress bar
                loadingText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                loadingText.setVisibility(View.GONE); // Hide the text
            }
        });

        if (isConnected()) {
            String url = "https://trenggalekkab.bps.go.id/menu/1/informasi-umum.html";
            webView.loadUrl(url);
        } else {
            Intent intent = new Intent(TentangActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "TentangActivity");
            startActivity(intent);
            finish();
        }
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
