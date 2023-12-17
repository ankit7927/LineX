package com.x64technology.linex.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.x64technology.linex.adapters.MessageAdapter;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityChatBinding;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatScreen extends AppCompatActivity {
    ActivityChatBinding chatBinding;
    MessageAdapter messageAdapter;
    DBService dbService;
    Chat chat;
    List<Message> tempMess = new ArrayList<>();
    Intent intent;
    SimpleDateFormat simpleDateFormat;


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

        messageAdapter = new MessageAdapter(this, dbService.getRangedChat(chat.messageTableName));


        chatBinding.msgRecycler.setItemAnimator(null);
        chatBinding.msgRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.msgRecycler.setAdapter(messageAdapter);
        chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);

        simpleDateFormat = new SimpleDateFormat("h:mm a");
    }


    private void setCallbacks() {
        chatBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        // add logo to toolbar

        chatBinding.sendBtn.setOnClickListener(view -> {
            String msg = chatBinding.msgBox.getEditableText().toString();
            String date = simpleDateFormat.format(Calendar.getInstance().getTime());

            Message message = new Message("test", "temp", msg, date);
            new ArrayList<Message>().add(message);

            tempMess.add(message);
            messageAdapter.setMessages(tempMess);

            chatBinding.msgBox.getEditableText().clear();
            chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
            tempMess.clear();

            dbService.insertMsg(chat.messageTableName, message);
            // emit on socket
        });
    }
}