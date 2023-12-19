package com.x64technology.linex.screens;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.x64technology.linex.R;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityNewContactBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class NewContact extends AppCompatActivity {
    ActivityNewContactBinding newContactBinding;
    ChatViewModel chatViewModel;
    ContactViewModel contactViewModel;
    DBService dbService;
    UserPreference userPreference;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newContactBinding = ActivityNewContactBinding.inflate(getLayoutInflater());
        setContentView(newContactBinding.getRoot());

        initVars();

        setCallBacks();
    }

    private void initVars() {
        userPreference = new UserPreference(this);

        socket = SocketManager.socket;

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        dbService = new DBService(this);
    }

    private void setCallBacks() {
        newContactBinding.requestBtn.setOnClickListener(view -> {
            String userId_code = newContactBinding.usernameInp.getEditableText().toString();
            // userId_code = EnDecoder.DecodeUserId(Integer.parseInt(userId_code));



            Contact contact = new Contact(Constants.STR_UNKNOWN, userId_code, Constants.STR_UNKNOWN, Constants.REQUEST_SENT);
            contactViewModel.insert(contact);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("to", userId_code);
                jsonObject.put("from", userPreference.userPref.getString("username", ""));
                jsonObject.put("pic", userPreference.userPref.getString("username", ""));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            socket.emit(getString(R.string.event_contact_request), jsonObject);
            Toast.makeText(NewContact.this, "contact request sent", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}