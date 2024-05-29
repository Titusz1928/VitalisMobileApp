package com.example.ip_mobileapp.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class UserSession {
    private static final String PREFS_NAME = "user_session";
    private static final String KEY_USER = "user";
    private static SharedPreferences sharedPreferences;
    private static UserSession instance;
    private User user;

    private UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userData = sharedPreferences.getString(KEY_USER, null);
        if (userData != null) {
            user = new Gson().fromJson(userData, User.class);
        }
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER, new Gson().toJson(user));
        editor.apply();
    }

    public User getUser() {
        return user;
    }

    public void clearUser() {
        user = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER);
        editor.apply();
    }
}

