package com.x64technology.linex.database.contact;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.x64technology.linex.models.Contact;

@Database(entities = Contact.class, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDAO chatDAO();
    private static volatile ContactDatabase Instance;
    public static ContactDatabase getInstance(final Context context) {
        if (Instance == null) {
            synchronized (ContactDatabase.class) {
                Instance = Room.databaseBuilder(context, ContactDatabase.class,"contacts")
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return Instance;
    }
}
