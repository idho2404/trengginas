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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trengginas.R;
import com.trengginas.activity.NetworkCheckActivity;

public class KemiskinanFragment extends Fragment {

    private CustomWebView webView;
    private Button buttonTabel, buttonGrafik;
    private String currentUrl = "";

    public KemiskinanFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kemiskinan, container, false);

        webView = view.findViewById(R.id.web_view);
        buttonTabel = view.findViewById(R.id.button_tabel);
        buttonGrafik = view.findViewById(R.id.button_grafik);
        if(isConnected()) {
            setupWebView();
            setupEventListeners();
        }else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "KemiskinanFragment");
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

        loadWebViewContent("tabel");
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

    private void setupEventListeners() {
        buttonTabel.setOnClickListener(v -> loadWebViewContent("tabel"));
        buttonGrafik.setOnClickListener(v -> loadWebViewContent("grafik"));
    }

    private void loadWebViewContent(String type) {
        String url = "";

        if (type.equals("tabel")) {
            url = "https://bpstrenggalek.com/trengginas/03_kemiskinan.php";
        } else {
            url = "https://bpstrenggalek.com/trengginas/chart/chart_kemiskinan.php";
        }

        currentUrl = url;
        webView.loadUrl(url);
    }
}
