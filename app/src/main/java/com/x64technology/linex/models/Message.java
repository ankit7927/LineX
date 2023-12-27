package com.x64technology.linex.models;

import java.io.Serializable;
import java.util.Objects;


public class Message implements Serializable {
    public int id;
    public String receiver;
    public String sender;
    public String content;
    public int timestamp;
    public boolean isMine = false;

    public Message() {
    }

    public Message(int id, String receiver, String sender, String content, int timestamp, boolean isMine1) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.isMine = isMine1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && isMine == message.isMine && Objects.equals(receiver, message.receiver) && Objects.equals(sender, message.sender) && Objects.equals(content, message.content) && Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, sender, content, timestamp, isMine);
    }
}
