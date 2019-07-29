/*
 * SignUpActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.footprnt.Util.AppConstants;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Handles all sign up activities
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mPasswordConfirm;
    private EditText mPhoneInput;
    private EditText mEmailInput;
    private EditText mDescription;
    private Button mSubmitNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameInput = findViewById(R.id.new_username);
        mPasswordInput = findViewById(R.id.new_password);
        mPhoneInput = findViewById(R.id.phone_number);
        mEmailInput = findViewById(R.id.email);
        mSubmitNewUser = findViewById(R.id.btnReset);
        mPasswordConfirm = findViewById(R.id.etConfirmEmail);
        mDescription = findViewById(R.id.etDescription);

        mSubmitNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(mUsernameInput.getText().toString());
                user.put(AppConstants.description, mDescription.getText().toString());
                if (mPasswordInput.getText().toString().equals(mPasswordConfirm.getText().toString())) {
                    user.setPassword(mPasswordInput.getText().toString());

                } else {
                    Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEmailInput.getText().toString().contains(".")) {
                    user.setEmail(mEmailInput.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.put(AppConstants.phone, mPhoneInput.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to create account, please try again.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
