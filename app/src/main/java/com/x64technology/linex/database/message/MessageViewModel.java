package com.x64technology.linex.database.message;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.x64technology.linex.models.Message;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    final private MessageRepo messageRepo;
    public MessageViewModel(@NonNull Application application) {
        super(application);
        messageRepo = new MessageRepo(application);
    }

    public LiveData<List<Message>> getMessages() {
        return messageRepo.getMessages();
    }


    public void insert(Message message) {
        messageRepo.insert(message);
    }


    public void cleanTable() {
        messageRepo.cleanTable();
    }
}
