package com.example.footprnt.Repository;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Models.Post;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Clarisa Leu
 */
public class PostRepository {
    private String DB_NAME = "db_task";

    private PostDatabase postDatabase;

    public PostRepository(Context context) {
        postDatabase = Room.databaseBuilder(context, PostDatabase.class, DB_NAME).build();
    }

    public void insetPost(String title,
                          String description, ParseFile image, ParseGeoPoint location, ParseUser user, String city, String country, String continent, ArrayList<String> tags) {
        insertPost(title, description, image, location, user, city, country, continent, tags);
    }

    public void insertPost(String title,
                           String description, ParseFile image, ParseGeoPoint location, ParseUser user, String city, String country, String continent, ArrayList<String> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setTitle(title);
        post.setImage(image);
        post.setLocation(location);
        post.setUser(user);
        post.setCity(city);
        post.setCountry(country);
        post.setContinent(continent);
        post.setTags(tags);
        insertPost(post);
    }

    @SuppressLint("StaticFieldLeak")
    public void insertPost(final Post post) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                postDatabase.daoAccess().insertPost(post);
                return null;
            }
        }.execute();
    }
//
//    public void updatePost(final Post post) {
//        post.setModifiedAt(AppUtils.getCurrentDateTime());
//
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                postDatabase.daoAccess().updatePost(post);
//                return null;
//            }
//        }.execute();
//    }

    @SuppressLint("StaticFieldLeak")
    public void deletePost(final Post post) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                postDatabase.daoAccess().deletePost(post);
                return null;
            }
        }.execute();
    }

    public LiveData<Post> getTask(int id) {
        return postDatabase.daoAccess().getPost(id);
    }

    public LiveData<List<Post>> getPosts() {
        return postDatabase.daoAccess().fetchAllPosts();
    }
}
