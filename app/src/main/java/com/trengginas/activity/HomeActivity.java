package com.trengginas.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trengginas.R;
import com.trengginas.adapter.HomeAdapter;
import com.trengginas.api.Api;
import com.trengginas.api.ApiClient;
import com.trengginas.model.Home;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout loaderLayout = findViewById(R.id.loader_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new HomeAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (isConnected()) {
            loaderLayout.setVisibility(View.VISIBLE); // Show loader
            loadItems();
        } else {
            Intent intent = new Intent(HomeActivity.this, NetworkCheckActivity.class);
            intent.putExtra("origin", "HomeActivity");
            startActivity(intent);
            finish();
        }

        // Handle the back button press with a callback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadItems() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        String query = "SELECT * FROM 0006_list_home";
        String key = "trengginas";

        Call<String> call = api.getListHome(query, key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LinearLayout loaderLayout = findViewById(R.id.loader_layout);
                loaderLayout.setVisibility(View.GONE); // Hide loader once data is loaded
                if (response.isSuccessful() && response.body() != null) {
                    List<Home> homes = parseResponse(response.body());
                    adapter.updateData(homes);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LinearLayout loaderLayout = findViewById(R.id.loader_layout);
                loaderLayout.setVisibility(View.GONE); // Hide loader on error
                Toast.makeText(HomeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Home> parseResponse(String response) {
        List<Home> homes = new ArrayList<>();
        String[] rows = response.split("\n");
        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] columns = row.split(",");
            if (columns.length == 5) {
                String icon = columns[1];
                String judul = columns[2];
                String subjudul = columns[3];
                String nmscreen = columns[4];
                if (nmscreen != null) { // Check if nmscreen is not null
                    Home home = new Home(judul, subjudul, nmscreen, icon);
                    homes.add(home);
                }
            }
        }
        return homes;
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

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Sebelum keluar, jika berkenan mengisi survei silahkan pilih ISI SURVEY, jika tidak berkenan atau sudah pernah tekan OK untuk Keluar.");

        builder.setPositiveButton("ISI SURVEY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isConnected()) {
                    // Open the survey URL in a WebView or browser
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSd_skkj6MDXl5aHbnYBqwI1p7212UqO88iXNqk91zrLIdv3_w/viewform")); // Replace with your survey URL
                    startActivity(intent);
                } else {
                    showNoInternetDialog();
                }
            }
        });

        builder.setNegativeButton("BATAL", null);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // Close the application
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need an internet connection to access the survey. Please check your connection and try again.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
