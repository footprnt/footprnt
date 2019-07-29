package com.example.footprnt.Repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Models.PostWrapper;

import java.util.List;

/**
 * @author Clarisa Leu
 */
public class PostRepository {
    public String DB_NAME = "db_task";

    public PostDatabase postDatabase;

    public PostRepository(Context context) {
        postDatabase = Room.databaseBuilder(context, PostDatabase.class, DB_NAME).build();
    }

    public void insertPost(final PostWrapper post) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                postDatabase.daoAccess().insertPost(post);
                return null;
            }
        }.execute();
    }

//    @SuppressLint("StaticFieldLeak")
//    public void insertPost(final Post post) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                postDatabase.daoAccess().insertPost(post);
//                return null;
//            }
//        }.execute();
//    }
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

//    @SuppressLint("StaticFieldLeak")
//    public void deletePost(final Post post) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                postDatabase.daoAccess().deletePost(post);
//                return null;
//            }
//        }.execute();
//    }

//    public LiveData<Post> getPost(int objectId) {
//        return postDatabase.daoAccess().getPost(objectId);
//    }

    public LiveData<List<PostWrapper>> getPosts() {
        return postDatabase.daoAccess().fetchAllPosts();
    }
}
