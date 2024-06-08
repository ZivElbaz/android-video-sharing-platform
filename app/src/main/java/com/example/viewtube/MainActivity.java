package com.example.viewtube;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the HomeActivity when MainActivity is created
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // Finish the MainActivity so that pressing back won't return to it
        finish();
    }
}
