package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.noroom.DBService;
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
        newChatBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = newChatBinding.usernameEdit.getText().toString();
                String userid = newChatBinding.useridEdit.getText().toString();
                String lastmsg = newChatBinding.lastmsgEdit.getText().toString();

                Chat chat = new Chat();
                chat.setUsername(username);
                chat.setRoom("myrrom");
                chat.setLast_msg(lastmsg);
                chat.setTable_name(username);
                chat.setUser_id(userid);

                chatViewModel.addNewChat(chat);
                dbService.newChat(username);

                Toast.makeText(NewChat.this, "chat added", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}