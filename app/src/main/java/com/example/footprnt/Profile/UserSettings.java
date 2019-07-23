/*
 * UserSettings.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.example.footprnt.Util.Constants;
import com.example.footprnt.Util.Util;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Activity to allow users to change their profile information
 *
 * @author Clarisa Leu-Rodriguez
 */
public class UserSettings extends AppCompatActivity {
    Util util;
    File mPhotoFile;
    ParseFile mParseFile;
    final ParseUser user = ParseUser.getCurrentUser();

    ImageView mIvProfileImage;
    TextView mTvEditPhoto;
    ImageView mIvBackArrow;
    ImageView mIvSave;
    EditText mEtUsername;
    EditText mEtNumber;
    EditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        util = new Util();
        setViews();
        updateCurrentViews();

        // Set on click listener for photo
        mTvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettings.this);
                builder.setTitle("Upload or Take a Photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Upload image
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), Constants.GET_FROM_GALLERY);
                    }
                });
                builder.setNegativeButton("Take a Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Take Photo
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mPhotoFile = util.getPhotoFileUri(UserSettings.this, Constants.photoFileName);
                        Uri fileProvider = FileProvider.getUriForFile(UserSettings.this, Constants.fileProvider, mPhotoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        if (intent.resolveActivity(UserSettings.this.getPackageManager()) != null) {
                            startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Go back to profile activity if user clicks back
        // TODO: fix
        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParseFile != null) {
                    user.put(Constants.profileImage, mParseFile);
                }

                user.setEmail(mEtEmail.getText().toString());
                user.setUsername(mEtUsername.getText().toString());
                user.put(Constants.phone, mEtNumber.getText().toString());
                user.put(Constants.email, mEtEmail.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        updateCurrentViews();
                        Intent data = new Intent();
                        setResult(Constants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE, data);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == com.example.footprnt.Util.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == this.RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                mIvProfileImage.setImageBitmap(takenImage);
                File photoFile = util.getPhotoFileUri(this, Constants.photoFileName);
                mParseFile = new ParseFile(photoFile);
            } else {
                mParseFile = null;
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (resultCode == this.RESULT_OK) {
                Bitmap bitmap = null;
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (Exception e) {
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.captureImageQuality, stream);
                byte[] image = stream.toByteArray();
                mParseFile = new ParseFile(Constants.profileImagePath, image);
                final Bitmap finalBitmap = bitmap;
                Glide.with(this).load(finalBitmap).into(mIvProfileImage);
            } else {
                mParseFile = null;
            }
        }
    }

    /**
     * Helper method to update the views with the corresponding user information
     */
    public void updateCurrentViews() {
        mIvProfileImage = findViewById(R.id.ivProfileImageMain);
        if (user.getParseFile(Constants.profileImage) != null) {
            Glide.with(this).load(user.getParseFile(Constants.profileImage).getUrl()).into(mIvProfileImage);
        } else {
            Glide.with(this).load(R.drawable.ic_user).into(mIvProfileImage);
        }
        mEtUsername.setText(user.getUsername());
        mEtNumber.setText(String.format("%s", user.get(Constants.phone)));
        mEtEmail.setText(String.format("%s", user.get(Constants.email)));
    }

    /**
     * Helper method to set the view meta data for the UserSettings Activity
     */
    public void setViews() {
        mIvProfileImage = findViewById(R.id.ivProfileImageMain);
        mTvEditPhoto = findViewById(R.id.tvEditPhoto);
        mIvBackArrow = findViewById(R.id.ivBack);
        mEtUsername = findViewById(R.id.etUsername);
        mEtNumber = findViewById(R.id.etNumber);
        mEtEmail = findViewById(R.id.etEmail);
        mIvSave = findViewById(R.id.ivSave);
    }
}
