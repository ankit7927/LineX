package com.x64technology.linex.database.noroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.x64technology.linex.models.Message;
import com.x64technology.linex.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DBService {
    DBHelper dbHelper;
    SQLiteDatabase writableDb, readableDb;
    Context context;

    public DBService(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        writableDb = dbHelper.getWritableDatabase();
        readableDb = dbHelper.getReadableDatabase();
    }

    private String getTableName (String str) {
        return "table" + str.substring(str.length() - 7);
    }


    public void newChat(String userid) {
        String tableName = getTableName(userid);
        writableDb.execSQL(String.format(Constants.MESSAGE_TABLE_QUERY,
                tableName, Constants.ID, Constants.RECEIVER, Constants.SENDER, Constants.CONTENT, Constants.TIME));
    }

    public void insertMsg(String userid, Message message) {
        String tableName = getTableName(userid);
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.RECEIVER, message.to);
        contentValues.put(Constants.SENDER, message.from);
        contentValues.put(Constants.CONTENT, message.content);
        contentValues.put(Constants.TIME, message.time);

        writableDb.insert(tableName, null, contentValues);
    }

    public List<Message> getRangedMessages(String userid) {
        String tableName = getTableName(userid);
        Cursor cursor = readableDb.rawQuery("SELECT * FROM "+tableName, new String[] {});
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext())
            messages.add(new Message(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        cursor.close();
        return messages;
    }
}
