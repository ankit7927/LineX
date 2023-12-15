package com.x64technology.linex.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.x64technology.linex.models.Chat;

import java.util.List;

public class ChatDiffUtil extends DiffUtil.Callback {
    private final List<Chat> oldChats;
    private final List<Chat> newChats;

    public ChatDiffUtil(List<Chat> oldChats, List<Chat> newChats) {
        this.oldChats = oldChats;
        this.newChats = newChats;
    }


    @Override
    public int getOldListSize() {
        return oldChats.size();
    }

    @Override
    public int getNewListSize() {
        return newChats.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldChats.get(oldItemPosition).getId() == newChats.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Chat oldChat = oldChats.get(oldItemPosition);
        Chat newChat = oldChats.get(newItemPosition);

        return oldChat.getId() == newChat.getId() &&
                oldChat.getTable_name().equals(newChat.getTable_name()) &&
                oldChat.getRoom().equals(newChat.getRoom()) &&
                oldChat.getUsername().equals(newChat.getUsername()) &&
                oldChat.getUser_id().equals(newChat.getUser_id()) &&
                oldChat.getLast_msg().equals(newChat.getLast_msg());
    }
}
