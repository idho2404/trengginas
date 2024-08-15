package com.trengginas.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.trengginas.R;

public class PublikasiActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private String downloadUrl;
    private String downloadFileName;
    private TextView loadingText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publikasi);

        WebView webView;
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_publikasi);  // Initialize the progress bar
        loadingText = findViewById(R.id.loading_publikasi);  // Initialize the loading text

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

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
                loadingText.setVisibility(View.VISIBLE); // Show the loading text
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                loadingText.setVisibility(View.GONE);  // Hide the loading text
            }
        });

        if (isConnected()) {
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://trenggalekkab.bps.go.id/publication.html");
        } else {
            Intent intent = new Intent(PublikasiActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "PublikasiActivity");
            startActivity(intent);
            finish();
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadUrl = url;
                downloadFileName = contentDisposition.replace("inline; filename=", "").replace("\"", "");
                if (ContextCompat.checkSelfPermission(PublikasiActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showDownloadDialog();
                } else {
                    ActivityCompat.requestPermissions(PublikasiActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                }
            }
        });
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublikasiActivity.this);
        builder.setTitle("Konfirmasi")
                .setMessage("Nama File yang akan di Download:\n\n" + downloadFileName)
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    private void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle("Downloading " + downloadFileName);
        request.setDescription("Download in progress");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(PublikasiActivity.this, "Download started...", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDownloadDialog();
            } else {
                Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
