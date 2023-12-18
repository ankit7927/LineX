package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.databinding.ActivityNewChatBinding;
import com.x64technology.linex.services.AppPreference;
import com.x64technology.linex.services.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class NewContact extends AppCompatActivity {
    ActivityNewChatBinding newChatBinding;
    ChatViewModel chatViewModel;
    DBService dbService;
    UserPreference userPreference;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newChatBinding = ActivityNewChatBinding.inflate(getLayoutInflater());
        setContentView(newChatBinding.getRoot());

        initVars();

        setCallBacks();
    }

    private void initVars() {
        userPreference = new UserPreference(this);

        socket = SocketManager.socket;

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        dbService = new DBService(this);
    }

    private void setCallBacks() {
        newChatBinding.requestBtn.setOnClickListener(view -> {
            String username = newChatBinding.usernameInp.getEditableText().toString();

            dbService.insertContact(Constants.STR_UNKNOWN, username, Constants.STR_UNKNOWN, Constants.STR_UNKNOWN, Constants.REQUEST_SENT);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("to", username);
                jsonObject.put("senderUsername", userPreference.userPref.getString("username", ""));
                jsonObject.put("senderDpLink", "somwlink");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            socket.emit("contact request", jsonObject);
            Toast.makeText(NewContact.this, "contact request sent", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}