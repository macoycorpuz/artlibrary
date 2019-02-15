package com.protobender.artlibary;

import android.app.Application;
import android.content.res.Resources;

public class AppController extends Application {

    private static AppController mInstance;
    public static Resources resources;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }
}
