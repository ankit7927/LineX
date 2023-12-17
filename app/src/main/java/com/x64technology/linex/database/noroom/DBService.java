package com.x64technology.linex.database.noroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.x64technology.linex.models.Contact;
import com.x64technology.linex.models.Message;

import java.util.ArrayList;
import java.util.List;

public class DBService {
    DBHelper dbHelper;
    SQLiteDatabase writableDb, readableDb;

    public DBService(Context context) {
        dbHelper = new DBHelper(context);
        writableDb = dbHelper.getWritableDatabase();
        readableDb = dbHelper.getReadableDatabase();
    }

    public void newChat(String tableName) {
        writableDb.execSQL(
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                        tableName, DBStrings.ID, DBStrings.SENDER_ID, DBStrings.SENDER_USERNAME, DBStrings.CONTENT, DBStrings.TIME)
        );
    }

    public void insertContact(String name, String username, String userId, String dp, String reqType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStrings.CONTACT_NAME, name);
        contentValues.put(DBStrings.CONTACT_USERNAME, username);
        contentValues.put(DBStrings.CONTACT_USER_ID, userId);
        contentValues.put(DBStrings.CONTACT_DP_IMAGE_LINK, dp);
        contentValues.put(DBStrings.CONTACT_REQUEST_TYPE, reqType);

        writableDb.insert(DBStrings.CONTACT_TABLE_NAME, null, contentValues);
    }


    public void insertMsg(String tableName, Message message) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBStrings.SENDER_ID, message.senderId);
        contentValues.put(DBStrings.SENDER_USERNAME, message.senderUsername);
        contentValues.put(DBStrings.CONTENT, message.content);
        contentValues.put(DBStrings.TIME, message.time);

        writableDb.insert(tableName, null, contentValues);
    }

    public List<Message> getRangedChat(String tableName) {
        Cursor cursor = readableDb.rawQuery("SELECT * FROM "+tableName, new String[] {});
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            messages.add(new Message(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)).setId(cursor.getInt(0)));
        }
        cursor.close();
        return messages;
    }

    public List<Contact> getContacts() {
        Cursor cursor = readableDb.rawQuery("SELECT * FROM contact", new String[] {});
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            contacts.add(new Contact(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return contacts;
    }
}
