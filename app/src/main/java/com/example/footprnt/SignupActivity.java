package com.example.footprnt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Handles all sign up activities
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class SignupActivity extends AppCompatActivity {

    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mPhoneInput;
    private EditText mEmailInput;
    private Button mSubmitNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameInput = findViewById(R.id.new_username);
        mPasswordInput = findViewById(R.id.new_password);
        mPhoneInput = findViewById(R.id.phone_number);
        mEmailInput = findViewById(R.id.email);
        mSubmitNewUser = findViewById(R.id.btn_confirm_signup);

        mSubmitNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(mUsernameInput.getText().toString());
                user.setPassword(mPasswordInput.getText().toString());
                user.setEmail(mEmailInput.getText().toString());
                user.put("phone", mPhoneInput.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to create account", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
