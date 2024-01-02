package com.x64technology.linex.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "contacts")
public class Contact implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String userId;
    public String userDp;
    public String reqType;

    public Contact() {}

    public Contact(int id, String name, String userId, String userDp, String reqType) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.userDp = userDp;
        this.reqType = reqType;
    }

    public Contact(String name, String userId, String userDp, String reqType) {
        this.name = name;
        this.userId = userId;
        this.userDp = userDp;
        this.reqType = reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id == contact.id && Objects.equals(name, contact.name) && Objects.equals(userId, contact.userId) && Objects.equals(userDp, contact.userDp) && Objects.equals(reqType, contact.reqType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userId, userDp, reqType);
    }
}
