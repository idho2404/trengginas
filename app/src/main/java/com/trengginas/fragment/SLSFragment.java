package com.trengginas.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class SLSFragment extends Fragment {

    private Spinner spinnerKec, spinnerDesa;
    private CustomWebView webView;  // Using CustomWebView

    private List<String> listKecDisplay = new ArrayList<>();
    private List<String> listDesaDisplay = new ArrayList<>();

    public SLSFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sls, container, false);

        spinnerKec = view.findViewById(R.id.spinner_kec);
        spinnerDesa = view.findViewById(R.id.spinner_desa);
        webView = view.findViewById(R.id.web_view);

        if(isConnected()) {
            setupWebView();  // Set up WebView
            loadKecamatanData();
        }else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "GiniRasioFragment");
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        spinnerKec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedKec = listKecDisplay.get(position);
                loadDesaData(selectedKec);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDesa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedKec = listKecDisplay.get(spinnerKec.getSelectedItemPosition());
                String selectedDesa = listDesaDisplay.get(position);
                loadSLSData(selectedKec, selectedDesa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            public void onPageFinished(android.webkit.WebView view, String url) {
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

    private void loadKecamatanData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT kecamatan FROM 0001_kec";

        Call<String> call = api.getKecamatanList("trengginas", query);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listKecDisplay = parseResponse(response.body());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listKecDisplay);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerKec.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch kecamatan data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDesaData(String selectedKec) {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT kd_nm_desa FROM 0002_desa WHERE kd_nm_kec='" + selectedKec + "'";

        Call<String> call = api.getDesaList("trengginas", query);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDesaDisplay = parseResponse(response.body());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listDesaDisplay);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDesa.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch desa data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSLSData(String selectedKec, String selectedDesa) {
        String baseUrl = "https://bpstrenggalek.com/trengginas/maswil.php?jdl=3&kec=";
        String url = baseUrl + selectedKec + "&desa=" + selectedDesa;
        webView.loadUrl(url);
    }

    private List<String> parseResponse(String response) {
        List<String> parsedData = new ArrayList<>();
        String[] rows = response.split("\n");
        for (int i = 1; i < rows.length; i++) { // Start from index 1 to skip the first element
            parsedData.add(rows[i].trim());
        }
        return parsedData;
    }
}
