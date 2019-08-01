/*
 * SignUpActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Handles all sign up activities
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class SignUpActivity extends AppCompatActivity {

    private final String TAG = SignUpActivity.class.getSimpleName();
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

        initializeViews();

        mSubmitNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup(){
        ParseUser user = new ParseUser();
        String newUsername = mUsernameInput.getText().toString();
        if (userNameDoesNotExist(newUsername)) {
            user.setUsername(mUsernameInput.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
            return;
        }
        user.put(AppConstants.description, mDescription.getText().toString());
        if (mPasswordInput.getText().toString().equals(mPasswordConfirm.getText().toString())) {
            user.setPassword(mPasswordInput.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return;
        }
        String newEmail = mEmailInput.getText().toString();
        if (AppUtil.isValidEmail(newEmail)) {
            user.setEmail(newEmail);
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        user.put(AppConstants.phone, mPhoneInput.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Helper method to initialize views
     */
    private void initializeViews() {
        mUsernameInput = findViewById(R.id.new_username);
        mPasswordInput = findViewById(R.id.new_password);
        mPhoneInput = findViewById(R.id.phone_number);
        mEmailInput = findViewById(R.id.email);
        mSubmitNewUser = findViewById(R.id.btnReset);
        mPasswordConfirm = findViewById(R.id.etConfirmEmail);
        mPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signup();
                }
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        mDescription = findViewById(R.id.etDescription);
    }

    /**
     * Helper method to see if user name exists already in Parse DB
     *
     * @param username to query
     * @return true is username doesn't exist, false if it does
     */
    private boolean userNameDoesNotExist(final String username) {
        final boolean[] res = {true};
        ParseQuery<ParseUser> query = getUserByUsername(username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "error querying for username");
                    e.printStackTrace();
                    res[0] = false;
                } else if (objects.size() != 0) {
                    Toast.makeText(SignUpActivity.this, "Username taken", Toast.LENGTH_LONG).show();
                    res[0] = false;
                } else {
                    res[0] = true;
                }
            }
        });
        return res[0];
    }

    private ParseQuery<ParseUser> getUserByUsername(String username) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        return query;
    }
}
