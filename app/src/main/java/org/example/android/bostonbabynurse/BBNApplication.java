package org.example.android.bostonbabynurse;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

public class BBNApplication extends Application {
    public static final String APPLICATION_ID = "FHr4iYzp2owb09hTDgSoar5vJowOPDmdeN5Q1Myq";
    public static final String CLIENT_KEY = "xRm0mffpR9dT15rFkrv3VXtHCduAL1F6o7tUYKMe";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("BBNApplicationClass", "Is this the first thing that runs?");

        ParseObject.registerSubclass(Message.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }


}