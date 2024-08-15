package com.trengginas.activity.data;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trengginas.R;
import com.trengginas.activity.NetworkCheckActivity;
import com.trengginas.adapter.ViewPagerKependudukanAdapter;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;
import com.trengginas.fragment.JenisKelaminFragment;
import com.trengginas.fragment.KelompokUmurFragment;
import com.trengginas.fragment.KomposisiFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataKependudukanActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datakependudukan);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ViewPagerKependudukanAdapter adapter = new ViewPagerKependudukanAdapter(this);
        adapter.addFragment(new JenisKelaminFragment(), "Jenis Kelamin");
        adapter.addFragment(new KelompokUmurFragment(), "Kelompok Umur");
        adapter.addFragment(new KomposisiFragment(), "Komposisi");

        if(isConnected()) {
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Jenis Kelamin");
                        break;
                    case 1:
                        tab.setText("Kelompok Umur");
                        break;
                    case 2:
                        tab.setText("Komposisi");
                        break;
                }
            }).attach();

            fetchIconUrl();
        } else {
            Intent intent = new Intent(DataKependudukanActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "DataKependudukanActivity");
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

    private void fetchIconUrl() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT REPLACE(icon, 'jokosanbps', 'bpstrenggalek') FROM 0007_list_datatabel WHERE id=4";
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

    private void showToast(String message) {
        Toast.makeText(DataKependudukanActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
