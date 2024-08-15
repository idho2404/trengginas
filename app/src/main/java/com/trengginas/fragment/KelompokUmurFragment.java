package com.trengginas.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trengginas.R;
import com.trengginas.activity.NetworkCheckActivity;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class KelompokUmurFragment extends Fragment {

    private Spinner spinnerLevel, spinnerKecamatan, spinnerTahun;
    private WebView webView;
    private Button buttonTabel, buttonGrafik;
    private List<String> kecamatanList = new ArrayList<>();
    private ArrayAdapter<String> kecamatanAdapter;
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private View kecamatanLayout;

    public KelompokUmurFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kelompok_umur, container, false);

        spinnerLevel = view.findViewById(R.id.spinner_level);
        spinnerKecamatan = view.findViewById(R.id.spinner_kecamatan);
        spinnerTahun = view.findViewById(R.id.spinner_tahun);
        kecamatanLayout = view.findViewById(R.id.layout_kecamatan);
        webView = view.findViewById(R.id.web_view);
        buttonTabel = view.findViewById(R.id.button_tabel);
        buttonGrafik = view.findViewById(R.id.button_grafik);

        if(isConnected()) {
            setupLevelSpinner();
            setupTahunSpinner();
            setupKecamatanSpinner();
            setupWebView();
            setupEventListeners();
        } else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "KelompokUmurFragment");
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

    private void setupLevelSpinner() {
        List<String> levels = new ArrayList<>();
        levels.add("Kabupaten");
        levels.add("Kecamatan");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
    }

    private void setupTahunSpinner() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT tahun FROM 0005_tahun WHERE sp_kelum=1";
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTahun.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private void setupKecamatanSpinner() {
        kecamatanAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, kecamatanList);
        kecamatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKecamatan.setAdapter(kecamatanAdapter);
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
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLevel = parent.getItemAtPosition(position).toString();
                if (selectedLevel.equals("Kabupaten")) {
                    kecamatanLayout.setVisibility(View.GONE);
                    loadWebViewContent("tabel");
                } else if (selectedLevel.equals("Kecamatan")) {
                    kecamatanLayout.setVisibility(View.VISIBLE);
                    loadKecamatanData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWebViewContent("tabel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWebViewContent("tabel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonTabel.setOnClickListener(v -> loadWebViewContent("tabel"));
        buttonGrafik.setOnClickListener(v -> loadWebViewContent("grafik"));
    }

    private void loadKecamatanData() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getKecamatanList("trengginas", "SELECT kecamatan FROM 0001_kec");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kecamatanList.clear();
                    String[] kecamatanArray = response.body().split("\n");
                    for (int i = 1; i < kecamatanArray.length; i++) {
                        kecamatanList.add(kecamatanArray[i].trim());
                    }
                    kecamatanAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private void loadWebViewContent(String type) {
        String selectedLevel = (spinnerLevel.getSelectedItem() != null) ? spinnerLevel.getSelectedItem().toString() : null;
        String selectedTahun = (spinnerTahun.getSelectedItem() != null) ? spinnerTahun.getSelectedItem().toString() : null;

        if (selectedLevel == null || selectedTahun == null) {
            Log.e("LoadWebViewContent", "Selected level or tahun is null");
            return;
        }

        String url = "";
        if (selectedLevel.equals("Kabupaten")) {
            if (type.equals("tabel")) {
                url = "https://bpstrenggalek.com/trengginas/03_pendudukpyk_kab.php?th=" + selectedTahun;
            } else {
                url = "https://bpstrenggalek.com/trengginas/chart/chart_pddk_kelum_kab.php?th=" + selectedTahun;
            }
        } else if (selectedLevel.equals("Kecamatan")) {
            String selectedKecamatan = spinnerKecamatan.getSelectedItem() != null ? spinnerKecamatan.getSelectedItem().toString() : "";
            if (type.equals("tabel")) {
                url = "https://bpstrenggalek.com/trengginas/03_penduduksp_kec_kelum.php?kec=" + selectedKecamatan + "&th=" + selectedTahun;
            } else {
                url = "https://bpstrenggalek.com/trengginas/chart/chart_pddk_kelum_kec.php?kec=" + selectedKecamatan + "&th=" + selectedTahun;
            }
        }

        webView.loadUrl(url);
    }
}
