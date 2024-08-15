package com.trengginas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.trengginas.R;

public class KontakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontak);

        CardView cvAlamat = findViewById(R.id.cv_alamat);
        CardView cvTelp = findViewById(R.id.cv_telp);
        CardView cvEmail = findViewById(R.id.cv_email);
        CardView cvWeb = findViewById(R.id.cv_web);
        CardView cvFb = findViewById(R.id.cv_fb);
        CardView cvIg = findViewById(R.id.cv_ig);
        CardView cvWa = findViewById(R.id.cv_wa);

        cvAlamat.setOnClickListener(v -> openUrl("https://maps.app.goo.gl/wXXbzDN7A8B2rncm7"));
        cvTelp.setOnClickListener(v -> showConfirmationDialog());
        cvEmail.setOnClickListener(v -> openEmail());
        cvWeb.setOnClickListener(v -> openUrl("https://trenggalekkab.bps.go.id"));
        cvFb.setOnClickListener(v -> openUrl("https://m.facebook.com/BPSKabupatenTrenggalek"));
        cvIg.setOnClickListener(v -> openUrl("https://m.instagram.com/bps_trenggalek"));
        cvWa.setOnClickListener(v -> openWhatsApp("+6281249476727", "Ysh. BPS Kab. Trenggalek"));

        if (!isNetworkConnected()) {
            Intent intent = new Intent(KontakActivity.this, NetworkCheckActivity.class);
            intent.putExtra("startValue", "kontak");
            startActivity(intent);
            finish();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        return false;
    }


    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("KONFIRMASI")
                .setMessage("Anda YAKIN akan menghubungi BPS Trenggalek melalui telepon?")
                .setPositiveButton("YA", (dialog, which) -> callPhoneNumber("0355791432"))
                .setNegativeButton("TIDAK", null)
                .show();
    }

    private void callPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:bps3503@bps.go.id"));
        startActivity(intent);
    }

    private void openWhatsApp(String number, String message) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + number + "&text=" + message));
        startActivity(intent);
    }
}
