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
    ActivityChatBinding chatBinding;
    AuthManager authManager;
    CognitoUser cognitoUser;
    MessageAdapter messageAdapter;
    ChatViewModel chatViewModel;
    LinearLayoutManager msgLayoutMgr;
    DBService dbService;
    LinearProgressIndicator progressBar;
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

        messageAdapter = new MessageAdapter(this);
        messageAdapter.messages = dbService.getRangedMessages(chat.userid, pageNo);

        chatBinding.msgRecycler.setAdapter(messageAdapter);
        msgLayoutMgr = new LinearLayoutManager(this);
        chatBinding.msgRecycler.setLayoutManager(msgLayoutMgr);
        chatBinding.msgRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);

        chatBinding.toolbar.setTitle(chat.name);
        chatBinding.toolbar.setSubtitle(chat.userid);

        appPreference = new AppPreference(this);
        calendar = Calendar.getInstance();

        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);
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

                    messageAdapter.messages.addAll(0, messages);
                    messageAdapter.notifyItemRangeInserted(0, messages.size());
                }
            }
        });


        calendar.setTime(new Date());

        chatBinding.btnMsgSend.setOnClickListener(view -> {
            message = new Message();
            message.receiver = chat.userid;
            message.sender = cognitoUser.getUserId();
            message.content = chatBinding.msgBox.getEditableText().toString();
            message.timestamp =(int) System.currentTimeMillis();
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
                jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                jsonObject.put(Constants.RECEIVER, chat.userid);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            socket.emit(Constants.EVENT_MESSAGE, jsonObject);
            chat.lastMsg = message.content;
            chat.lastMsgTime = Converter.MillisToTime(message.timestamp);
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
            messageAdapter.messages.add(message1);
            int x = messageAdapter.messages.size() -1;
            messageAdapter.notifyItemInserted(x);

            chatBinding.msgRecycler.scrollToPosition(x);
        });
    }
}