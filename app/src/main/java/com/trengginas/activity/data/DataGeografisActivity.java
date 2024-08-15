package com.trengginas.activity.data;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.trengginas.R;
import com.trengginas.activity.NetworkCheckActivity;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataGeografisActivity extends AppCompatActivity {

    private Spinner spinnerTabel, spinnerTahun;
    private WebView webView;

    private String selectedTable = "";
    private String selectedYear = "";
    private String selectedTableId = ""; // Holds the nmtbl value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datageografi);

        spinnerTabel = findViewById(R.id.spinner_tabel);
        spinnerTahun = findViewById(R.id.spinner_tahun);
        webView = findViewById(R.id.web_view);

        if (isConnected()) {
            setupWebView();
            setupTableSpinner();
            setupYearSpinner();
            fetchIconUrl();
        } else {
            Intent intent = new Intent(DataGeografisActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataGeografisActivity");
            startActivity(intent);
            finish();
        }
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false); // Hide zoom controls
        webView.getSettings().setSupportZoom(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJavaScript();
            }
        });
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
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT judultbl, nmtbl FROM 0004_judul WHERE jnstbl = '2'";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> tables = new ArrayList<>();
                    final List<String> tableIds = new ArrayList<>();

                    String[] rows = response.body().split("\n");
                    for (int i = 1; i < rows.length; i++) { // Skip the first row
                        String[] columns = rows[i].split(",");
                        if (columns.length >= 2) {
                            tables.add(columns[0].trim()); // judultbl
                            tableIds.add(columns[1].trim()); // nmtbl
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DataGeografisActivity.this, android.R.layout.simple_spinner_item, tables);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTabel.setAdapter(adapter);

                    spinnerTabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedTable = tables.get(position);
                            selectedTableId = tableIds.get(position); // Store the nmtbl value
                            if (isConnected()){
                                loadData();
                            } else {
                                Intent intent = new Intent(DataGeografisActivity.this, NetworkCheckActivity.class);
                                intent.putExtra("origin", "DataGeografisActivity");
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    showToast("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void setupYearSpinner() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT tahun FROM 0005_tahun WHERE geo=1";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> years = new ArrayList<>();
                    String[] rows = response.body().split("\n");
                    for (int i = 1; i < rows.length; i++) { // Skip the first row
                        years.add(rows[i].trim());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DataGeografisActivity.this, android.R.layout.simple_spinner_item, years);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTahun.setAdapter(adapter);

                    spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedYear = years.get(position);
                            loadData();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    showToast("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchIconUrl() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT REPLACE(icon, 'jokosanbps', 'bpstrenggalek') FROM 0007_list_datatabel WHERE id=1";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String[] rows = response.body().split("\n");
                    if (rows.length > 1) { // Check if there's more than one line
                        String iconUrl = rows[1].trim(); // Get the second line
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
        ImageView iconImageView = findViewById(R.id.icon_judul);
        if (!iconUrl.isEmpty()) {
            Glide.with(this)
                    .load(iconUrl)
                    .into(iconImageView);
        } else {
            showToast("Gambar tidak ditemukan");
        }
    }

    private void loadData() {

        if (selectedTable.isEmpty() || selectedYear.isEmpty() || selectedTableId.isEmpty()) {
            return;
        }

        String url = "https://bpstrenggalek.com/trengginas/01_geografis.php?jdl=" + selectedTableId + "&th=" + selectedYear;
        webView.loadUrl(url);
    }

    private void showToast(String message) {
        Toast.makeText(DataGeografisActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
