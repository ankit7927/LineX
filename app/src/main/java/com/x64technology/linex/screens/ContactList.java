package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.x64technology.linex.R;
import com.x64technology.linex.adapters.ContactAdapter;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityContactListBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.utils.ContactProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;

public class ContactList extends AppCompatActivity implements ContactProfile {

    ActivityContactListBinding contactListBinding;
    ContactViewModel contactViewModel;
    Intent intent;
    ContactAdapter contactAdapter;
    UserPreference userPreference;
    Socket socket;
    SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactListBinding = ActivityContactListBinding.inflate(getLayoutInflater());
        setContentView(contactListBinding.getRoot());

        initVars();

        setCallbacks();

    }

    private void initVars() {
        intent = getIntent();
        if (intent.hasExtra("new contact")) {
            contactListBinding.toolbar.setTitle("Add Contact");
        } else {
            contactListBinding.layUserid.setVisibility(View.GONE);
            contactListBinding.btnSend.setVisibility(View.GONE);
        }
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        contactAdapter = new ContactAdapter(this, this);

        contactListBinding.contactRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactListBinding.contactRecycler.setAdapter(contactAdapter);

        userPreference = new UserPreference(this);
        socket = SocketManager.socket;

        String[] contact_types_Array = getResources().getStringArray(R.array.contact_types);
        spinnerAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, contact_types_Array);
        contactListBinding.layFilter.setAdapter(spinnerAdapter);
    }

    private void setCallbacks() {
        contactListBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        contactViewModel.getContacts().observe(this, contacts -> contactAdapter.setContacts(contacts));

        contactListBinding.btnSend.setOnClickListener(view -> {
            String userId_code = contactListBinding.inpUserid.getEditableText().toString();

            Contact contactByUserId = contactViewModel.getContactByUserId(userId_code);
            if (contactByUserId != null) {
                Snackbar.make(ContactList.this, contactListBinding.layUserid, "Contact Request already sent to "+contactByUserId.userId, Snackbar.LENGTH_SHORT).show();
                return;
            }

            Contact contact = new Contact(Constants.STR_UNKNOWN, userId_code, Constants.STR_UNKNOWN, Constants.REQUEST_SENT);
            contactViewModel.insert(contact);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.TO, userId_code);
                jsonObject.put(Constants.FROM, userPreference.userPref.getString(Constants.STR_USERID, ""));
                jsonObject.put(Constants.STR_NAME, userPreference.userPref.getString(Constants.STR_NAME, ""));
                jsonObject.put(Constants.STR_DPLINK, userPreference.userPref.getString(Constants.STR_DPLINK, "link from fb user"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            socket.emit(getString(R.string.event_contact_request), jsonObject);
            Toast.makeText(ContactList.this, "contact request sent", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onContactClicked(Contact contact) {
        intent = new Intent(this, Profile.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }
}