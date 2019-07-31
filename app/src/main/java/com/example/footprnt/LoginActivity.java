/*
 * LoginActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprnt.Util.AppConstants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all login activity
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private TextView mForgotPassword;
    private Button mLoginBtn;
    private Button mSignUpBtn;
    private Button mFacebookLoginBtn;
    private final List<String> permissions = Arrays.asList(AppConstants.PUBLIC_PROFILE, AppConstants.email);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameInput = findViewById(R.id.username);
        mForgotPassword = findViewById(R.id.forgotPassword);
        mPasswordInput = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.btn_login);
        mSignUpBtn = findViewById(R.id.btn_signup);
        mFacebookLoginBtn = findViewById(R.id.btn_fb_login);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        // TODO: implement
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivityForResult(it, 123);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameInput.getText().toString();
                final String password = mPasswordInput.getText().toString();
                login(username, password);
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, AppConstants.SIGN_UP_ACTIVITY_REQUEST_CODE);
            }
        });

        mFacebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFB();
            }
        });
    }

    /**
     * Helper method to login/connect with Facebook
     */
    private void loginFB() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "Error logging in with Facebook", Toast.LENGTH_LONG).show();
                } else if (user.isNew()) {
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                } else {
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Attempt Parse login
     *
     * @param username username of user attempting login
     * @param password password of user attempting login
     */
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
