/*
 * LoginActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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

    private final String TAG = "LoginActivity";
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private TextView mForgotPassword;
    private Button mLoginBtn;
    private Button mSignUpBtn;
    private Button mFacebookLoginBtn;
    private String fullName;
    private String profilePicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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


        setUpFacebookLogin();
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
        // Required for making Facebook login work
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpFacebookLogin() {
        mFacebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList();
                permissions.add("email");
                permissions.add("public_profile");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d(TAG, "Error occurred" + err.toString());
                                    err.printStackTrace();
                                } else if (user == null) {
                                    Log.d(TAG, "The user cancelled the Facebook login.");
                                } else {
                                    handleValidUser(user);
                                }
                            }
                        }
                );
            }
        });
    }

    private void handleValidUser(final ParseUser user) {
        if (user.isNew()) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                fullName = String.valueOf(object.getString("name"));
                                user.put("username", fullName);
                                profilePicId = String.valueOf(object.getString("id"));

                                URL picUrl = new URL("https://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large");
                                Bitmap bitmap = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
                                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                                byte[] imageByte = byteArrayOutputStream.toByteArray();
                                ParseFile parseFile = new ParseFile("image_file.JPEG",imageByte);
                                if(parseFile!=null){
                                    user.put("profileImg", parseFile);
                                }

                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        startActivity(new Intent(LoginActivity.this,
                                                HomeActivity.class));
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
            startActivity(new Intent(LoginActivity.this,
                    HomeActivity.class));
        }
    }
}


