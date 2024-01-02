package com.x64technology.linex;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.x64technology.linex.adapters.ChatsAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityMainBinding;
import com.x64technology.linex.interfaces.MainInterFace;
import com.x64technology.linex.interfaces.MainToChat;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.models.Message;
import com.x64technology.linex.screens.Auth;
import com.x64technology.linex.screens.ChatScreen;
import com.x64technology.linex.screens.ContactList;
import com.x64technology.linex.screens.Profile;
import com.x64technology.linex.services.AppPreference;
import com.x64technology.linex.services.AuthManager;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.utils.Converter;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity implements MainInterFace, MainToChat {
    AuthManager authManager;
    ChatViewModel chatViewModel;
    ChatsAdapter chatsAdapter;
    ActivityMainBinding mainBinding;
    LinearProgressIndicator progressBar;
    ContactViewModel contactViewModel;
    DBService dbService;
    Socket socket;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        initVars();

        setCallbacks();
    }

    private void initVars() {
        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);
        progressBar.setPadding(16, 4, 16, 0);
        mainBinding.appbar.addView(progressBar, 0);

        initAuth();
        SocketManager.mainInterFace = this;

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        dbService = new DBService(this);

        chatsAdapter = new ChatsAdapter(this);

        mainBinding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.chatRecycler.setAdapter(chatsAdapter);
    }

    private void setCallbacks() {

        mainBinding.searchBar.setNavigationOnClickListener(view -> mainBinding.drawerLayout.open());

        mainBinding.floating.setOnClickListener(view -> {
            intent = new Intent(MainActivity.this, ContactList.class);
            intent.putExtra("new contact", true);
            startActivity(intent);
        });

        chatViewModel.getChats().observe(this, chats -> chatsAdapter.setChats(chats));

        mainBinding.navigationView.setNavigationItemSelectedListener(item -> {
            mainBinding.drawerLayout.close();
            if (item.getItemId() == R.id.menu_contacts) {
                intent = new Intent(MainActivity.this, ContactList.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.menu_profile) {
                intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
            return false;
        });
    }

    protected void initAuth() {
        authManager = new AuthManager(this);

        authManager.userLoggedIn(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                String jwtToken = userSession.getIdToken().getJWTToken();
                socket = SocketManager.initSocket(MainActivity.this, jwtToken);
                SocketManager.addSocketListeners();
                socket.connect();

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {

            }

            @Override
            public void onFailure(Exception exception) {
                startActivity(new Intent(MainActivity.this, Auth.class));
                finish();
            }
        });


        new AppPreference(this).removeActiveUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
            SocketManager.removeSocketListeners();
        }
    }


    @Override
    public void onSocketConnect() {
        runOnUiThread(() -> {
            mainBinding.appbar.removeView(progressBar);
        });
    }

    @Override
    public void onSocketDisconnect() {
        runOnUiThread(() -> {
            mainBinding.appbar.removeView(progressBar);
            mainBinding.appbar.addView(progressBar, 0);
        });
    }

    @Override
    public void onSocketConnectError() {

    }

    @Override
    public void onConnectionReq(Contact contact) {
        contactViewModel.insert(contact);
    }

    @Override
    public void onReqAccept(String userid, String name, String dplink) {
        Contact contact = contactViewModel.getContactByUserId(userid);
        contact.reqType = Constants.REQUEST_ACCEPTED;
        contact.name = name;
        contact.userDp = dplink;
        contactViewModel.update(contact);

        chatViewModel.addNewChat(new Chat(name, userid, dplink, "", "", 0));
        new DBService(this).newChat(userid);
    }

    @Override
    public void onReqReject(String userid) {
        Contact contactByUserId = contactViewModel.getContactByUserId(userid);
        contactByUserId.reqType = Constants.REQUEST_REJECTED;
        contactViewModel.update(contactByUserId);
        // TODO notify for this event
    }

    @Override
    public void onReqCancel(String userid) {
        Contact contactByUserId = contactViewModel.getContactByUserId(userid);
        if (contactByUserId != null)
            contactViewModel.delete(contactByUserId);
    }

    @Override
    public void onIncomingMessage(Message message) {
        dbService.insertMsg(message.sender, message);

        Chat chatByUserId = chatViewModel.getChatByUserId(message.sender);
        chatByUserId.lastMsg = message.content;
        chatByUserId.lastMsgTime = Converter.MillisToTime(message.timestamp);
        chatByUserId.unreadCount++;

        chatViewModel.updateChat(chatByUserId);
    }

    @Override
    public void onChatClicked(Chat chat) {
        intent = new Intent(this, ChatScreen.class);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }
}