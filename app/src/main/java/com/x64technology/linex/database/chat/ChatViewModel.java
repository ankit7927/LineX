package com.x64technology.linex.database.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.x64technology.linex.models.Chat;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    final private ChatRepo chatRepo;
    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepo = new ChatRepo(application);
    }

    public LiveData<List<Chat>> getChats() {
        return chatRepo.getChats();
    }

    public void addNewChat(Chat chat) {
        chatRepo.addNewChat(chat);
    }

    public void updateChat(Chat chat) {
        chatRepo.updateChat(chat);
    }

    public void deleteChat(Chat chat) {
        chatRepo.deleteChat(chat);
    }

    public Chat getChatById(String id) {
        return chatRepo.getChatById(id);
    }

    public Chat getChatByUsername(String username) {
        return chatRepo.getChatByUsername(username);
    }

    public Chat getChatByUserId(String userid) {
        return chatRepo.getChatByUserId(userid);
    }
}
