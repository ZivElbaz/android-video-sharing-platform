package com.example.viewtube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.viewtube.R;
import com.example.viewtube.entities.User;
import com.example.viewtube.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Handle login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUsernameExistence();
            }
        });

        // Handle register button click
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to RegisterActivity
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    // Check if username exists in the database
    private void checkUsernameExistence() {
        String usernameInput = username.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput)) {
            username.setError("Username is required!");
            return;
        }

        // Check username existence via ViewModel
        userViewModel.checkUsernameExists(usernameInput).observe(this, exists -> {
            if (exists != null && exists) {
                authenticateUser(); // Proceed to authentication if username exists
            } else {
                username.setError("Username does not exist!"); // Show error if username is not found
            }
        });
    }

    // Authenticate user with provided credentials
    private void authenticateUser() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(passwordInput)) {
            password.setError("Password is required!");
            return;
        }

        // Create a User object with input credentials
        User loginUser = new User();
        loginUser.setUsername(usernameInput);
        loginUser.setPassword(passwordInput);

        // Authenticate via ViewModel
        userViewModel.authenticateUser(loginUser).observe(this, authenticatedUser -> {
            if (authenticatedUser != null) {
                // Save user data to SharedPreferences
                saveUserData(authenticatedUser);

                // Show success message and navigate to HomeActivity
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Show error message if authentication fails
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save authenticated user's data to SharedPreferences
    private void saveUserData(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("image", user.getImage());
        editor.apply();
    }
}
