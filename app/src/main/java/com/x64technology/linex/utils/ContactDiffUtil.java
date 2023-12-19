package com.x64technology.linex.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.x64technology.linex.models.Contact;

import java.util.List;

public class ContactDiffUtil extends DiffUtil.Callback {
    private final List<Contact> oldContacts;
    private final List<Contact> newContacts;

    public ContactDiffUtil(List<Contact> oldContacts, List<Contact> newContacts) {
        this.oldContacts = oldContacts;
        this.newContacts = newContacts;
    }

    @Override
    public int getOldListSize() {
        return oldContacts.size();
    }

    @Override
    public int getNewListSize() {
        return newContacts.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldContacts.get(oldItemPosition).id == newContacts.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Contact oldContact = oldContacts.get(oldItemPosition);
        Contact newContact = newContacts.get(newItemPosition);

        return oldContact.equals(newContact);
    }
}
