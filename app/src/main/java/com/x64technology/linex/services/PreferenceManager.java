package com.x64technology.linex.services;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public PreferenceManager(Context context) {
        String PREF = "my-pref";
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveCred(String username, String uid, String token) {
        editor.putString("username", username);
        editor.putString("uid", uid);
        editor.putString("token", token);
        editor.apply();
    }

    public String getToken(){
        return  sharedPreferences.getString("token", "");
    }

    public String getUsername(){
        return  sharedPreferences.getString("username", "");
    }

    public String getUid(){
        return  sharedPreferences.getString("uid", "");
    }

    public void saveCurrentChatInfo(String chatId) {
        editor.putString("currentChat", chatId);
    }

    public String getCurrentChatId(){
        return  sharedPreferences.getString("currentChat", "");
    }
}
