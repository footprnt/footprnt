package com.example.footprnt.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.parse.ParseUser;

public class UserSettings extends AppCompatActivity {
    ImageView ivProfileImage;
    TextView tvEditPhoto;
    ImageView ivBackArrow;
    ImageView ivSave;
    EditText etPassword;
    EditText etUsername;
    EditText etNumber;
    EditText etEmail;
    final ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Set Views
        ivProfileImage = findViewById(R.id.ivProfileImageMain);
        tvEditPhoto = findViewById(R.id.tvEditPhoto);
        ivBackArrow = findViewById(R.id.ivBack);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        etNumber = findViewById(R.id.etNumber);
        etEmail = findViewById(R.id.etEmail);
        ivSave = findViewById(R.id.ivSave);

        updateCurrentViews();


        // Set on click listener for photo
        tvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettings.this);
                builder.setTitle("Upload or Take a Photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Upload image
                    }
                });
                builder.setNegativeButton("Take a Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Take Photo
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Go back to profile activity if user clicks back
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(etEmail.getText().toString());
                user.setUsername(etUsername.getText().toString());
                user.put("phone", etNumber.getText().toString());
                user.put("email", etEmail.getText().toString());
                user.saveInBackground();
                updateCurrentViews();
                finish();
            }
        });

    }


    public void updateCurrentViews(){
        // Populate
        // For profile image:
        ivProfileImage = findViewById(R.id.ivProfileImageMain);
        if (user.getParseFile("profileImg") != null) {
            Glide.with(this).load(user.getParseFile("profileImg").getUrl()).into(ivProfileImage);
        } else {
            Glide.with(this).load(R.drawable.ic_user).into(ivProfileImage);
        }

        // EditText:
        //etPassword.setText(user.get("password").toString());
        etUsername.setText(user.getUsername());
        etNumber.setText(String.format("%s",user.get("phone")));
        etEmail.setText(String.format("%s",user.get("email")));
    }



}
