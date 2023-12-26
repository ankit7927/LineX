package com.x64technology.linex.models;

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

    public Message(int id, String receiver, String sender, String content, String time, boolean isMine1) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.isMine = isMine1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && isMine == message.isMine && Objects.equals(receiver, message.receiver) && Objects.equals(sender, message.sender) && Objects.equals(content, message.content) && Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, sender, content, time, isMine);
    }
}
