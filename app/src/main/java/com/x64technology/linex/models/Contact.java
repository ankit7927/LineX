package com.x64technology.linex.models;

import java.io.Serializable;
import java.util.Objects;

public class Contact implements Serializable {
    public int contactId;
    public String name;
    public String username;
    public String userId;
    public String userDp;
    public String reqType;

    public Contact(int contactId, String name, String username, String userId, String userDp, String reqType) {
        this.contactId = contactId;
        this.name = name;
        this.username = username;
        this.userId = userId;
        this.userDp = userDp;
        this.reqType = reqType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return contactId == contact.contactId && Objects.equals(name, contact.name) && Objects.equals(username, contact.username) && Objects.equals(userId, contact.userId) && Objects.equals(userDp, contact.userDp) && Objects.equals(reqType, contact.reqType);
    }
}
