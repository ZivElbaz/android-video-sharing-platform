package com.example.viewtube.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.R;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        // Start the HomeActivity when MainActivity is created
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // Finish the MainActivity so that pressing back won't return to it
        finish();
    }
}
