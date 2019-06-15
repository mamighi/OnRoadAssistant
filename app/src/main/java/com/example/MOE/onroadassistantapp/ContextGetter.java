package com.example.emad.onroadassistantapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Emad on 6/9/2017.
 */

public class ContextGetter extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}