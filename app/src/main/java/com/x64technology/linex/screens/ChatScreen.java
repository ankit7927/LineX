package com.x64technology.linex.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.x64technology.linex.adapters.MessageAdapter;
import com.x64technology.linex.database.message.MessageViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityChatBinding;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Message;

import java.util.List;

public class ChatScreen extends AppCompatActivity {
    ActivityChatBinding chatBinding;
    MessageAdapter messageAdapter;
    MessageViewModel messageVIewModel;
    DBService dbService;
    Chat chat;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());

        initVars();

        setCallbacks();
    }

    private void initVars() {
        intent = getIntent();
        if (intent.hasExtra("chat")) chat = (Chat) intent.getSerializableExtra("chat");

        dbService = new DBService(this);
        messageAdapter = new MessageAdapter(this);
        messageVIewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        chatBinding.msgRecycler.setLayoutManager(new LinearLayoutManager(this));

        chatBinding.msgRecycler.setAdapter(messageAdapter);

        messageAdapter.setMessages(dbService.getRangedChat(chat.getUsername()));
    }


    private void setCallbacks() {
        chatBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        // add logo to toolbar

        messageVIewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messageAdapter.setMessages(messages);
                chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        chatBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chatBinding.msgBox.getEditableText().toString();
                Message message = new Message( "test", "temp", msg, "time");

                messageVIewModel.insert(message);
                dbService.insertMsg(chat.username, message);

                chatBinding.msgBox.getEditableText().clear();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageVIewModel.cleanTable();
    }
}