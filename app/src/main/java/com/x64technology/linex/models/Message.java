package com.x64technology.linex.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;


public class Message implements Serializable {
    public int id;
    public String receiver;
    public String sender;
    public String content;
    public String time;
    public boolean isMine = false;

    public Message() {
    }

    public Message(String receiver, String sender, String content, String time) {
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    public Message(int id, String receiver, String sender, String content, String time) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(receiver, message.receiver) && Objects.equals(sender, message.sender) && Objects.equals(content, message.content) && Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, sender, content, time);
    }
}
