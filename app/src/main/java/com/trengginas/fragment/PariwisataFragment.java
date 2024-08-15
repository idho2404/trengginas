package com.trengginas.fragment;

import static android.content.Context.*;

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
import android.webkit.WebView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PariwisataFragment extends Fragment {

    private Spinner spinnerJudul;
    private WebView webView;
    private List<String> judulList = new ArrayList<>();
    private ArrayAdapter<String> judulAdapter;
    private Map<String, String> judulToNmtblMap = new HashMap<>();
    private String selectedNmtbl = "";

    public PariwisataFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pariwisata, container, false);

        spinnerJudul = view.findViewById(R.id.spinner_judul);
        webView = view.findViewById(R.id.web_view);

        if(isConnected()) {
            setupJudulSpinner();
        } else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "PariwisataFragment");
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());

        spinnerJudul.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJudul = judulList.get(position);
                selectedNmtbl = judulToNmtblMap.get(selectedJudul);
                loadWebViewContent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void setupJudulSpinner() {
        judulAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, judulList);
        judulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJudul.setAdapter(judulAdapter);
        loadJudulData();
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
                // Handle failure
            }
        });
    }

    private void loadWebViewContent() {
        String url = "https://bpstrenggalek.com/trengginas/07_wisata.php?jdl=" + selectedNmtbl;
        if(isConnected()) {
            webView.loadUrl(url);
        }else {
            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
            intent.putExtra("origin", "PariwisataFragment");
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}
