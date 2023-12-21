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
import com.x64technology.linex.services.AppPreference;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.ChatInterFace;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.socket.client.Socket;

public class ChatScreen extends AppCompatActivity implements ChatInterFace {
    ActivityChatBinding chatBinding;
    MessageAdapter messageAdapter;
    DBService dbService;
    Chat chat;
    UserPreference userPreference;
    AppPreference appPreference;
    List<Message> tempMess = new ArrayList<>();
    JSONObject jsonObject;
    Intent intent;
    Socket socket;
    SimpleDateFormat simpleDateFormat;
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
        if (intent.hasExtra("chat")) chat = (Chat) intent.getSerializableExtra("chat");

        chatBinding.toolbar.setTitle(chat.name);

        userPreference = new UserPreference(this);
        appPreference = new AppPreference(this);
        myUserid = userPreference.userPref.getString(Constants.STR_USERID, "");

        dbService = new DBService(this);

        messageAdapter = new MessageAdapter(this, dbService.getRangedMessages(chat.userid));

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

            message = new Message(chat.userid, myUserid, msg, date);
            message.isMine = true;

            tempMess.add(message);
            messageAdapter.setMessages(tempMess);

            chatBinding.msgBox.getEditableText().clear();
            chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
            tempMess.clear();

            dbService.insertMsg(message);

            jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.CONTENT, msg);
                jsonObject.put(Constants.TIME, date);
                jsonObject.put(Constants.SENDER, myUserid);
                jsonObject.put(Constants.RECEIVER, chat.userid);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            socket.emit(Constants.EVENT_MESSAGE, jsonObject);
            // emit on socket
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
    public void incomingMessage(Message message1) {
        runOnUiThread(() -> {
            tempMess.add(message1);
            messageAdapter.setMessages(tempMess);

            dbService.insertMsg(message1);
            chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
            tempMess.clear();
        });
    }
}