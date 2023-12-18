package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.x64technology.linex.R;
import com.x64technology.linex.adapters.ContactAdapter;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityContactListBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.utils.ContactProfile;

public class ContactList extends AppCompatActivity implements ContactProfile {

    ActivityContactListBinding contactListBinding;
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
        dbService = new DBService(this);
        contactAdapter = new ContactAdapter(this, this, dbService.getContacts());

        //contactAdapter.setContacts(dbService.getContacts());

        contactListBinding.contactRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactListBinding.contactRecycler.setAdapter(contactAdapter);
    }

    private void setCallbacks() {
        contactListBinding.searchBar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public void onContactClicked(Contact contact) {
        intent = new Intent(this, Profile.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }
}