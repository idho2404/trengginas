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
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

public class IpmFragment extends Fragment {

    private Spinner spinnerTahun;
    private CustomWebView webView;
    private List<String> tahunList = new ArrayList<>();
    private ArrayAdapter<String> tahunAdapter;

    public IpmFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipm, container, false);

        spinnerTahun = view.findViewById(R.id.spinner_tahun);
        webView = view.findViewById(R.id.web_view);
        if(isConnected()) {
            setupTahunSpinner();
            setupWebView();
            setupEventListeners();
        }else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "IpmFragment");
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
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

    private void setupTahunSpinner() {
        tahunAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tahunList);
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);
        loadTahunData();
    }

    private void loadTahunData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getTahunList("trengginas", "SELECT tahun FROM 0005_tahun WHERE pdrb=1");
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
                // Handle failure
            }
        });
    }

    private void setupEventListeners() {
        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWebViewContent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadWebViewContent() {
        String selectedTahun = (String) spinnerTahun.getSelectedItem();
        String url = "https://bpstrenggalek.com/trengginas/03_ipm.php?th=" + selectedTahun;
        webView.loadUrl(url);
    }
}
