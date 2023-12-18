package com.x64technology.linex.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.x64technology.linex.utils.Constants;

public class AppPreference {
    public final SharedPreferences appPref;
    private final SharedPreferences.Editor editor;
    public AppPreference(Context context) {
        String PREF = "my-pref";
        appPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = appPref.edit();
    }


    public void saveCurrentChat(String chatId) {
        editor.putString(Constants.STR_CURRENT_CHAT, chatId);
        editor.apply();
    }

    public void removeCurrentChat() {
        editor.remove(Constants.STR_CURRENT_CHAT);
        editor.apply();
    }
}
