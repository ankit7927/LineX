package com.x64technology.linex.database.chat;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.x64technology.linex.models.Chat;

@Database(entities = Chat.class, version = 1)
public abstract class ChatDatabase extends RoomDatabase {
    public abstract ChatDAO chatDAO();
    private static volatile ChatDatabase Instance;
    public static ChatDatabase getInstance(final Context context) {
        if (Instance == null) {
            synchronized (ChatDatabase.class) {
                Instance = Room.databaseBuilder(context, ChatDatabase.class,"chats")
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return Instance;
    }
}
