package com.x64technology.linex.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.x64technology.linex.models.Message;

import java.util.List;

public class MessageDiffUtil extends DiffUtil.Callback {

    private final List<Message> oldMessages;
    private final List<Message> newMessages;

    public MessageDiffUtil(List<Message> oldMessages, List<Message> newMessages) {
        this.oldMessages = oldMessages;
        this.newMessages = newMessages;
    }

    @Override
    public int getOldListSize() {
        return oldMessages.size();
    }

    @Override
    public int getNewListSize() {
        return newMessages.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMessages.get(oldItemPosition).id == newMessages.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Message old = oldMessages.get(oldItemPosition);
        Message nMs = newMessages.get(newItemPosition);

        return old.equals(nMs);
    }
}
