/*
 * SignUpActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprnt.Util.AppConstants;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Handles all sign up activities
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SignUpActivity";
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mPasswordConfirm;
    private EditText mPhoneInput;
    private EditText mEmailInput;
    private EditText mDescription;
    private Button mSubmitNewUser;
    private String fullName;
    private ParseUser mUser;
    private String profilePicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getFacebookInformation();
        validRegistrationCheck();


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
    //checks to see if information user inputted is valid
    private void validRegistrationCheck() {
        final String username = mUsernameInput.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Username empty", Toast.LENGTH_LONG).show();
            return;
        } else {
            queryUsernameExists(username);
        }
    }

    //gets user's full name and profile picture from Facebook
    private void getFacebookInformation() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            fullName = String.valueOf(object.getString("name"));
                            profilePicId = String.valueOf(object.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        request.executeAsync();
    }

    //completes registration process if new user from facebook login
    private void completeRegister(ParseUser newUser, final String username) {
        newUser.setUsername(username);
        newUser.put("profileImg", profilePicId);
        newUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    /**
     * Helper method to see if user name exists already in Parse DB
     * @param username to query
     */
    private void queryUsernameExists(final String username) {
        ParseQuery<ParseUser> query = getUserByUsername(username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e != null) {
                    Log.d(TAG, "error querying for username");
                    e.printStackTrace();
                } else if (objects.size() != 0){
                    Toast.makeText(SignUpActivity.this, "Username taken", Toast.LENGTH_LONG).show();
                } else {
                    completeRegister(ParseUser.getCurrentUser(), username);
                }
            }
        });
    }

    // Query for users that have the given username.
    public static ParseQuery<ParseUser> getUserByUsername(String username) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        return query;
    }

}
