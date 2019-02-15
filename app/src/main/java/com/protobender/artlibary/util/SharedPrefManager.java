package com.protobender.artlibary.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.protobender.artlibary.model.entity.User;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager = new SharedPrefManager();

    public static SharedPrefManager getInstance() {
        return sharedPrefManager;
    }

    public void userLogin(Context mCtx, User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Tags.KEY_USER_ID, user.getUserId());
        editor.putString(Tags.KEY_USER_NAME, user.getName());
        editor.putString(Tags.KEY_USER_EMAIL, user.getEmail());
        editor.putString(Tags.KEY_USER_NUMBER, user.getNumber());
        editor.putString(Tags.KEY_USER_ADDRESS, user.getAddress());
        editor.apply();
    }

    public boolean isLoggedIn(Context mCtx) {
        SharedPreferences sharedP = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return (sharedP.getInt(Tags.KEY_USER_ID, 0) != 0 );
    }

    public User getUser(Context mCtx) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(Tags.KEY_USER_ID, 0),
                sharedPreferences.getString(Tags.KEY_USER_NAME, null),
                sharedPreferences.getString(Tags.KEY_USER_EMAIL, null),
                sharedPreferences.getString(Tags.KEY_USER_NUMBER, null),
                sharedPreferences.getString(Tags.KEY_USER_ADDRESS, null)
        );
    }

    public void logout(Context mCtx) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
