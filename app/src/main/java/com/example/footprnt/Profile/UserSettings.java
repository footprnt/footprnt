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
import com.example.footprnt.Util.PhotoHelper;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.example.footprnt.Map.MapFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.footprnt.Map.MapFragment.GET_FROM_GALLERY;

public class UserSettings extends AppCompatActivity {
    ImageView ivProfileImage;
    TextView tvEditPhoto;
    ImageView ivBackArrow;
    ImageView ivSave;
    EditText etPassword;
    EditText etUsername;
    EditText etNumber;
    EditText etEmail;
    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseFile parseFile;
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
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                    }
                });
                builder.setNegativeButton("Take a Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Take Photo
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        PhotoHelper photoHelper = new PhotoHelper();
                        photoFile = photoHelper.getPhotoFileUri(UserSettings.this, photoFileName);

                        Uri fileProvider = FileProvider.getUriForFile(UserSettings.this, "com.example.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                        if (intent.resolveActivity(UserSettings.this.getPackageManager()) != null) {
                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
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

                if (parseFile == null){
                    user.remove("profileImg");
                } else {
                    user.put("profileImg",parseFile);
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == this.RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivProfileImage.setImageBitmap(takenImage);
                PhotoHelper photoHelper = new PhotoHelper();
                File photoFile = photoHelper.getPhotoFileUri(this, photoFileName);
                parseFile = new ParseFile(photoFile);
            } else {
                parseFile = null;
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if (resultCode == this.RESULT_OK) {
                Bitmap bitmap = null;
                Uri selectedImage = data.getData();
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (Exception e) {
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                parseFile = new ParseFile("profpic.jpg", image);
                final Bitmap finalBitmap = bitmap;
                Glide.with(this).load(finalBitmap).into(ivProfileImage);
            } else {
                parseFile = null;
            }
        }

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
