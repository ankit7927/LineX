package com.x64technology.linex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.x64technology.linex.adapters.ChatsAdapter;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.databinding.ActivityMainBinding;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.screens.Auth;
import com.x64technology.linex.screens.NewChat;
import com.x64technology.linex.services.PreferenceManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ChatViewModel chatViewModel;
    ChatsAdapter chatsAdapter;
    ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        initVars();

        setCallbacks();
    }

    private void initVars() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatsAdapter = new ChatsAdapter(this);

        mainBinding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.chatRecycler.setAdapter(chatsAdapter);
    }

    private void setCallbacks() {
        mainBinding.searchBar.setNavigationOnClickListener(view -> mainBinding.drawerLayout.open());

        mainBinding.floating.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, NewChat.class)));

        chatViewModel.getChats().observe(this, chats -> {
            chatsAdapter.setChats(chats);
            System.out.println(chats.size());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (new PreferenceManager(this).getUsername().equals("")) {
//            startActivity(new Intent(this, Auth.class));
//            finish();
        }
    }
}