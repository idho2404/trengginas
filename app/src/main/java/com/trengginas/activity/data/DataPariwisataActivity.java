package com.trengginas.activity.data;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.trengginas.R;
import com.trengginas.activity.NetworkCheckActivity;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataPariwisataActivity extends AppCompatActivity {

    private Spinner spinnerTabel;
    private WebView webView;
    private String selectedNmtbl = ""; // Holds the nmtbl value
    private final List<String> judulList = new ArrayList<>();
    private final Map<String, String> judulToNmtblMap = new HashMap<>(); // Map to hold judultbl to nmtbl mapping
    private ArrayAdapter<String> judulAdapter;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datapariwisata);

        iconImageView = findViewById(R.id.icon_judul);
        spinnerTabel = findViewById(R.id.spinner_tabel);
        webView = findViewById(R.id.web_view);
        if(isConnected()) {
            setupWebView();
            setupTableSpinner();
            fetchIconUrl();
        } else {
            Intent intent = new Intent(DataPariwisataActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataGeografisActivity");
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

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJavaScript();
            }
        });
    }

    private void injectJavaScript() {
        String js = "javascript:(function() {" +
                "var headers = document.getElementsByTagName('th');" +
                "for (var i = 0; i < headers.length; i++) {" +
                "headers[i].style.backgroundColor = '#61A4D9';" +
                "headers[i].style.color = 'white';" +
                "}" +
                "var rows = document.getElementsByTagName('tr');" +
                "for (var j = 0; j < rows.length; j++) {" +
                "var cells = rows[j].getElementsByTagName('td');" +
                "for (var k = 0; k < cells.length; k++) {" +
                "cells[k].style.color = 'black';" +
                "if (j % 2 == 0) {" +
                "cells[k].style.backgroundColor = '#DFF1FF';" +
                "} else {" +
                "cells[k].style.backgroundColor = 'white';" +
                "}" +
                "}" +
                "}" +
                "})()";
        webView.evaluateJavascript(js, null);
    }

    private void setupTableSpinner() {
        judulAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, judulList);
        judulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTabel.setAdapter(judulAdapter);
        loadJudulData();

        spinnerTabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJudul = judulList.get(position);
                selectedNmtbl = judulToNmtblMap.get(selectedJudul); // Get the corresponding nmtbl value
                loadWebViewContent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadJudulData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getJudulList("trengginas", "SELECT nmtbl, judultbl FROM 0004_judul WHERE jnstbl=9");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    judulList.clear();
                    judulToNmtblMap.clear();
                    String[] judulArray = response.body().split("\n");
                    for (int i = 1; i < judulArray.length; i++) {
                        String[] parts = judulArray[i].split(",");
                        if (parts.length == 2) {
                            String nmtbl = parts[0].trim();
                            String judultbl = parts[1].trim();
                            judulList.add(judultbl);
                            judulToNmtblMap.put(judultbl, nmtbl);
                        }
                    }
                    judulAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void loadWebViewContent() {
        String url = "https://bpstrenggalek.com/trengginas/07_wisata.php?jdl=" + selectedNmtbl;
        if(isConnected()) {
            webView.loadUrl(url);
        } else {
            Intent intent = new Intent(DataPariwisataActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataPariwisataActivity");
            startActivity(intent);
            finish();
        }
    }

    private void fetchIconUrl() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT REPLACE(icon, 'jokosanbps', 'bpstrenggalek') FROM 0007_list_datatabel WHERE id=9";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String[] rows = response.body().split("\n");
                    if (rows.length > 1) {
                        String iconUrl = rows[1].trim();
                        loadIcon(iconUrl);
                    } else {
                        showToast("Icon URL not found");
                    }
                } else {
                    showToast("Failed to fetch icon");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void loadIcon(String iconUrl) {
        if (!iconUrl.isEmpty()) {
            Glide.with(this)
                    .load(iconUrl)
                    .into(iconImageView);
        } else {
            showToast("Gambar tidak ditemukan");
        }
    }

    private void showToast(String message) {
        Toast.makeText(DataPariwisataActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
