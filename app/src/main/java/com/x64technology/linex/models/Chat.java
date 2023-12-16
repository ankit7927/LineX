package com.x64technology.linex.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "chats")
public class Chat implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int chatId;
    public String messageTableName;
    public String chatUsername;
    public String chatUserId;
    public String room;
    public String lastMessage;
    public int unreadSmgCount;

    public Chat(String messageTableName, String chatUsername, String chatUserId, String room, String lastMessage, int unreadSmgCount) {
        this.messageTableName = messageTableName;
        this.chatUsername = chatUsername;
        this.chatUserId = chatUserId;
        this.room = room;
        this.lastMessage = lastMessage;
        this.unreadSmgCount = unreadSmgCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", messageTableName='" + messageTableName + '\'' +
                ", chatUsername='" + chatUsername + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", room='" + room + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", unreadSmgCount=" + unreadSmgCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return chatId == chat.chatId && unreadSmgCount == chat.unreadSmgCount && Objects.equals(messageTableName, chat.messageTableName) && Objects.equals(chatUsername, chat.chatUsername) && Objects.equals(chatUserId, chat.chatUserId) && Objects.equals(room, chat.room) && Objects.equals(lastMessage, chat.lastMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, messageTableName, chatUsername, chatUserId, room, lastMessage, unreadSmgCount);
    }
}
