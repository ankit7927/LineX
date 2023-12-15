package com.x64technology.linex.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.x64technology.linex.models.Message;

public class MessageDiffCallback extends DiffUtil.ItemCallback<Message> {
    @Override
    public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
        return oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
        return oldItem.senderId.equals(newItem.senderId) &&
                oldItem.senderUsername.equals(newItem.senderUsername) &&
                oldItem.content.equals(newItem.content) &&
                oldItem.time.equals(newItem.time);
    }
}
