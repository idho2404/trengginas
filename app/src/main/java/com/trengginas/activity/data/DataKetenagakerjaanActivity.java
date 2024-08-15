package com.trengginas.activity.data;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class DataKetenagakerjaanActivity extends AppCompatActivity {

    private Spinner spinnerJudul, spinnerTahun, spinnerSatuan;
    private WebView webView;
    private Button buttonTabel, buttonGrafik;
    private final List<String> judulList = new ArrayList<>();
    private final List<String> tahunList = new ArrayList<>();
    private ArrayAdapter<String> judulAdapter;
    private ArrayAdapter<String> tahunAdapter;
    private String selectedNmtbl = "";
    private String currentUrl = "";
    private final Map<String, String> judulToNmtblMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataketenagakerjaan);

        spinnerJudul = findViewById(R.id.spinner_judul);
        spinnerTahun = findViewById(R.id.spinner_tahun);
        spinnerSatuan = findViewById(R.id.spinner_satuan);
        webView = findViewById(R.id.web_view);
        buttonTabel = findViewById(R.id.button_tabel);
        buttonGrafik = findViewById(R.id.button_grafik);

        if(isConnected()) {
            setupJudulSpinner();
            setupTahunSpinner();
            setupSatuanSpinner();
            setupWebView();
            setupEventListeners();
        } else {
            Intent intent = new Intent(DataKetenagakerjaanActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataKetenagakerjaanActivity");
            startActivity(intent);
            finish();
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);

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

    private void setupJudulSpinner() {
        judulAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, judulList);
        judulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJudul.setAdapter(judulAdapter);
        loadJudulData();
    }

    private void setupTahunSpinner() {
        tahunAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tahunList);
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);
        loadTahunData();
    }

    private void setupSatuanSpinner() {
        List<String> satuan = new ArrayList<>();
        satuan.add("Orang");
        satuan.add("Persen");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, satuan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSatuan.setAdapter(adapter);
    }

    private void setupEventListeners() {
        spinnerJudul.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJudul = judulList.get(position);
                selectedNmtbl = judulToNmtblMap.get(selectedJudul);
                loadWebViewContent("tabel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWebViewContent("tabel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWebViewContent("tabel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonTabel.setOnClickListener(v -> loadWebViewContent("tabel"));
        buttonGrafik.setOnClickListener(v -> loadWebViewContent("grafik"));
    }

    private void loadJudulData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getJudulList("trengginas", "SELECT nmtbl, judultbl FROM 0004_judul WHERE jnstbl=6");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    judulList.clear();
                    judulToNmtblMap.clear();
                    String[] judulArray = response.body().split("\n");
                    for (int i = 1; i < judulArray.length; i++) {
                        String[] columns = judulArray[i].split(",");
                        if (columns.length >= 2) {
                            String nmtbl = columns[0].trim();
                            String judultbl = columns[1].trim();
                            judulList.add(judultbl);
                            judulToNmtblMap.put(judultbl, nmtbl);
                        }
                    }
                    judulAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private void loadTahunData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getTahunList("trengginas", "SELECT tahun FROM 0005_tahun WHERE naker=1");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tahunList.clear();
                    String[] tahunArray = response.body().split("\n");
                    for (int i = 1; i < tahunArray.length; i++) {
                        tahunList.add(tahunArray[i].trim());
                    }
                    tahunAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private void loadWebViewContent(String type) {
        String selectedTahun = (String) spinnerTahun.getSelectedItem();
        String selectedSatuan = (String) spinnerSatuan.getSelectedItem();
        String satuanValue = selectedSatuan.equals("Orang") ? "1" : "2";
        String url = "";

        if (selectedNmtbl == null || selectedNmtbl.isEmpty() || selectedTahun == null || selectedTahun.isEmpty()) {
            Log.e("LoadWebViewContent", "Required parameters are missing");
            return;
        }

        if (type.equals("tabel")) {
            url = "https://bpstrenggalek.com/trengginas/06_naker.php?in=" + selectedNmtbl + "&th=" + selectedTahun + "&sat=" + satuanValue;
        } else {
            url = "https://bpstrenggalek.com/trengginas/chart/grafiknaker.php?in=" + selectedNmtbl + "&th=" + selectedTahun + "&sat=" + satuanValue;
        }

        if(isConnected()) {
            currentUrl = url;
            webView.loadUrl(url);
        } else {
            Intent intent = new Intent(DataKetenagakerjaanActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataKetenagakerjaanActivity");
            startActivity(intent);
            finish();
        }
    }
}
