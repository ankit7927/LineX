package com.x64technology.linex.database.message;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.x64technology.linex.models.Message;

import java.util.List;

@Dao
public interface MessageDAO {
    @Query("SELECT * FROM TempMessage")
    LiveData<List<Message>> getMessages();

    @Insert
    void insert(Message message);

    @Query("DELETE FROM TEMPMESSAGE")
    void cleanTable();
}
