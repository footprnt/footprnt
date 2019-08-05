package com.example.footprnt.Profile;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.Profile.Adapters.SavedPostsAdapter;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Saved Posts activity
 * @author Clarisa Leu
 */
public class SavedPosts extends AppCompatActivity {

    ImageView mBackButton;
    RecyclerView mRvSavedPosts;
    ArrayList<SavedPost> mSavedPosts;
    SavedPostsAdapter mSavedPostsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        mSavedPosts = new ArrayList<>();
        mBackButton = findViewById(R.id.ivBack);
        mRvSavedPosts = findViewById(R.id.rvSavedPosts);

        getSavedPosts();

    }

    /**
     * Helper method to query saved posts
     */
    private void getSavedPosts(){
        final SavedPost.Query query = new SavedPost.Query();
    }
}
