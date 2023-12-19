package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.x64technology.linex.R;
import com.x64technology.linex.adapters.ContactAdapter;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityContactListBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.utils.ContactProfile;

import java.util.List;

public class ContactList extends AppCompatActivity implements ContactProfile {

    ActivityContactListBinding contactListBinding;
    ContactViewModel contactViewModel;
    DBService dbService;
    Intent intent;
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactListBinding = ActivityContactListBinding.inflate(getLayoutInflater());
        setContentView(contactListBinding.getRoot());

        initVars();

        setCallbacks();

    }

    private void initVars() {
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        dbService = new DBService(this);
        contactAdapter = new ContactAdapter(this, this);

        //contactAdapter.setContacts(dbService.getContacts());

        contactListBinding.contactRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactListBinding.contactRecycler.setAdapter(contactAdapter);
    }

    private void setCallbacks() {
        contactListBinding.searchBar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        contactViewModel.getContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                contactAdapter.setContacts(contacts);
            }
        });
    }

    @Override
    public void onContactClicked(Contact contact) {
        intent = new Intent(this, Profile.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }
}