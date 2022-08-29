package com.wanjy.dannie.rivchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FCAIBF extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



    }
}
