package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.x64technology.linex.R;
import com.x64technology.linex.databinding.ActivityProfileBinding;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding profileBinding;
    UserPreference userPreference;
    Intent intent;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        initVars();

        setCallbacks();
    }

    private void initVars() {
        userPreference = new UserPreference(this);
        intent = getIntent();

        if (intent.hasExtra("contact")) {
            contact = (Contact) intent.getSerializableExtra("contact");
            if (contact != null) {
                profileBinding.proName.setText(contact.name);
            }
            profileBinding.proMessage.setVisibility(View.GONE);
            profileBinding.proDisconnect.setVisibility(View.GONE);
        } else {
            String name = userPreference.userPref.getString(Constants.STR_NAME, "");
            profileBinding.proName.setText(name);

            profileBinding.proReqAccept.setVisibility(View.GONE);
            profileBinding.proReqReject.setVisibility(View.GONE);
        }
    }

    private void setCallbacks() {
        profileBinding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }
}