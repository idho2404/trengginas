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

public class DataPertanianActivity extends AppCompatActivity {

    private Spinner spinnerTabel, spinnerTahun;
    private WebView webView;
    private String selectedTableId = ""; // Untuk menyimpan nmtbl

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datapertanian);

        spinnerTabel = findViewById(R.id.spinner_tabel);
        spinnerTahun = findViewById(R.id.spinner_tahun);
        webView = findViewById(R.id.web_view);

        if(isConnected()) {
            setupWebView();
            setupTableSpinner();
            setupYearSpinner();
            fetchIconUrl();
        } else {
            Intent intent = new Intent(DataPertanianActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataPertanianActivity");
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
                view.evaluateJavascript(js, null);
            }
        });
    }

    private void setupTableSpinner() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT judultbl, nmtbl FROM 0004_judul WHERE jnstbl = '5'";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> tables = new ArrayList<>();
                    final List<String> tableIds = new ArrayList<>();

                    String[] rows = response.body().split("\n");
                    for (int i = 1; i < rows.length; i++) {
                        String[] columns = rows[i].split(",");
                        if (columns.length >= 2) {
                            tables.add(columns[0].trim()); // judultbl
                            tableIds.add(columns[1].trim()); // nmtbl
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DataPertanianActivity.this, android.R.layout.simple_spinner_item, tables);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTabel.setAdapter(adapter);
                    spinnerTabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedTableId = tableIds.get(position); // Simpan nilai nmtbl
                            if(isConnected()) {
                                loadData();
                            } else {
                                Intent intent = new Intent(DataPertanianActivity.this, NetworkCheckActivity.class);
                                intent.putExtra("origin", "DataPertanianActivity");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    Toast.makeText(DataPertanianActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DataPertanianActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupYearSpinner() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT tahun FROM 0005_tahun WHERE pertanian=1";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> years = new ArrayList<>();
                    String[] rows = response.body().split("\n");
                    for (int i = 1; i < rows.length; i++) {
                        years.add(rows[i].trim());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DataPertanianActivity.this, android.R.layout.simple_spinner_item, years);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTahun.setAdapter(adapter);
                    spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(isConnected()) {
                                loadData();
                            } else {
                                Intent intent = new Intent(DataPertanianActivity.this, NetworkCheckActivity.class);
                                intent.putExtra("origin", "DataPertanianActivity");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    Toast.makeText(DataPertanianActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DataPertanianActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchIconUrl() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT REPLACE(icon, 'jokosanbps', 'bpstrenggalek') FROM 0007_list_datatabel WHERE id=3";
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
        String selectedYear = (String) spinnerTahun.getSelectedItem();
        if (selectedTableId.isEmpty() || selectedYear == null || selectedYear.isEmpty()) {
            return;
        }
        if(isConnected()) {
            String url = "https://bpstrenggalek.com/trengginas/05_pertanian.php?jdl=" + selectedTableId + "&th=" + selectedYear;
            webView.loadUrl(url);
        }else {
            Intent intent = new Intent(DataPertanianActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataPertanianActivity");
            startActivity(intent);
            finish();
        }
    }

    private void showToast(String message) {
        Toast.makeText(DataPertanianActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
