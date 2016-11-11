package com.example.mena.myapplication;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Mena on 11/11/16.
 */
public class SimplePost extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

       //
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
