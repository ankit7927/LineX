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

    public void newChat(String userid) {
        String tableName = "table_" + userid;
        writableDb.execSQL(String.format(Constants.MESSAGE_TABLE_QUERY,
                tableName, Constants.ID, Constants.RECEIVER, Constants.SENDER, Constants.CONTENT, Constants.TIMESTAMP, Constants.IS_MINE));
    }

    public void insertMsg(String userid, Message message) {
        String tableName = "table_" + userid;
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.RECEIVER, message.receiver);
        contentValues.put(Constants.SENDER, message.sender);
        contentValues.put(Constants.CONTENT, message.content);
        contentValues.put(Constants.TIMESTAMP, message.timestamp);
        contentValues.put(Constants.IS_MINE, message.isMine ? 1 : 0);

        writableDb.insert(tableName, null, contentValues);
    }

    public List<Message> getRangedMessages(String userid) {
        String tableName = "table_" + userid;
        String r =String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 15", tableName, Constants.TIMESTAMP);
        Cursor cursor = readableDb.rawQuery(r, new String[] {});
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext())
            messages.add(0, new Message(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5) == 1));
        cursor.close();
        return messages;
    }
}
