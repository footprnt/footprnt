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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
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
 * @version 1.0
 * @since 2019-07-22
 */
public class UserSettings extends AppCompatActivity {

    private final String TAG = "UserSettings";
    File mPhotoFile;
    ParseFile mParseFile;
    final ParseUser mUser = ParseUser.getCurrentUser();
    ImageView mIvProfileImage;
    TextView mTvEditPhoto;
    ImageView mIvBackArrow;
    ImageView mIvSave;
    EditText mEtUsername;
    EditText mEtNumber;
    EditText mEtEmail;
    EditText mEtDescription;
    Switch mSwitchPrivacy;
    private Boolean userPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        setViews();
        userPrivacy = mUser.getBoolean(AppConstants.privacy);
        if (userPrivacy == null || userPrivacy == false) {
            mSwitchPrivacy.setChecked(false);
        } else if (userPrivacy == true) {
            mSwitchPrivacy.setChecked(true);
        }
        updateCurrentViews();

        // Set on click listener for photo
        mTvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettings.this);
                builder.setTitle(getResources().getString(R.string.upload_or_take_photo));
                builder.setPositiveButton(getResources().getString(R.string.upload), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Upload image
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), AppConstants.GET_FROM_GALLERY);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.take_photo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Take Photo
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mPhotoFile = AppUtil.getPhotoFileUri(UserSettings.this, AppConstants.photoFileName);
                        Uri fileProvider = FileProvider.getUriForFile(UserSettings.this, AppConstants.fileProvider, mPhotoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        if (intent.resolveActivity(UserSettings.this.getPackageManager()) != null) {
                            startActivityForResult(intent, AppConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Go back to profile activity if user clicks back
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
                    mUser.put(AppConstants.profileImage, mParseFile);
                }
                mUser.setEmail(mEtEmail.getText().toString());
                mUser.setUsername(mEtUsername.getText().toString());
                mUser.put(AppConstants.phone, mEtNumber.getText().toString());
                mUser.put(AppConstants.email, mEtEmail.getText().toString());
                mUser.put(AppConstants.description, mEtDescription.getText().toString());
                mUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        updateCurrentViews();
                        Intent data = new Intent();
                        setResult(AppConstants.RELOAD_USER_PROFILE_FRAGMENT_REQUEST_CODE, data);
                        Toast.makeText(UserSettings.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            if (requestCode == AppConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                    mIvProfileImage.setImageBitmap(takenImage);
                    File photoFile = AppUtil.getPhotoFileUri(this, AppConstants.photoFileName);
                    mParseFile = new ParseFile(photoFile);
                } else {
                    mParseFile = null;
                }
            } else {
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (Exception ignored) {
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, AppConstants.captureImageQuality, stream);
                    byte[] image = stream.toByteArray();
                    mParseFile = new ParseFile(AppConstants.profileImagePath, image);
                    final Bitmap finalBitmap = bitmap;
                    Glide.with(this).load(finalBitmap).into(mIvProfileImage);
                } else {
                    mParseFile = null;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.photo_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper method to update the views with the corresponding user information
     */
    public void updateCurrentViews() {
        mIvProfileImage = findViewById(R.id.ivProfileImageMain);
        if (mUser.getParseFile(AppConstants.profileImage) != null) {
            Glide.with(this).load(mUser.getParseFile(AppConstants.profileImage).getUrl()).into(mIvProfileImage);
        }
        mEtUsername.setText(mUser.getUsername());
        mEtDescription.setText(mUser.getString(AppConstants.description));
        mEtNumber.setText(String.format("%s", mUser.get(AppConstants.phone)));
        mEtEmail.setText(String.format("%s", mUser.get(AppConstants.email)));

        mSwitchPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwitchPrivacy.setChecked(true);
                    mUser.put(AppConstants.privacy, true);
                    mUser.saveInBackground();
                } else {
                    mSwitchPrivacy.setChecked(false);
                    mUser.put(AppConstants.privacy, false);
                    mUser.saveInBackground();
                }
            }
        });
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
        mIvSave = findViewById(R.id.ivSaveBtnPost);
        mEtDescription = findViewById(R.id.etDescription);
        mSwitchPrivacy = findViewById(R.id.switchPrivate);
    }
}
