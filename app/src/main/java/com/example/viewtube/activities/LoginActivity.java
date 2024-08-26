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

    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    checkUsernameExistence();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void checkUsernameExistence() {
        String usernameInput = username.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput)) {
            username.setError("Username is required!");
            return;
        }

        userViewModel.checkUsernameExists(usernameInput).observe(this, exists -> {
            if (exists != null && exists) {
                authenticateUser();
            } else {
                username.setError("Username does not exist!");
            }
        });
    }

    private void authenticateUser() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(passwordInput)) {
            password.setError("Password is required!");
            return;
        }

        User loginUser = new User();
        loginUser.setUsername(usernameInput);
        loginUser.setPassword(passwordInput);

        userViewModel.authenticateUser(loginUser).observe(this, authenticatedUser -> {
            if (authenticatedUser != null) {
                // Save user data to SharedPreferences
                saveUserData(authenticatedUser);

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
