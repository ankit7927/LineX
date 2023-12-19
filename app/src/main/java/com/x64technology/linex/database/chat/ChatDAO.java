package com.x64technology.linex.database.chat;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.x64technology.linex.models.Chat;

import java.util.List;

@Dao
public interface ChatDAO {

    @Query("SELECT * FROM chats")
    LiveData<List<Chat>> getChats();

    @Insert
    void insert(Chat chat);

    @Update
    void update(Chat chat);

    @Delete
    void delete(Chat chat);

    @Query("SELECT * FROM chats WHERE id=:id")
    Chat getChatById(String id);

    @Query("SELECT * FROM chats WHERE userid=:userId")
    Chat getChatByUserId(String userId);
}
