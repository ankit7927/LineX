package com.x64technology.linex;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.x64technology.linex.adapters.ChatsAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.databinding.ActivityMainBinding;
import com.x64technology.linex.screens.Auth;
import com.x64technology.linex.screens.ContactList;
import com.x64technology.linex.screens.NewContact;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.services.UserPreference;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    ChatViewModel chatViewModel;
    ChatsAdapter chatsAdapter;
    ActivityMainBinding mainBinding;
    Socket socket;
    SocketManager socketManager;
    UserPreference userPreference;
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

        socketManager = new SocketManager(this);
        socket = socketManager.initSocket(userPreference.userPref.getString("token", ""));
        socketManager.addSocketListeners(socket);
        socket.connect();

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatsAdapter = new ChatsAdapter(this);

        mainBinding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.chatRecycler.setAdapter(chatsAdapter);
    }

    private void setCallbacks() {
        mainBinding.searchBar.setNavigationOnClickListener(view -> mainBinding.drawerLayout.open());

        mainBinding.floating.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, NewContact.class)));

        chatViewModel.getChats().observe(this, chats -> chatsAdapter.setChats(chats));

        mainBinding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_contacts) {
                startActivity(new Intent(MainActivity.this, ContactList.class));
            }
            mainBinding.drawerLayout.close();
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (new UserPreference(this)
                .userPref.getString(getString(R.string.str_username), "").equals("")) {
            startActivity(new Intent(this, Auth.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket == null) return;
        socket.disconnect();
        socketManager.removeSocketListeners(socket);
    }
}