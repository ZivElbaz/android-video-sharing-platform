package com.example.viewtube.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.viewtube.R;
import com.example.viewtube.viewmodels.UserViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        currentPasswordEditText = findViewById(R.id.edit_current_password);
        newPasswordEditText = findViewById(R.id.edit_new_password);
        changePasswordButton = findViewById(R.id.change_password_button);

        String username = getIntent().getStringExtra("username");

        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();

            if (!TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(newPassword)) {
                userViewModel.updateUserPassword(username, currentPassword, newPassword);
                Toast.makeText(this, "Password Changed!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
