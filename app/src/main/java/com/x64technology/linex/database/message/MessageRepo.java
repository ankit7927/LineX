package com.x64technology.linex.database.message;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.x64technology.linex.models.Message;

import java.util.List;

public class MessageRepo {
    private final MessageDAO messageDAO;
    public MessageRepo(Application application) {
        messageDAO = MessageDatabase.getInstance(application).messageDAO();
    }


    LiveData<List<Message>> getMessages() {
        return messageDAO.getMessages();
    }


    void insert(Message message) {
        messageDAO.insert(message);
    }


    void cleanTable() {
        messageDAO.cleanTable();
    }
}
