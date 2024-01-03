package com.x64technology.linex.screens;

import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.x64technology.linex.adapters.MessageAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityChatBinding;
import com.x64technology.linex.interfaces.ChatInterFace;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Message;
import com.x64technology.linex.services.AppPreference;
import com.x64technology.linex.services.AuthManager;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.utils.Converter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import io.socket.client.Socket;

public class ChatScreen extends AppCompatActivity implements ChatInterFace {
    public List<Message> messageList;
    ActivityChatBinding chatBinding;
    AuthManager authManager;
    CognitoUser cognitoUser;
    MessageAdapter messageAdapter;
    ChatViewModel chatViewModel;
    LinearLayoutManager msgLayoutMgr;
    DBService dbService;
    Chat chat;
    AppPreference appPreference;
    JSONObject jsonObject;
    Intent intent;
    Socket socket;
    Calendar calendar;
    Message message;
    int pageNo = 1;


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
        authManager = new AuthManager(this);
        cognitoUser = authManager.getUser();

        dbService = new DBService(this);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        messageList = dbService.getRangedMessages(chat.userid, pageNo);
        messageAdapter = new MessageAdapter(this);

        chatBinding.msgRecycler.setAdapter(messageAdapter);
        msgLayoutMgr = new LinearLayoutManager(this);
        chatBinding.msgRecycler.setLayoutManager(msgLayoutMgr);
        chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);

        chatBinding.toolbar.setTitle(chat.name);
        chatBinding.toolbar.setSubtitle(chat.userid);

        appPreference = new AppPreference(this);
        calendar = Calendar.getInstance();


    }


    private void setCallbacks() {
        chatBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        // TODO add logo to toolbar

        chatBinding.msgRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstItem = msgLayoutMgr.findFirstVisibleItemPosition();

                if (msgLayoutMgr.getChildCount() + firstItem >= msgLayoutMgr.getItemCount() - 2 && firstItem >= 0) {
                    pageNo++;
                    List<Message> messages = dbService.getRangedMessages(chat.userid, pageNo);

                    messageList.addAll(0, messages);
                    messageAdapter.notifyItemRangeInserted(0, messages.size());
                }
            }
        });


        chatBinding.btnMsgSend.setOnClickListener(view -> {
            calendar.setTime(new Date());
            message = new Message(chat.userid, cognitoUser.getUserId(), chatBinding.msgBox.getEditableText().toString().trim(),
                    (int) calendar.getTimeInMillis(), true);

            chatBinding.msgBox.getEditableText().clear();

            messageList.add(message);
            messageAdapter.notifyItemInserted(messageList.size() -1);

            chatBinding.msgRecycler.scrollToPosition(messageList.size() -1);

            new Thread(() -> {
                dbService.insertMsg(chat.userid, message);

                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.CONTENT, message.content);
                    jsonObject.put(Constants.TIMESTAMP, message.timestamp);
                    jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                    jsonObject.put(Constants.RECEIVER, chat.userid);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                socket.emit(Constants.EVENT_MESSAGE, jsonObject);
                chat.lastMsg = message.content;
                chat.lastMsgTime = Converter.MillisToTime(message.timestamp);
            }).start();
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
    protected void onStop() {
        super.onStop();

        SocketManager.chatInterFace = null;
        appPreference.removeActiveUser();

        chat.unreadCount=0;
        chatViewModel.updateChat(chat);
    }

    @Override
    public void onIncomingMessageActive(Message message1) {
        chat.lastMsg = message1.content;
        chat.lastMsgTime = Converter.MillisToTime(message1.timestamp);
        dbService.insertMsg(message1.sender, message1);

        runOnUiThread(() -> {
            messageList.add(message1);
            messageAdapter.notifyItemInserted(messageList.size() -1);

            chatBinding.msgRecycler.scrollToPosition(messageList.size() -1);
        });
    }
}