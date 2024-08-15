package com.trengginas.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trengginas.R;

public class InfografisActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private boolean isFirstLoad = true;
    private boolean isImagePreviewOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infografis);

        // Initialize UI components
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loading_text);

        // Set up WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Enable debugging
        WebView.setWebContentsDebuggingEnabled(true);

        // Set WebViewClient to manage loading behavior
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                loadingText.setVisibility(View.GONE); // Hide loading text

                // Inject JavaScript to add a close button overlay when an image is clicked
                view.evaluateJavascript(
                        "document.querySelectorAll('.filtr-item a').forEach(function(element) { " +
                                "   element.onclick = function(event) { " +
                                "       event.preventDefault(); " +
                                "       var overlay = document.createElement('div'); " +
                                "       overlay.style.position = 'fixed'; " +
                                "       overlay.style.top = '0'; " +
                                "       overlay.style.left = '0'; " +
                                "       overlay.style.width = '100%'; " +
                                "       overlay.style.height = '100%'; " +
                                "       overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.8)'; " +
                                "       overlay.style.zIndex = '9999'; " +
                                "       var img = document.createElement('img'); " +
                                "       img.src = element.href; " +
                                "       img.style.position = 'absolute'; " +
                                "       img.style.top = '50%'; " +
                                "       img.style.left = '50%'; " +
                                "       img.style.transform = 'translate(-50%, -50%)'; " +
                                "       img.style.maxWidth = '90%'; " +
                                "       img.style.maxHeight = '90%'; " +
                                "       overlay.appendChild(img); " +
                                "       var closeBtn = document.createElement('button'); " +
                                "       closeBtn.innerHTML = 'Close'; " +
                                "       closeBtn.style.position = 'absolute'; " +
                                "       closeBtn.style.top = '10px'; " +
                                "       closeBtn.style.right = '10px'; " +
                                "       closeBtn.style.backgroundColor = '#61A4D9'; " +
                                "       closeBtn.style.color = '#fff'; " +
                                "       closeBtn.style.border = 'none'; " +
                                "       closeBtn.style.padding = '10px 20px'; " +
                                "       closeBtn.style.cursor = 'pointer'; " +
                                "       closeBtn.style.zIndex = '10000'; " +
                                "       overlay.appendChild(closeBtn); " +
                                "       document.body.appendChild(overlay); " +
                                "       closeBtn.onclick = function() { " +
                                "           document.body.removeChild(overlay); " +
                                "           isImagePreviewOpen = false; " +
                                "       }; " +
                                "       isImagePreviewOpen = true; " +
                                "   }; " +
                                "}); ", null
                );

                // Inject JavaScript to style card headers and make them full-width flat boxes
                view.evaluateJavascript(
                        "document.querySelectorAll('.card-header').forEach(function(element) { " +
                                "element.style.backgroundColor = '#61A4D9'; " + // Background color
                                "element.style.color = '#FFF'; " + // Text color (white)
                                "element.style.fontFamily = 'poppins_bold'; " + // Font family
                                "element.style.fontWeight = 'bold'; " + // Make the text bold
                                "element.style.textTransform = 'uppercase'; " + // Make the text uppercase
                                "element.style.fontSize = '16px'; " + // Font size equivalent to 16sp
                                "element.style.height = '70px'; " +  // Increase height of card header
                                "element.style.display = 'flex'; " +  // Use flexbox for centering
                                "element.style.alignItems = 'center'; " + // Vertically center the text
                                "element.style.justifyContent = 'center'; " + // Horizontally center the text
                                "element.style.padding = '16px'; " + // Add padding equivalent to 16dp
                                "element.style.boxShadow = 'none'; " + // Remove the card shadow
                                "element.style.borderRadius = '0'; " + // Remove rounded corners
                                "element.style.margin = '0'; " + // Remove margin to make it full-width
                                "element.style.width = '100%'; " + // Ensure full width
                                "});", null
                );

                // Make sure the entire card or container is also full width without padding
                view.evaluateJavascript(
                        "document.querySelectorAll('.card').forEach(function(element) { " +
                                "element.style.width = '100%'; " + // Ensure full width of the card
                                "element.style.margin = '0'; " + // Center the card horizontally
                                "element.style.padding = '0'; " + // Remove padding
                                "element.style.border = 'none'; " + // Remove border
                                "element.style.borderRadius = '0'; " + // Remove rounded corners
                                "element.style.boxShadow = 'none'; " + // Remove shadow
                                "});", null
                );

                isFirstLoad = false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isFirstLoad) {
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Handle back navigation
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isImagePreviewOpen) {
                    // Close the image preview overlay
                    webView.evaluateJavascript(
                            "document.body.querySelectorAll('div').forEach(function(overlay) { " +
                                    "if (overlay.style.position === 'fixed') { " +
                                    "document.body.removeChild(overlay); " +
                                    "isImagePreviewOpen = false;" +
                                    "}" +
                                    "});",
                            null
                    );
                } else {
                    // Navigate back to HomeActivity
                    Intent intent = new Intent(InfografisActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                }
            }
        });

        // Load the infographics URL if connected
        if (isConnected()) {
            webView.loadUrl("https://bpstrenggalek.com/trengginas/slide/view.php");
        } else {
            // Navigate to another activity when no network connection
            Intent intent = new Intent(InfografisActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "InfografisActivity");
            startActivity(intent);
            finish();
        }
    }

    // Check for network connectivity
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
