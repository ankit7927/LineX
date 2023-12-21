package com.x64technology.linex.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.x64technology.linex.utils.Constants;

public class AppPreference {
    public final SharedPreferences appPref;
    private final SharedPreferences.Editor editor;
    public AppPreference(Context context) {
        appPref = context.getSharedPreferences(Constants.APP_PREFERENCE, Context.MODE_PRIVATE);
        editor = appPref.edit();
    }


    public void saveActiveUser(String chatId) {
        editor.putString(Constants.STR_ACTIVE_USER, chatId);
        editor.apply();
    }

    public void removeActiveUser() {
        editor.remove(Constants.STR_ACTIVE_USER);
        editor.apply();
    }
}
