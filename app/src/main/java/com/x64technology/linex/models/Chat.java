package com.x64technology.linex.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "chats")
public class Chat implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String table_name;
    public String username;
    public String user_id;
    public String room;
    public String last_msg;

    public int getId() {
        return id;
    }

    public String getTable_name() {
        return table_name;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRoom() {
        return room;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", table_name='" + table_name + '\'' +
                ", username='" + username + '\'' +
                ", user_id='" + user_id + '\'' +
                ", room='" + room + '\'' +
                ", last_msg='" + last_msg + '\'' +
                '}';
    }
}
