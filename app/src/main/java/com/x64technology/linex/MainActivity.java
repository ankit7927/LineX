package com.x64technology.linex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.x64technology.linex.adapters.ChatsAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityMainBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.screens.Auth;
import com.x64technology.linex.screens.ContactList;
import com.x64technology.linex.screens.Profile;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.utils.MainInterFace;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity implements MainInterFace {
    ChatViewModel chatViewModel;
    ChatsAdapter chatsAdapter;
    ActivityMainBinding mainBinding;
    ContactViewModel contactViewModel;
    Socket socket;
    UserPreference userPreference;
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
        userPreference = new UserPreference(this);

        socket = SocketManager.initSocket(this, userPreference.userPref.getString("token", ""));
        SocketManager.addSocketListeners();
        socket.connect();
        SocketManager.mainInterFace = this;

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
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
            if (item.getItemId() == R.id.menu_contacts) {
                intent = new Intent(MainActivity.this, ContactList.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.menu_profile) {
                intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
            mainBinding.drawerLayout.close();
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (new UserPreference(this)
                .userPref.getString(Constants.STR_USERID, "").equals("")) {
            startActivity(new Intent(this, Auth.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        SocketManager.removeSocketListeners();
    }


    @Override
    public void onSocketConnect() {
//        runOnUiThread(() -> {
//
//        });
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
        contactViewModel.delete(contactByUserId);
    }
}