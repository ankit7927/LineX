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
        return oldChats.get(oldItemPosition).id == newChats.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Chat oldChat = oldChats.get(oldItemPosition);
        Chat newChat = newChats.get(newItemPosition);

        return oldChat.equals(newChat);
    }
}
