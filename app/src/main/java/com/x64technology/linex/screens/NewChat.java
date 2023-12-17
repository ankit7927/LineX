package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.database.noroom.DBStrings;
import com.x64technology.linex.databinding.ActivityNewChatBinding;
import com.x64technology.linex.models.Chat;

public class NewChat extends AppCompatActivity {
    ActivityNewChatBinding newChatBinding;
    ChatViewModel chatViewModel;
    DBService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newChatBinding = ActivityNewChatBinding.inflate(getLayoutInflater());
        setContentView(newChatBinding.getRoot());

        initVars();

        setCallBacks();
    }

    private void initVars() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        dbService = new DBService(this);
    }

    private void setCallBacks() {
        newChatBinding.requestBtn.setOnClickListener(view -> {
            String username = newChatBinding.usernameInp.getEditableText().toString();

            dbService.insertContact("unknown", username, "unknown", "unknown", DBStrings.REQUEST_SENT);
            Toast.makeText(NewChat.this, "Chat request sent", Toast.LENGTH_SHORT).show();
            finish();
        });
    }


}