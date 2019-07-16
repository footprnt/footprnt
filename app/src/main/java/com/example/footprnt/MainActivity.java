package com.example.footprnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

// TODO: Add Javadoc
public class MainActivity extends AppCompatActivity {

    // TODO: These are local private variables so naming format should be mVariableName
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginBtn = findViewById(R.id.btn_login);
        signupBtn = findViewById(R.id.btn_signup);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){ // TODO: Space between ")" and "{"
            // TODO: Inline the following line of code
            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Some of my comments will be nit-picking, but both the following lines of code can be inlined
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                login(username, password);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Inline the following line of code
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivityForResult(intent, 20);
            }
        });
    }

    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null){
                    // TODO: Remove log statements from master, there should be error logging but anything lower priority likely only local
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    // TODO: Remove log statements from master, there should be error logging but anything lower priority likely only local
                    Log.d("LoginActivity", "Login unsuccessful");
                }
            }
        });
    }
}
