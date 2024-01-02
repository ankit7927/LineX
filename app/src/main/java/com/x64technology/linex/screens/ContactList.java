package com.x64technology.linex.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.x64technology.linex.R;
import com.x64technology.linex.adapters.ContactAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityContactListBinding;
import com.x64technology.linex.interfaces.ContactProfile;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.services.AuthManager;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class ContactList extends AppCompatActivity implements ContactProfile {

    ActivityContactListBinding contactListBinding;
    AuthManager authManager;
    DBService dbService;
    CognitoUser cognitoUser;
    LinearProgressIndicator progressBar;
    Map<String, String> userData;
    ContactViewModel contactViewModel;
    ChatViewModel chatViewModel;
    Intent intent;
    ContactAdapter contactAdapter;
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
        authManager = new AuthManager(this);
        cognitoUser = authManager.getUser();

        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);
        contactListBinding.appbar.addView(progressBar, 0);

        getUserData();

        if (intent.hasExtra("new contact")) {
            contactListBinding.toolbar.setTitle("Add Contact");
        } else {
            contactListBinding.layUserid.setVisibility(View.GONE);
            contactListBinding.btnSend.setVisibility(View.GONE);
        }

        dbService = new DBService(this);

        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        contactAdapter = new ContactAdapter(this);

        contactListBinding.contactRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactListBinding.contactRecycler.setAdapter(contactAdapter);

        socket = SocketManager.socket;

        String[] contact_types_Array = getResources().getStringArray(R.array.contact_types);
        spinnerAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, contact_types_Array);
        contactListBinding.layFilter.setAdapter(spinnerAdapter);
    }

    private void setCallbacks() {
        contactListBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        contactViewModel.getAllContacts().observe(this, contacts -> contactAdapter.setContacts(contacts));

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
                jsonObject.put(Constants.RECEIVER, userId_code);
                jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                jsonObject.put(Constants.STR_NAME, userData.get("name"));
                jsonObject.put(Constants.STR_DPLINK, userData.get("picture"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            socket.emit(getString(R.string.event_contact_request), jsonObject);
            Toast.makeText(ContactList.this, "contact request sent", Toast.LENGTH_SHORT).show();
            contactListBinding.inpUserid.getEditableText().clear();
        });

//        contactListBinding.layFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String type = (String) adapterView.getItemAtPosition(i);
//
//                if (type.equals("All"))
//                    contactViewModel.getAllContacts().observe(ContactList.this, (Observer<List<Contact>>) contacts -> contactAdapter.setContacts(contacts));
//                else
//                    contactAdapter.setContacts(contactViewModel.getTypedContacts(type));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    @Override
    public void onContactClicked(Contact contact) {
        intent = new Intent(this, Profile.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    @Override
    public void onRequestAcceptClicked(Contact contact) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.RECEIVER, contact.userId);
            jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
            jsonObject.put(Constants.STR_NAME, userData.get("name"));
            jsonObject.put(Constants.STR_DPLINK, userData.get("picture"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socket.emit(Constants.EVENT_REQUEST_ACCEPTED, jsonObject);

        contact.setReqType(Constants.REQUEST_ACCEPTED);
        contactViewModel.update(contact);
        chatViewModel.addNewChat(new Chat(contact.name, contact.userId, contact.userDp, "", "", 0));
        dbService.newChat(contact.userId);
        finish(); // TODO contact is not updating when request is accepted fix it
    }

    @Override
    public void onRequestRejectClicked(Contact contact) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.RECEIVER, contact.userId);
            jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socket.emit(Constants.EVENT_REQUEST_REJECTED, jsonObject);
        contactViewModel.delete(contact);
    }

    @Override
    public void onRequestCancelClicked(Contact contact) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.RECEIVER, contact.userId);
            jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socket.emit(Constants.EVENT_REQUEST_CANCELED, jsonObject);
        contactViewModel.delete(contact);
    }

    private void getUserData() {
        cognitoUser.getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                contactListBinding.appbar.removeView(progressBar);
                userData = cognitoUserDetails.getAttributes().getAttributes();
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }
}