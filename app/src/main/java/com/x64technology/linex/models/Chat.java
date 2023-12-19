package com.x64technology.linex.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "chats")
public class Chat implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String userid;
    public String profilePic;
    public String lastMsg;
    public String lastMsgTime;
    public int unreadCount;

    public Chat(String name, String userid, String profilePic, String lastMsg, String lastMsgTime, int unreadCount) {
        this.name = name;
        this.userid = userid;
        this.profilePic = profilePic;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.unreadCount = unreadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return id == chat.id && unreadCount == chat.unreadCount && Objects.equals(name, chat.name) && Objects.equals(userid, chat.userid) && Objects.equals(profilePic, chat.profilePic) && Objects.equals(lastMsg, chat.lastMsg) && Objects.equals(lastMsgTime, chat.lastMsgTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userid, profilePic, lastMsg, lastMsgTime, unreadCount);
    }
}
