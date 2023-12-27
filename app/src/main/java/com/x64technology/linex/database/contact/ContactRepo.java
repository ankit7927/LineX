package com.x64technology.linex.database.contact;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.x64technology.linex.models.Contact;

import java.util.List;

public class ContactRepo {
    private final ContactDAO contactDAO;

    public ContactRepo(Application application) {
       contactDAO = ContactDatabase.getInstance(application).chatDAO();
    }

    public List<Contact> getAllContacts() {
        return contactDAO.getAllContacts();
    }

    public List<Contact> getTypedContacts(String type) {
        return contactDAO.getTypedContacts(type);
    }

    public void insert(Contact contact) {
        contactDAO.insert(contact);
    }

    void update(Contact contact) {
        contactDAO.update(contact);
    }

    void delete(Contact contact) {
        contactDAO.delete(contact);
    }

    Contact getContactById(String id) {
        return contactDAO.getContactById(id);
    }

    Contact getContactByUserId(String userId) {
        return contactDAO.getContactByUserId(userId);
    }
}
