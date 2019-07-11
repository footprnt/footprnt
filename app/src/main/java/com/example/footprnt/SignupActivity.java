package com.example.footprnt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText phoneInput;
    private EditText emailInput;
    private Button submitNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameInput = findViewById(R.id.new_username);
        passwordInput = findViewById(R.id.new_password);
        phoneInput = findViewById(R.id.phone_number);
        emailInput = findViewById(R.id.email);
        submitNewUser = findViewById(R.id.btn_confirm_signup);

        submitNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(usernameInput.getText().toString());
                user.setPassword(passwordInput.getText().toString());
                user.setEmail(emailInput.getText().toString());
                user.put("phone", phoneInput.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null){
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
