package com.x64technology.linex.database.contact;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.x64technology.linex.models.Contact;
import com.x64technology.linex.utils.Constants;

import java.util.List;


@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contacts")
    LiveData<List<Contact>> getAllContacts();

    @Query("SELECT * FROM contacts WHERE reqType=:type")
    List<Contact> getTypedContacts(String type);

    @Insert
    void insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("SELECT * FROM contacts WHERE id=:id")
    Contact getContactById(String id);

    @Query("SELECT * FROM contacts WHERE userid=:userId")
    Contact getContactByUserId(String userId);
}
