package com.x64technology.linex.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.x64technology.linex.adapters.MessageAdapter;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityChatBinding;
import com.x64technology.linex.interfaces.ChatInterFace;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Message;
import com.x64technology.linex.services.AppPreference;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class ChatScreen extends AppCompatActivity implements ChatInterFace {
    ActivityChatBinding chatBinding;
    MessageAdapter messageAdapter;
    DBService dbService;
    Chat chat;
    UserPreference userPreference;
    AppPreference appPreference;
    JSONObject jsonObject;
    Intent intent;
    Socket socket;
    String myUserid;
    Message message;



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
        chat = (Chat) intent.getSerializableExtra("chat");

        dbService = new DBService(this);

        messageAdapter = new MessageAdapter(this);
        messageAdapter.messages = dbService.getRangedMessages(chat.userid);

        chatBinding.msgRecycler.setAdapter(messageAdapter);
        chatBinding.msgRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);

        chatBinding.toolbar.setTitle(chat.name);

        userPreference = new UserPreference(this);
        appPreference = new AppPreference(this);
        myUserid = userPreference.userPref.getString(Constants.STR_USERID, "");
    }


    private void setCallbacks() {
        chatBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        // TODO add logo to toolbar

        chatBinding.textInputLayout.setEndIconOnClickListener(view -> {
            message = new Message();
            message.receiver = chat.userid;
            message.sender = myUserid;
            message.content = chatBinding.msgBox.getEditableText().toString();
            message.timestamp = (int) System.currentTimeMillis();
            message.isMine = true;

            chatBinding.msgBox.getEditableText().clear();

            messageAdapter.messages.add(message);
            int x = messageAdapter.messages.size() -1;
            messageAdapter.notifyItemInserted(x);

            chatBinding.msgRecycler.scrollToPosition(x);

            dbService.insertMsg(chat.userid, message);

            jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.CONTENT, message.content);
                jsonObject.put(Constants.TIMESTAMP, message.timestamp);
                jsonObject.put(Constants.SENDER, myUserid);
                jsonObject.put(Constants.RECEIVER, chat.userid);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            socket.emit(Constants.EVENT_MESSAGE, jsonObject);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        socket = SocketManager.socket;
        SocketManager.chatInterFace = this;
        appPreference.saveActiveUser(chat != null ? chat.userid : "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SocketManager.chatInterFace = null;
        appPreference.removeActiveUser();
    }

    @Override
    public void onIncomingMessageActive(Message message1) {
        runOnUiThread(() -> {
            dbService.insertMsg(message1.sender, message1);
            messageAdapter.messages.add(message1);
            int x = messageAdapter.messages.size() -1;
            messageAdapter.notifyItemInserted(x);

            chatBinding.msgRecycler.scrollToPosition(x);
        });
    }
}