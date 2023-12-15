package com.x64technology.linex.database.message;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.x64technology.linex.database.chat.ChatDAO;
import com.x64technology.linex.models.Message;

@Database(entities = {Message.class}, version = 1)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract MessageDAO messageDAO();
    private static volatile MessageDatabase Instance;
    public static MessageDatabase getInstance(final Context context) {
        if (Instance == null) {
            synchronized (MessageDatabase.class) {
                Instance = Room.databaseBuilder(context, MessageDatabase.class,"message_database")
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return Instance;
    }
}
