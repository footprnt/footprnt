/*
 * LoginActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprnt.Util.AppConstants;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Handles all login activity
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private TextView mForgotPassword;
    private Button mLoginBtn;
    private Button mSignUpBtn;
    private Button mFacebookLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        // Persisted login
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        // TODO: implement forgot password
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

        setUpFacebookLogin();
    }

    /**
     * Helper method for initialization
     */
    private void initialization() {
        // For querying on main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Set views
        mUsernameInput = findViewById(R.id.username);
        mForgotPassword = findViewById(R.id.forgotPassword);
        mPasswordInput = findViewById(R.id.password);
        mPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    final String username = mUsernameInput.getText().toString();
                    final String password = mPasswordInput.getText().toString();
                    login(username, password);
                }
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        mLoginBtn = findViewById(R.id.btn_login);
        mSignUpBtn = findViewById(R.id.btn_signup);
        mFacebookLoginBtn = findViewById(R.id.btn_fb_login);
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

    /**
     * Helper method to set up connecting with Facebook
     */
    private void setUpFacebookLogin() {
        mFacebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList();
                permissions.add(AppConstants.email);
                permissions.add(AppConstants.PUBLIC_PROFILE);
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d(TAG, "Error occurred" + err.toString());
                                    err.printStackTrace();
                                } else if (user == null) {
                                    Log.d(TAG, "User cancelled the Facebook login.");
                                } else {
                                    handleFacebookUser(user);
                                }
                            }
                        }
                );
            }
        });
    }

    /**
     * Helper method to handle valid user signing up with Facebook
     * @param user
     */
    private void handleFacebookUser(final ParseUser user) {
        if (user.isNew()) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                // Set up new Facebook user
                                user.put(AppConstants.username, String.valueOf(object.getString(AppConstants.name)));
                                // TODO: set description?
                                //user.put(AppConstants.description,"");
                                URL picUrl = new URL(String.format("https://graph.facebook.com/%s/picture?type=large", Profile.getCurrentProfile().getId()));
                                Bitmap bitmap = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, AppConstants.captureImageQuality, byteArrayOutputStream);
                                byte[] imageByte = byteArrayOutputStream.toByteArray();
                                ParseFile parseFile = new ParseFile(AppConstants.profileImagePathJPEG, imageByte);
                                // Save Facebook profile to DB
                                if (parseFile != null) {
                                    user.put(AppConstants.profileImage, parseFile);
                                }
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Intent it = new Intent(LoginActivity.this,
                                                HomeActivity.class);
                                        startActivity(it);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            request.executeAsync();
        } else {
            // Returning user - go to Map
            startActivity(new Intent(LoginActivity.this,
                    HomeActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Required for making Facebook login work
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}


