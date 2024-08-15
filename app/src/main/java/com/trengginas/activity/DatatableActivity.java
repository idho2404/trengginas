package com.trengginas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trengginas.R;
import com.trengginas.adapter.DatatableAdapter;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;
import com.trengginas.model.Datatable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DatatableActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatatableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DatatableAdapter(this, new ArrayList<>()); // Initialize adapter with an empty list
        recyclerView.setAdapter(adapter);

        if (isConnected()) {
            loadItems();
        } else {
            Intent intent = new Intent(DatatableActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DatatableActivity");
            startActivity(intent);
            finish();
        }
    }

    private void loadItems() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT * FROM 0007_list_datatabel";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Datatable> datatables = parseResponse(response.body());
                    adapter.updateData(datatables);
                } else {
                    Toast.makeText(DatatableActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("DatatableActivity", "Network error", t);
                Toast.makeText(DatatableActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Datatable> parseResponse(String response) {
        List<Datatable> datatables = new ArrayList<>();
        String[] rows = response.split("\n");
        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] columns = row.split(",");
            if (columns.length >= 5) { // Ensure there are enough columns
                String icon = columns[1];
                String judul = columns[2];
                String subjudul = columns[3];
                String nmscreen = columns[4];
                if (nmscreen != null) { // Check if nmscreen is not null
                    Datatable datatable = new Datatable(judul, subjudul, nmscreen, icon);
                    datatables.add(datatable);
                }
            }
        }
        return datatables;
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
}
