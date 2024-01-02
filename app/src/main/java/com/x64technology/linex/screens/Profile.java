package com.x64technology.linex.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.x64technology.linex.database.chat.ChatViewModel;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.databinding.ActivityProfileBinding;
import com.x64technology.linex.models.Chat;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.services.AuthManager;
import com.x64technology.linex.services.SocketManager;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import io.socket.client.Socket;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding profileBinding;
    AuthManager authManager;
    LinearProgressIndicator progressBar;
    CognitoUser cognitoUser;
    Map<String, String> userData;
    Intent intent;
    ContactViewModel contactViewModel;
    ChatViewModel chatViewModel;
    Contact contact;
    Socket socket;
    DBService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        initVars();

        setCallbacks();
    }

    private void initVars() {
        intent = getIntent();

        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);
        profileBinding.appbar.addView(progressBar, 0);

        checkOther();

        authManager = new AuthManager(this);
        cognitoUser = authManager.getUser();

        setUserData();

        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        socket = SocketManager.socket;
        dbService = new DBService(this);
    }

    private void setCallbacks() {
        profileBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        profileBinding.proReqAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                if (contact.reqType.equals(Constants.REQUEST_SENT)) {
                    try {
                        jsonObject.put(Constants.RECEIVER, contact.userId);
                        jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    socket.emit(Constants.EVENT_REQUEST_CANCELED, jsonObject);
                    contactViewModel.delete(contact);
                    finish();
                } else {
                    try {
                        jsonObject.put(Constants.RECEIVER, contact.userId);
                        jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                        jsonObject.put(Constants.STR_NAME, userData.get("name"));
                        jsonObject.put(Constants.STR_DPLINK, userData.get("picture"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    socket.emit(Constants.EVENT_REQUEST_ACCEPTED, jsonObject);
                    contact.reqType = Constants.REQUEST_ACCEPTED;
                    contactViewModel.update(contact);
                    chatViewModel.addNewChat(new Chat(contact.name, contact.userId, contact.userDp, "", "", 0));
                    dbService.newChat(contact.userId);
                    finish();
                }
            }
        });

        profileBinding.proReqReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.RECEIVER, contact.userId);
                    jsonObject.put(Constants.SENDER, cognitoUser.getUserId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                socket.emit(Constants.EVENT_REQUEST_REJECTED, jsonObject);
                contactViewModel.delete(contact);
                finish();
            }
        });

        profileBinding.proMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat chatByUserId = chatViewModel.getChatByUserId(contact.userId);
                if (chatByUserId == null) {
                    chatByUserId = new Chat(contact.name, contact.userId, contact.userDp, "", "", 0);
                    chatViewModel.addNewChat(chatByUserId);
                }
                intent = new Intent(Profile.this, ChatScreen.class);
                intent.putExtra("chat", chatByUserId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUserData() {
        cognitoUser.getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                profileBinding.appbar.removeView(progressBar);
                userData = cognitoUserDetails.getAttributes().getAttributes();

                profileBinding.proName.setText(userData.get("name"));
                profileBinding.proContactCode.setText(cognitoUser.getUserId());
                // TODO set profile image
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }

    private void checkOther() {
        if (intent.hasExtra("contact")) {
            contact = (Contact) intent.getSerializableExtra("contact");
            profileBinding.toolbar.setTitle("Contact info");
            profileBinding.proName.setText(contact.name);
            profileBinding.proContactCode.setText(String.format(Locale.getDefault(), "cc: %s", contact.userId));
            profileBinding.proInfo.setText(contact.reqType);

            switch (contact.reqType) {
                case Constants.REQUEST_ACCEPTED:
                    profileBinding.proMessage.setVisibility(View.VISIBLE);
                    profileBinding.proDisconnect.setVisibility(View.VISIBLE);
                    break;
                case Constants.REQUEST_REJECTED:
                    profileBinding.proMessage.setText("Retry Connection");
                    profileBinding.proMessage.setVisibility(View.VISIBLE);
                    break;
                case Constants.REQUEST_RECEIVED:
                    profileBinding.proReqAccept.setVisibility(View.VISIBLE);
                    profileBinding.proReqReject.setVisibility(View.VISIBLE);
                    break;
                case Constants.REQUEST_SENT:
                    profileBinding.proReqAccept.setText("Delete Request");
                    profileBinding.proReqAccept.setVisibility(View.VISIBLE);
            }
        }
    }
}