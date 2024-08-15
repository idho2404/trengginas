package com.trengginas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.trengginas.R;

public class NetworkCheckActivity extends AppCompatActivity {

    private String originActivityClassName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);

        if (getIntent() != null && getIntent().hasExtra("originActivityClassName")) {
            originActivityClassName = getIntent().getStringExtra("originActivityClassName");
        }

        findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originActivityClassName != null && !originActivityClassName.isEmpty()) {
                    try {
                        // Convert the full class name string into a Class object
                        Class<?> activityClass = Class.forName(originActivityClassName);
                        Intent intent = new Intent(NetworkCheckActivity.this, activityClass);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        // Handle the exception, possibly show an error message
                    }
                }
                finish();
            }
        });
    }
}
