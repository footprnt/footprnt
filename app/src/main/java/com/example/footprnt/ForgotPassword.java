/*
 * ForgotPassword.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Activity to reset user password
 *
 * @author Clarisa Leu-Rodriguez
 * @version 1.0
 * @since 2019-07-22
 */
public class ForgotPassword extends AppCompatActivity {

    Button mResetPassword;
    EditText mEmail;
    EditText mConfirmEmail;

    // TODO: Forgot password doesn't work
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mResetPassword = findViewById(R.id.btnReset);
        mEmail = findViewById(R.id.email);
        mConfirmEmail = findViewById(R.id.etConfirmEmail);

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().equals(mConfirmEmail.getText().toString())) {
                    ParseUser.requestPasswordResetInBackground(mEmail.getText().toString(),
                            new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // An email was successfully sent with reset instructions.
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_reset_check_email), Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error_try_again), Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.emails_must_match), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
