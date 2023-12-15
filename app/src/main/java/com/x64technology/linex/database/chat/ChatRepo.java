package com.x64technology.linex.database.chat;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.x64technology.linex.models.Chat;

import java.util.List;

public class ChatRepo {
    private final ChatDAO chatDAO;
    public ChatRepo(Application application) {
        chatDAO = ChatDatabase.getInstance(application).chatDAO();
    }

    public LiveData<List<Chat>> getChats() {
        return chatDAO.getChats();
    }

    public void addNewChat(Chat chat) {
        chatDAO.insert(chat);
    }

    public void updateChat(Chat chat) {
        chatDAO.update(chat);
    }

    public void deleteChat(Chat chat) {
        chatDAO.delete(chat);
    }

    public Chat getChatById(String id) {
        return chatDAO.getChatById(id);
    }

    public Chat getChatByUsername(String username) {
        return chatDAO.getChatByUsername(username);
    }

    public Chat getChatByUserId(String userid) {
        return chatDAO.getChatByUserId(userid);
    }

}
