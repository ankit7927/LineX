package com.x64technology.linex.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;


public class Message implements Serializable {
    public int id;
    public String to;
    public String from;
    public String content;
    public String time;

    public Message(String to, String from, String content, String time) {
        this.to = to;
        this.from = from;
        this.content = content;
        this.time = time;
    }

    public Message(int id, String to, String from, String content, String time) {
        this.id = id;
        this.to = to;
        this.from = from;
        this.content = content;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(to, message.to) && Objects.equals(from, message.from) && Objects.equals(content, message.content) && Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, to, from, content, time);
    }
}
